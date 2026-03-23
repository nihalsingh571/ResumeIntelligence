import java.io.*;

/**
 * ResumeReader.java
 * Module 1: Reads resume or job description text from a file.
 * Uses BufferedReader for efficient line-by-line reading.
 */
public class ResumeReader {

    /**
     * Reads the entire content of a text file and returns it as a String.
     * @param filePath the path to the text file
     * @return the full text content of the file, or empty string on error
     */
    public String readFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println("[ERROR] File not found: " + filePath);
        } catch (IOException e) {
            System.out.println("[ERROR] Could not read file: " + filePath);
        }
        return content.toString().trim();
    }

    /**
     * Checks whether a file exists at the given path.
     * @param filePath path to check
     * @return true if the file exists
     */
    public boolean fileExists(String filePath) {
        File f = new File(filePath);
        return f.exists() && f.isFile();
    }
}
