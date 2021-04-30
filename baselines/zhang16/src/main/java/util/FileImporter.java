package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileImporter {

    public static List<String> importFileLines(String filePath) {
        List<String> lines = new ArrayList<String>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    filePath));
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static String importFile(String filePath) {
        String text = "";
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    filePath));
            String line = reader.readLine();
            while (line != null) {
                text += (line + "\n");
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

}
