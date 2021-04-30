package controller;

import java.util.List;

import org.javatuples.Pair;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Table;

import dataImportExport.DataImporter;
import dataImportExport.Printer;
import nsgaii.Chromosome;
import nsgaii.CrowdingDistanceCalculator;
import nsgaii.FastNonDominatedSort;
import nsgaii.Population;
import nsgaii.PopulationGenerator;
import preprocessing.ObjFuncsOfSingleWordsCalculator;

public class Controller {

    private DataImporter dataImporter;
    private ObjFuncsOfSingleWordsCalculator wordSimCalculator;
    private PopulationGenerator populationGenerator;
    private Table<Integer, Integer, Double> jaccardOfWordInSet;
    private CrowdingDistanceCalculator crowdingDistanceCalculator;
    private int genesPerChromosome;
    private int populationSize;

    public Controller(String[] args) {
        dataImporter = new DataImporter();
        wordSimCalculator = new ObjFuncsOfSingleWordsCalculator();
        System.out.println("Setting up Dataset - This might take a while ");
        setupDataset(args);
        System.out.println("Calculating Jaccard");
        calculateJaccardSimilarities();
        System.out.println("Calculating TFIDF");
        calculateTFIDF();
    }

    public Controller(String requirementsFolder, String srcFolder) {
        dataImporter = new DataImporter();
        wordSimCalculator = new ObjFuncsOfSingleWordsCalculator();
        System.out.println("Setting up Dataset - This might take a while ");
        dataImporter.setupDataSetForCalculations(requirementsFolder, srcFolder);
        System.out.println("Calculating Jaccard");
        calculateJaccardSimilarities();
        System.out.println("Calculating TFIDF");
        calculateTFIDF();
    }

    private void setupDataset(String[] paths) {
        dataImporter.setupDataSetForCalculations(paths[0], paths[1]);
    }

    private void calculateJaccardSimilarities() {
        wordSimCalculator = new ObjFuncsOfSingleWordsCalculator();
        jaccardOfWordInSet = wordSimCalculator.calculateJaccardiOfDirectories(dataImporter.getReqFiles(), dataImporter.getCodeFilesContent());
    }

    private void calculateTFIDF() {
        List<Pair<Integer, List<Pair<Integer, String[]>>>> combinedList = dataImporter.srcCodeToReqFileListFormatConverter(dataImporter.getCodeFilesContent());
        combinedList.addAll(dataImporter.getReqFiles());
        wordSimCalculator.computeTfidf(combinedList);
    }

    public void runNSGAII(int iterations, int genesPerChromosome, int populationSize, int round) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        this.genesPerChromosome = genesPerChromosome;
        this.populationSize = populationSize;
        Population population = generatePopulation();

        for (int i = 0; i < iterations; i++) {

            FastNonDominatedSort fastNonDominatedSort = new FastNonDominatedSort(population);
            fastNonDominatedSort.executeFastNonDominatedSort();
            crowdingDistanceCalculator = new CrowdingDistanceCalculator(fastNonDominatedSort.getRankmap());
            crowdingDistanceCalculator.calculateCrowdingDistance();
            if (iterations - i > 1) {
                List<Chromosome> winnersOfTournament = populationGenerator.tournamentSelection(population.getChromosomes());
                List<Chromosome> mutants = populationGenerator.mutateChromosomes(winnersOfTournament);
                populationGenerator.crossoverMutants(mutants);
                population.getChromosomes().addAll(mutants);
                population.setChromosomes(populationGenerator.selectChromosomesForTheNextPopulation(crowdingDistanceCalculator.getRankmap()));
            }
        }
        Printer evaluator = new Printer(crowdingDistanceCalculator.getRankmap().get(1), dataImporter.getReqIdFilenameMap(), dataImporter.getNameToId(),
                dataImporter.getClearText(), genesPerChromosome, populationSize, iterations, round);
        evaluator.printBestChromosome();

        stopwatch.stop();
    }

    private Population generatePopulation() {
        populationGenerator = new PopulationGenerator(dataImporter.getCodeFilesContent(), dataImporter.getFileIDRelation(), wordSimCalculator.getTfIdfTable(),
                jaccardOfWordInSet, genesPerChromosome, populationSize);

        populationGenerator.setupDataforPopulationgeneration(dataImporter.getReqFiles());
        return populationGenerator.generateInitialPopulation();
    }
}
