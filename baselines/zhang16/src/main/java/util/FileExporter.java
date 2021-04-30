package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import entity.TraceLink;

public class FileExporter {

    private static final String[] HEADERS = { "# Results of Zhang16", "", "modelElementID,sentence,confidence" };

    private static final String CSV_SEPARATOR = ",";
    private static final String SEPARATOR = File.separator;

    public static void writeToFile(File file, List<String> lines) {
        try (FileWriter fileWriter = new FileWriter(file.getAbsoluteFile())) {

            for (String line : lines) {
                fileWriter.write(line + "\n");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void writeTraceLinksToCsv(Collection<TraceLink> links, File file) {
        // create list and add headers
        List<String> csvLines = new ArrayList<>(Arrays.asList(HEADERS));

        // add links
        for (TraceLink link : links) {
            String[] docuPath = link.getDocumentationDocument().getName().split(Pattern.quote(SEPARATOR));
            String[] compPath = link.getEntityDocument().getEntityName().split(Pattern.quote(SEPARATOR));

            String sentenceId = docuPath[docuPath.length - 1].split("#")[1];
            String modelElementName = compPath[compPath.length - 1];
            String modelElementId = link.getEntityDocument().getId();
            String confidence = "" + link.getWeight();

            String csvLine = modelElementId + CSV_SEPARATOR + sentenceId + CSV_SEPARATOR + confidence;
            csvLines.add(csvLine);
        }
        writeToFile(file, csvLines);
    }

}
