package nsgaii;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.javatuples.Pair;

import com.google.common.collect.Table;

public class PopulationGenerator {

    private List<Pair<Integer, String[]>> codeList;
    private List<Pair<Integer, String[]>> requirementList = new ArrayList<>();
    private int genesPerChromosome = 0; // Anzahl Gene pro Chromosom
    private int populationSize = 0; // Größe der Population
    private Table<Integer, String, Double> tfIdfTable;
    private List<Pair<Integer, Integer>> fileIDRelation;
    private int chromosomeCounter = 0;
    private Table<Integer, Integer, Double> jaccardis;
    private int maxRequirementID;
    private int maxSrcCodeID;

    public PopulationGenerator(List<Pair<Integer, String[]>> codeWithIDs, List<Pair<Integer, Integer>> fileIDRelationList,
            Table<Integer, String, Double> tfidfResults, Table<Integer, Integer, Double> jaccardiRelations, int genesPerChromosome, int populationSize) {
        this.genesPerChromosome = genesPerChromosome;
        this.populationSize = populationSize;
        codeList = codeWithIDs;
        fileIDRelation = fileIDRelationList;
        tfIdfTable = tfidfResults;
        jaccardis = jaccardiRelations;
    }

    public void setupDataforPopulationgeneration(List<Pair<Integer, List<Pair<Integer, String[]>>>> req) {
        for (Pair<Integer, List<Pair<Integer, String[]>>> reqFile : req) {
            for (Pair<Integer, String[]> requirement : reqFile.getValue1()) {
                requirementList.add(requirement);
            }
        }
    }

    public Population generateInitialPopulation() {
        Population initialPopulation = new Population();
        for (int i = 0; i < populationSize; i++) {
            generateChromosome(initialPopulation);
        }

        return initialPopulation;
    }

    private void generateChromosome(Population population) {
        Chromosome newChromosome = new Chromosome();
        List<Gen> genesOfChromosome = new ArrayList<>();
        newChromosome.setChromosomeId(chromosomeCounter);
        chromosomeCounter++;
        // Generate Genes for the Chromosome
        maxRequirementID = requirementList.get(requirementList.size() - 1).getValue0();
        int idx = codeList.size() - 1;
        maxSrcCodeID = idx > 0 ? codeList.get(codeList.size() - 1).getValue0() : 0;

        for (int i = 0; i < genesPerChromosome; i++) {

            boolean genAlreadyInChromosome = true;
            Gen newGen;
            while (genAlreadyInChromosome) {
                newGen = createGen();
                genAlreadyInChromosome = isGenAlreadyInChromosome(genesOfChromosome, newGen);
            }

            genesOfChromosome.add(createGen());
        }

        newChromosome.setGenesOfChromosome(genesOfChromosome);

        cumulateObjectiveFunctions(newChromosome);
        population.getChromosomes().add(newChromosome);
    }

    private Gen createGen() {
        int randomReq = generateRandomInteger(0, maxRequirementID + 1);
        int srcCodeId = generateRandomInteger(maxRequirementID + 1, maxSrcCodeID + 1);

        Gen newGen = new Gen();
        newGen.setReqId(randomReq);
        newGen.setSrcCodeId(srcCodeId);
        newGen.setJaccardiSimilarity(jaccardis.get(randomReq, srcCodeId));
        newGen.setCosineSimilarity(calculateCosineSimilarityOfCouple(randomReq, srcCodeId, findFileOfID(randomReq), findFileOfID(srcCodeId)));

        return newGen;
    }

    private void cumulateObjectiveFunctions(Chromosome chromosome) {
        double cumJaccard = 0.0;
        double cumCosine = 0.0;
        for (Gen element : chromosome.getGenesOfChromosome()) {
            cumCosine = cumCosine + element.getCosineSimilarity();
            cumJaccard = cumJaccard + element.getJaccardiSimilarity();
        }
        chromosome.setCumulatedJaccardAndCosineTuple(new Pair<>(cumJaccard, cumCosine));
    }

    public List<Chromosome> mutateChromosomes(List<Chromosome> inputChromos) {

        List<Chromosome> mutatedChromos = new ArrayList<>();
        for (Chromosome currentChromo : inputChromos) {
            Chromosome chromoToMutate = new Chromosome();
            List<Gen> genes = currentChromo.getGenesOfChromosome();
            chromoToMutate.getGenesOfChromosome().addAll(genes);
            int nrOfGenesToReplace = generateRandomInteger(0, genes.size());
            while (nrOfGenesToReplace != 0) {
                int genToMutateIndex = generateRandomInteger(0, genes.size());

                boolean genAlreadyInChromosome = true;
                Gen newGen = new Gen();
                while (genAlreadyInChromosome) {
                    newGen = createGen();
                    genAlreadyInChromosome = isGenAlreadyInChromosome(chromoToMutate.getGenesOfChromosome(), newGen);
                }

                chromoToMutate.getGenesOfChromosome().set(genToMutateIndex, newGen);
                nrOfGenesToReplace--;
                chromoToMutate.setChromosomeId(chromosomeCounter);
                chromosomeCounter++;
            }
            cumulateObjectiveFunctions(chromoToMutate);
            mutatedChromos.add(chromoToMutate);

        }
        return mutatedChromos;
    }

    private boolean isGenAlreadyInChromosome(List<Gen> chromGens, Gen gen) {
        for (Gen testGen : chromGens) {
            if (testGen.getReqId() == gen.getReqId() && testGen.getSrcCodeId() == gen.getSrcCodeId()) {
                return true;
            }
        }

        return false;
    }

    public void crossoverMutants(List<Chromosome> inputChromos) {

        for (int i = 0; i < inputChromos.size() / 2; i++) {
            int randomChrom1 = generateRandomInteger(0, inputChromos.size());
            int randomChrom2 = generateRandomInteger(0, inputChromos.size());

            Chromosome chrom1 = inputChromos.get(randomChrom1);
            Chromosome chrom2 = inputChromos.get(randomChrom2);

            for (int j = 0; j < chrom1.getGenesOfChromosome().size() / 2; j++) {
                int genIndex = generateRandomInteger(0, chrom1.getGenesOfChromosome().size());
                Gen temp = chrom1.getGenesOfChromosome().get(genIndex);
                chrom1.getGenesOfChromosome().set(genIndex, chrom2.getGenesOfChromosome().get(genIndex));
                chrom2.getGenesOfChromosome().set(genIndex, temp);

            }
        }
    }

    public List<Chromosome> tournamentSelection(List<Chromosome> chromosomoesOfPopulation) {
        List<Chromosome> winnerList = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            Chromosome candidate1 = pickRandomParentCandidate(chromosomoesOfPopulation);
            Chromosome candidate2 = pickRandomParentCandidate(chromosomoesOfPopulation);
            Chromosome winner = binaryTournament(candidate1, candidate2);
            winnerList.add(winner);
        }
        return winnerList;
    }

    private Chromosome pickRandomParentCandidate(List<Chromosome> candidates) {
        int randomIndexParent = ThreadLocalRandom.current().nextInt(0, candidates.size());
        return candidates.get(randomIndexParent);
    }

    private Chromosome binaryTournament(Chromosome chromo1, Chromosome chromo2) {
        if (chromo1.getRank() > chromo2.getRank()) {
            return chromo1;
        }

        if (chromo1.getRank() < chromo2.getRank()) {
            return chromo2;
        }

        if (chromo1.getRank() == chromo2.getRank()) {
            if (chromo1.getCrowdingDistance() > chromo2.getCrowdingDistance()) {
                return chromo1;
            }
            if (chromo1.getCrowdingDistance() < chromo2.getCrowdingDistance()) {
                return chromo2;
            }
        }
        return chromo1;
    }

    public List<Chromosome> selectChromosomesForTheNextPopulation(Map<Integer, List<Chromosome>> oldPop) {
        List<Chromosome> listOfNewChromosomes = new ArrayList<>();
        int currentSizeOfNewPopulation = 0;
        for (Entry<Integer, List<Chromosome>> entry : oldPop.entrySet()) {
            List<Chromosome> entryValue = entry.getValue();
            if (currentSizeOfNewPopulation < populationSize) {
                if (currentSizeOfNewPopulation + entryValue.size() <= populationSize) {
                    listOfNewChromosomes.addAll(entryValue);
                    currentSizeOfNewPopulation = currentSizeOfNewPopulation + entryValue.size();
                } else if (currentSizeOfNewPopulation + entryValue.size() > populationSize) {
                    int missingChromosomes = populationSize - currentSizeOfNewPopulation;
                    listOfNewChromosomes.addAll(selectViaCrowdingDistance(missingChromosomes, entryValue));
                    currentSizeOfNewPopulation = missingChromosomes + currentSizeOfNewPopulation;
                }
            }

        }
        return listOfNewChromosomes;
    }

    public List<Chromosome> selectViaCrowdingDistance(int nrOfNeededChromosomes, List<Chromosome> paretoFront) {

        quickSort(0, paretoFront.size() - 1, paretoFront);
        List<Chromosome> listOfCrowdingDistanceChroms = new ArrayList<>();
        int highestCrowdingDistanceElement = paretoFront.size() - 1;
        while (nrOfNeededChromosomes != 0) {
            if (highestCrowdingDistanceElement == -1) {
                break;
            }
            listOfCrowdingDistanceChroms.add(paretoFront.get(highestCrowdingDistanceElement));
            nrOfNeededChromosomes--;
            highestCrowdingDistanceElement--;
        }
        return listOfCrowdingDistanceChroms;
    }

    private void quickSort(int startIndex, int endIndex, List<Chromosome> paretoFront) {
        if (startIndex < endIndex) {
            int pivotIndex = partition(paretoFront, startIndex, endIndex);
            quickSort(startIndex, pivotIndex, paretoFront);
            quickSort(pivotIndex + 1, endIndex, paretoFront);
        }
    }

    private int partition(List<Chromosome> array, int startIndex, int endIndex) {
        int pivotIndex = (startIndex + endIndex) / 2;
        double pivotValue = array.get(pivotIndex).getCrowdingDistance();
        startIndex--;
        endIndex++;

        while (true) {
            do {
                startIndex++;
            } while (array.get(startIndex).getCrowdingDistance() < pivotValue);
            do {
                endIndex--;
            } while (array.get(endIndex).getCrowdingDistance() > pivotValue);
            if (startIndex >= endIndex) {
                return endIndex;
            }
            Chromosome temp = array.get(startIndex);
            array.set(startIndex, array.get(endIndex));
            array.set(endIndex, temp);
        }
    }

    private int findFileOfID(int id) {
        for (Pair<Integer, Integer> idRelation : fileIDRelation) {
            if (idRelation.getValue1() == id) {
                return idRelation.getValue0();
            }
        }
        return 0;
    }

    private double calculateCosineSimilarityOfCouple(int reqID, int srcCodeID, int reqFileID, int srcCodeFileID) {
        String[] requirement = getTextFromInputList(reqID);
        double dotProduct = 0;
        double requirementsRadicant = 0;
        double srcCodeRadicant = 0;

        for (String element : requirement) {
            double reqTfidf = tfIdfTable.get(reqFileID, element);
            requirementsRadicant += Math.pow(reqTfidf, 2);
            double srcCodeTfidf;
            if (tfIdfTable.get(srcCodeFileID, element) != null) {
                srcCodeTfidf = tfIdfTable.get(srcCodeFileID, element);
                srcCodeRadicant += Math.pow(srcCodeTfidf, 2);
            } else {
                srcCodeTfidf = 0.0;
            }
            dotProduct += srcCodeTfidf * reqTfidf;

        }

        double cosineSimilarityDenominator = Math.sqrt(requirementsRadicant) * Math.sqrt(srcCodeRadicant);

        return dotProduct / cosineSimilarityDenominator;
    }

    private String[] getTextFromInputList(int idToSearchFor) {

        List<Pair<Integer, String[]>> mixedList = new ArrayList<>();
        mixedList.addAll(requirementList);
        mixedList.addAll(codeList);

        for (Pair<Integer, String[]> listEntry : mixedList) {
            if (idToSearchFor == listEntry.getValue0()) {
                return listEntry.getValue1();
            }
        }
        return new String[0];
    }

    public String removePunctuation(String rawString) {
        return rawString.replaceAll("\\p{P}", " ");
    }

    public String[] splitAtSpace(String stringToSplit) {
        return stringToSplit.split("\\s+");
    }

    private int generateRandomInteger(int min, int max) {
        if (min >= max) {
            return min;
        }
        return ThreadLocalRandom.current().nextInt(min, max);
    }

}
