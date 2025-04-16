import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.*;

public class XMLFieldSelectorSAX {

    // Valid field names
    private static final Set<String> ALLOWED_FIELDS = new HashSet<>(Arrays.asList(
            "name", "postalZip", "region", "country", "address", "list"
    ));

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java XMLFieldSelectorSAX <file-path>");
            return;
        }

        String filePath = args[0];
        File xmlFile = new File(filePath);

        // Check if the file exists and is valid
        if (!xmlFile.exists() || !xmlFile.isFile()) {
            System.out.println("Error: File not found or invalid path: " + filePath);
            return;
        }

        // Collect user input for the fields they want to extract
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the fields you want to display (comma-separated):");
        System.out.println("Available: name, postalZip, region, country, address, list");
        String[] inputFields = scanner.nextLine().trim().split(",");
        Set<String> fieldsToExtract = new HashSet<>();

        Set<String> invalidFields = new HashSet<>();
        for (String field : inputFields) {
            String trimmedField = field.trim();
            if (!trimmedField.isEmpty()) {
                if (ALLOWED_FIELDS.contains(trimmedField)) {
                    fieldsToExtract.add(trimmedField);
                } else {
                    invalidFields.add(trimmedField);
                }
            }
        }

        // Report invalid fields
        if (!invalidFields.isEmpty()) {
            System.out.println("The following fields are invalid or misspelled:");
            for (String invalidField : invalidFields) {
                System.out.println("  - " + invalidField);
            }
        }

        // If no valid fields are entered, exit
        if (fieldsToExtract.isEmpty()) {
            System.out.println("Error: No valid fields entered. Exiting.");
            return;
        }

        try {
            // Initialize SAX parser
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            RecordHandler handler = new RecordHandler(fieldsToExtract);
            saxParser.parse(xmlFile, handler);

            // Output results as JSON format
            System.out.println("[");
            List<Map<String, String>> records = handler.getRecords();

            for (int i = 0; i < records.size(); i++) {
                Map<String, String> record = records.get(i);
                StringBuilder json = new StringBuilder("  {");

                int j = 0;
                for (String field : fieldsToExtract) {
                    String value = record.getOrDefault(field, "").replace("\"", "\\\"");
                    json.append("\"").append(field).append("\": \"").append(value).append("\"");
                    if (j < fieldsToExtract.size() - 1) {
                        json.append(", ");
                    }
                    j++;
                }

                json.append("}");
                System.out.println(json + (i < records.size() - 1 ? "," : ""));
            }

            System.out.println("]");

        } catch (Exception e) {
            System.out.println("Error: Failed to parse XML.");
            e.printStackTrace();
        }
    }

    // SAX handler to process the XML
    static class RecordHandler extends DefaultHandler {
        private final Set<String> fieldsToExtract;
        private final List<Map<String, String>> records = new ArrayList<>();
        private Map<String, String> currentRecord = null;
        private StringBuilder content = null;
        private String currentElement = "";

        public RecordHandler(Set<String> fieldsToExtract) {
            this.fieldsToExtract = fieldsToExtract;
        }

        public List<Map<String, String>> getRecords() {
            return records;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if (qName.equalsIgnoreCase("record")) {
                currentRecord = new HashMap<>();
            } else if (fieldsToExtract.contains(qName)) {
                currentElement = qName;
                content = new StringBuilder();
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            if (content != null) {
                content.append(ch, start, length);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            if (qName.equalsIgnoreCase("record")) {
                if (currentRecord != null) {
                    records.add(currentRecord);
                }
            } else if (qName.equalsIgnoreCase(currentElement)) {
                if (currentRecord != null && fieldsToExtract.contains(currentElement)) {
                    currentRecord.put(currentElement, content.toString().trim());
                }
                content = null;
                currentElement = "";
            }
        }
    }
}







