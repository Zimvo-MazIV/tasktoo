import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.*;

public class XmlFieldSelectorFromJson {

    private static final List<String> ALLOWED_FIELDS = Arrays.asList(
        "name", "postalZip", "region", "country", "address", "list"
    );

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java XmlFieldSelectorFromJson <xml-file-path> <json-file-path>");
            return;
        }

        String xmlFilePath = args[0];
        String jsonFilePath = args[1];

        List<String> selectedFields = loadFieldsFromJson(jsonFilePath);
        if (selectedFields.isEmpty()) {
            System.out.println("No valid fields found in JSON file.");
            return;
        }

        try {
            File xmlFile = new File(xmlFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList records = doc.getElementsByTagName("record");

            for (int i = 0; i < records.getLength(); i++) {
                Node node = records.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    System.out.println("Record " + (i + 1) + ":");
                    for (String field : selectedFields) {
                        String value = getTagValue(field, element);
                        System.out.println("  " + field + ": " + value);
                    }
                    System.out.println();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // A minimal JSON parser to get fields from a very basic file
    private static List<String> loadFieldsFromJson(String jsonFilePath) {
        List<String> fields = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(jsonFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("\"fields\"")) {
                    // Extract the part inside brackets
                    int start = line.indexOf('[');
                    int end = line.indexOf(']');
                    if (start != -1 && end != -1) {
                        String[] items = line.substring(start + 1, end).split(",");
                        for (String item : items) {
                            String field = item.trim().replaceAll("\"", "");
                            if (ALLOWED_FIELDS.contains(field)) {
                                fields.add(field);
                            } else {
                                System.out.println("Ignored invalid field: " + field);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
        }
        return fields;
    }

    private static String getTagValue(String tag, Element element) {
        NodeList tagList = element.getElementsByTagName(tag);
        if (tagList.getLength() > 0 && tagList.item(0).getFirstChild() != null) {
            return tagList.item(0).getTextContent();
        }
        return "[Not Found]";
    }
}









