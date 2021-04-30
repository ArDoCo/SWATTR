package dataImportExport;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import io.jenetics.ext.moea.Vec;
import nsgaii.Chromosome;
import nsgaii.Gen;

public class Printer {

    private static final String[] HEADERS = { "# Results of Rodriguez20", "", "modelElementID,sentence,confidence" };

    private static final String CSV_SEPARATOR = ",";

    static {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat timestamp = new SimpleDateFormat("yyyyMMddHHmmss");
        RESULT_BASE_NAME = "tracelinks_rodriguez20_" + timestamp.format(cal.getTime()) + "_";
    }

    private static final String RESULT_BASE_PATH = "./output/";
    private static final String RESULT_BASE_NAME;

    private List<Chromosome> bestChromsOfPop;
    private Map<Integer, String> nameMap;
    private Map<String, String> nameToId;
    private List<Pair<Integer, String>> clearReqs;
    private double maxCosineValue = 0.0;
    private double jaccardiAvg = 0.0;
    private double cosineAvg = 0.0;
    private double maxJaccardi = 0.0;
    private Chromosome bestChrom = null;
    private List<String> evalToolStrings = new ArrayList<>();
    private int maxI;
    private int maxP;
    private int maxG;
    private int i;

    public Printer(List<Chromosome> highestRankedChroms, Map<Integer, String> fileNameMap, Map<String, String> nameToId, List<Pair<Integer, String>> clearText,
            int maxII, int maxPI, int maxGI, int iI) {
        maxI = maxII;
        maxP = maxPI;
        maxG = maxGI;
        bestChromsOfPop = highestRankedChroms;
        nameMap = fileNameMap;
        this.nameToId = nameToId;
        clearReqs = clearText;
        i = iI;

    }

    public void printBestChromosome() {

        double counter = 0.0;

        for (Chromosome avgChrom : bestChromsOfPop) {
            jaccardiAvg = jaccardiAvg + avgChrom.getCumulatedJaccardAndCosineTuple().getValue0();
            cosineAvg = cosineAvg + avgChrom.getCumulatedJaccardAndCosineTuple().getValue1();
            if (avgChrom.getCumulatedJaccardAndCosineTuple().getValue0() > maxJaccardi) {
                maxJaccardi = avgChrom.getCumulatedJaccardAndCosineTuple().getValue0();
            }
            if (avgChrom.getCumulatedJaccardAndCosineTuple().getValue1() > maxCosineValue) {
                maxCosineValue = avgChrom.getCumulatedJaccardAndCosineTuple().getValue1();
            }
            counter++;
        }
        jaccardiAvg = jaccardiAvg / counter;
        cosineAvg = cosineAvg / counter;
        for (Chromosome currentChrom : bestChromsOfPop) {
            if (bestChrom == null) {
                bestChrom = currentChrom;
            }
            if (isDominating(currentChrom, bestChrom)) {
                bestChrom = currentChrom;
            }
        }
        writeToFile();
    }

    private boolean isDominating(Chromosome chromo1, Chromosome chromo2) {

        Vec<double[]> vec1 = Vec.of(chromo1.getCumulatedJaccardAndCosineTuple().getValue1(), chromo1.getCumulatedJaccardAndCosineTuple().getValue0());
        Vec<double[]> vec2 = Vec.of(chromo2.getCumulatedJaccardAndCosineTuple().getValue1(), chromo2.getCumulatedJaccardAndCosineTuple().getValue0());

        if ((vec1.dominance(vec2) == 1 && ((vec1.data()[0] == maxCosineValue) && (vec1.data()[1] >= jaccardiAvg)))
                || ((vec1.dominance(vec2) == 1 && ((vec1.data()[1] == maxJaccardi) && (vec1.data()[0] >= cosineAvg))))
                || ((vec1.dominance(vec2) == 1) && (vec1.data()[0] >= maxCosineValue * 0.8) && (vec1.data()[1] >= maxJaccardi * 0.8))) {
            return true;
        } else {
            return false;
        }
    }

    private void writeToFile() {
        String filename = RESULT_BASE_PATH + RESULT_BASE_NAME + maxG + "_" + maxI + "_" + maxP + "_" + i + ".csv";
        try (FileWriter csvWriter = new FileWriter(filename)) {

            for (String headerline : HEADERS) {
                csvWriter.append(headerline);
                csvWriter.append("\n");
            }

            for (Gen gen : bestChrom.getGenesOfChromosome()) {
                // String requirement = findRequirementText(gen);
                String sentenceId = "" + gen.getReqId();
                String modelElementName = nameMap.get(gen.getSrcCodeId());
                String modelElementId = nameToId.get(modelElementName);
                // double confidence = 1.0; // TODO: is there a way to show a confidence?
                String outputLine = modelElementId + CSV_SEPARATOR + sentenceId;
                evalToolStrings.add(outputLine);
                csvWriter.append(outputLine);
                csvWriter.append("\n");

            }
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String findRequirementText(Gen gen) {
        for (Pair<Integer, String> clearreq : clearReqs) {
            if (clearreq.getValue0() == gen.getReqId()) {
                return clearreq.getValue1();
            }
        }
        return null;
    }
}
