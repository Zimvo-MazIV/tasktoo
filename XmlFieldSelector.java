import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.util.Scanner;

public class XmlFieldSelector {

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

            // Assuming the root has multiple child elements like <employee>
            String parentTag = doc.getDocumentElement().getFirstChild().getNodeName();
            NodeList nodeList = doc.getElementsByTagName(parentTag);

            System.out.println("Enter the field/tag names you want to extract (comma-separated):");
            String input = scanner.nextLine();
            String[] fields = input.split(",");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    System.out.println("Entry " + (i + 1) + ":");
                    for (String field : fields) {
                        String tag = field.trim();
                        String value = getTagValue(tag, element);
                        System.out.println("  " + tag + ": " + value);
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
        if (tagList.getLength() > 0) {
            Node node = tagList.item(0);
            return node.getTextContent();
        }
        return "[Not Found]";
    }
}





