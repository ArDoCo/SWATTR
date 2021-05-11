package process;

import java.io.File;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import entity.Document;
import entity.ModelEntityDocument;
import entity.TraceLink;
import util.Evaluator;
import util.FileExporter;

public class Main {

    protected static final double[] THRESHOLDS = { 0.03, 0.025, 0.02, 0.01, 0.005 };
    private static final double DELTA = 0.25;
    private static final String RESULT_SAVE_PATH = "./output/";

    public static void runAndSave(String repoFilePath, String docuFilePath, File wordnetDict) {

        // 1. create new model- and documentation processor
        NLProcessor nlProcessor = null;
        try {
            nlProcessor = new NLProcessor(wordnetDict);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.err.println("Could not load dictionary. Aborting now!");
            return;
        }
        ModelProcessor modelProcessor = new ModelProcessor(repoFilePath, nlProcessor);
        DocumentationProcessor documentationProcessor = new DocumentationProcessor(docuFilePath, nlProcessor);

        List<ModelEntityDocument> entityDocuments = modelProcessor.createModelEntityDocuments();
        List<Document> documentationDocuments = documentationProcessor.createDocuments();

        // 2. calculate links
        for (double threshold : THRESHOLDS) {
            System.out.println("Calculating links for threshold " + threshold);
            LinkCalculator linkCalculator = new LinkCalculator(DELTA);
            Set<TraceLink> traceLinks = linkCalculator.calculateLinks(documentationDocuments, entityDocuments, threshold);
            List<TraceLink> sortedLinks = new ArrayList<>(traceLinks);
            Collections.sort(sortedLinks);

            // 3. save links to csv
            System.out.println("Saving found links for threshold " + threshold);
            File savePath = createSaveFile(threshold);
            FileExporter.writeTraceLinksToCsv(sortedLinks, savePath);
        }

        System.out.println("Finished.");
    }

    private static File createSaveFile(double threshold) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat timestamp = new SimpleDateFormat("yyyyMMddHHmmss");
        String savePathString = RESULT_SAVE_PATH + "tracelinks_zhang16_" + threshold + "_" + timestamp.format(cal.getTime()) + ".csv";
        return new File(savePathString);
    }

    public static void runAndEvaluate(String repoFilePath, String assemblyFilePath, String docuFilePath, String wordnetDictPath, File savePath,
            String solutionPath, File evaluationSavePath) {

        // 1. create new model- and documentation processor
        NLProcessor nlProcessor = null;
        try {
            nlProcessor = new NLProcessor(wordnetDictPath);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.err.println("Could not load dictionary. Aborting now!");
            return;
        }
        ModelProcessor modelProcessor = new ModelProcessor(repoFilePath, nlProcessor);
        DocumentationProcessor documentationProcessor = new DocumentationProcessor(docuFilePath, nlProcessor);

        List<ModelEntityDocument> entityDocuments = modelProcessor.createModelEntityDocuments();
        List<Document> documentationDocuments = documentationProcessor.createDocuments();

        // 2. calculate links
        System.out.println("Calculating links...");
        LinkCalculator linkCalculator = new LinkCalculator(DELTA);
        Set<TraceLink> traceLinks = linkCalculator.calculateLinks(documentationDocuments, entityDocuments, THRESHOLDS[1]);
        List<TraceLink> sortedLinks = new ArrayList<>(traceLinks);
        Collections.sort(sortedLinks);

        // 3. save links to csv and evaluate
        System.out.println("Saving found links...");
        FileExporter.writeTraceLinksToCsv(sortedLinks, savePath);
        Evaluator.evaluateAndSave(traceLinks, solutionPath, evaluationSavePath);

    }

    /**
     * @param args Should be like the following: - 1. Path to the text document - 2. Path to the ".repository" file of a
     *             PCM
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Invalid Arguments, stopping execution. Please proved path to text document and to repository file.");
            return;
        }

        String docuFilePath = args[0];
        String repoFilePath = args[1];

        System.out.println("Initializing...");

        File wordnetDatabaseDict = copyData();
        if (!wordnetDatabaseDict.exists()) {
            System.out.println("Dictionary firectory does not exist: " + wordnetDatabaseDict.getAbsolutePath());
            return;
        }

        runAndSave(repoFilePath, docuFilePath, wordnetDatabaseDict);
    }

    private static File copyData() {
        Object loader = new Object() {
        };
        File tmp = new File(System.getProperty("java.io.tmpdir"));
        File target = new File(tmp + File.separator + "swattr-zhang16-data");
        target.mkdirs();
        final String base = "wordnet-database/dict";
        Set<String> files = new Reflections(base, new ResourcesScanner()).getResources(p -> true);
        try {
            for (String file : files) {
                String path = file.substring(base.length());
                FileUtils.copyURLToFile(loader.getClass().getResource("/" + file), new File(target + path));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        System.out.println("Copied files to " + target.getAbsolutePath());
        return target;
    }
}
