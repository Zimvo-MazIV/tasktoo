import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class XmlFileReader {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java XmlFileReader <file-path>");
            return;
        }

        String filePath = args[0];

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }
}




