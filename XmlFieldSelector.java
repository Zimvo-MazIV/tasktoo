import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.util.*;

public class XmlFieldSelector {

    private static final List<String> ALLOWED_FIELDS = Arrays.asList(
        "name", "postalZip", "region", "country", "address", "list"
    );

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java XmlFieldSelector <file-path>");
            return;
        }

        String filePath = args[0];
        Scanner scanner = new Scanner(System.in);

        try {
            // Parse the XML
            File xmlFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList records = doc.getElementsByTagName("record");

            // Ask user for fields to extract
            System.out.println("Available fields: " + String.join(", ", ALLOWED_FIELDS));
            System.out.print("Enter the field/tag names you want to extract (comma-separated): ");
            String input = scanner.nextLine();
            String[] userFields = input.split(",");

            List<String> selectedFields = new ArrayList<>();
            for (String field : userFields) {
                field = field.trim();
                if (ALLOWED_FIELDS.contains(field)) {
                    selectedFields.add(field);
                } else {
                    System.out.println("Ignored invalid field: " + field);
                }
            }

            if (selectedFields.isEmpty()) {
                System.out.println("No valid fields selected. Exiting.");
                return;
            }

            // Print selected fields
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

    private static String getTagValue(String tag, Element element) {
        NodeList tagList = element.getElementsByTagName(tag);
        if (tagList.getLength() > 0 && tagList.item(0).getFirstChild() != null) {
            return tagList.item(0).getTextContent();
        }
        return "[Not Found]";
    }
}






