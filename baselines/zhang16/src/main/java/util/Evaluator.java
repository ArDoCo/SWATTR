package util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.TraceLink;

public class Evaluator {

    private static List<String> importGoldstandard(String path) {
        List<String> solutionText = FileImporter.importFileLines(path);
        Map<String, List<String>> solutionMap = new HashMap<>();
        List<String> solutionStrLinks = new ArrayList<>();

        for (String line : solutionText) {
            List<String> split = Arrays.asList(line.split(","));
            String k = split.get(0);
            solutionMap.put(k, split.subList(1, split.size()));
        }

        for (String k : solutionMap.keySet()) {
            for (String e : solutionMap.get(k)) {
                String linkString = k + "@" + e;
                solutionStrLinks.add(linkString);
            }
        }

        return solutionStrLinks;
    }

    public static List<double[]> evaluate(Collection<TraceLink> traceLinks, String solutionPath) {
        List<double[]> result = new ArrayList<>();
        List<TraceLink> foundLinks = new ArrayList<>(traceLinks);
        List<String> foundStrLinks = new ArrayList<>();
        List<String> correctLinks = importGoldstandard(solutionPath);
        Collections.sort(foundLinks);

        for (TraceLink link : foundLinks) {
            String strLink = link.getDocumentationDocument().getName().split("#")[1] + "@" + link.getEntityDocument().getEntityName();

            foundStrLinks.add(strLink);
            double[] metrics = calcMetric(foundStrLinks, correctLinks);
            double f1 = 0.0;
            if ((metrics[0] + metrics[0]) > 0.0) {
                f1 = 2.0 * ((metrics[0] * metrics[1]) / (metrics[0] + metrics[1]));
            }
            double[] entry = { link.getWeight(), metrics[0], metrics[1], f1 };
            result.add(entry);
        }

        return result;
    }

    public static void evaluateAndSave(Collection<TraceLink> traceLinks, String solutionPath, File evaluationSavePath) {
        List<double[]> result = evaluate(traceLinks, solutionPath);
        List<String> csvLines = new ArrayList<>();
        csvLines.add("Threshold, Precision, Recall, f1score");

        for (double[] triple : result) {
            csvLines.add(triple[0] + "," + triple[1] + "," + triple[2] + "," + triple[3]);
        }

        FileExporter.writeToFile(evaluationSavePath, csvLines);
    }

    private static double[] calcMetric(List<String> foundLinks, List<String> correctLinks) {
        double fp = 0;
        double tp = 0;
        double fn = 0;

        for (String l : foundLinks) {
            if (correctLinks.contains(l)) {
                tp += 1;
            } else {
                fp += 1;
            }
        }

        for (String l : correctLinks) {
            if (!foundLinks.contains(l)) {
                fn += 1;
            }
        }

        double precision = tp / (fp + tp);
        double recall = tp / (tp + fn);

        double[] result = { precision, recall };
        return result;
    }
}
