import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.*;

public class ReadXMLSAX {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java ReadXMLSAX <file-path>");
            return;
        }

        String filePath = args[0];
        File xmlFile = new File(filePath);

        if (!xmlFile.exists() || !xmlFile.isFile()) {
            System.out.println("Error: File not found or invalid path: " + filePath);
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the fields you want to display (comma-separated):");
        System.out.println("Available: name, postalZip, region, country, address, list");
        String[] inputFields = scanner.nextLine().trim().split(",");
        Set<String> fieldsToExtract = new HashSet<>();

        for (String field : inputFields) {
            if (!field.trim().isEmpty()) {
                fieldsToExtract.add(field.trim());
            }
        }

        if (fieldsToExtract.isEmpty()) {
            System.out.println("Error: No valid fields entered. Exiting.");
            return;
        }

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            RecordHandler handler = new RecordHandler(fieldsToExtract);
            saxParser.parse(xmlFile, handler);

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




