package nsgaii;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.jenetics.ext.moea.Vec;

public class FastNonDominatedSort {

    private Population population;
    private double maxCosineValue = 0.0;
    private double cosineAvg = 0.0;
    private double maxJaccardi = 0.0;
    private double jaccardiAvg = 0.0;
    private Map<Integer, List<Chromosome>> rankmap = new HashMap<>();

    public FastNonDominatedSort(Population population) {
        this.population = population;
    }

    public void executeFastNonDominatedSort() {
        maxCosineValue();
        maxJaccardiValue();
        int gId = 1;
        ArrayList<Chromosome> notClassifiedChromosomes = new ArrayList<>();
        notClassifiedChromosomes.addAll(population.getChromosomes());
        while (notClassifiedChromosomes.size() != 0) {
            ArrayList<Chromosome> classifiedChroms = sortIteration(notClassifiedChromosomes);
            notClassifiedChromosomes.removeAll(classifiedChroms);
            rankmap.put(gId, classifiedChroms);
            gId++;
        }
    }

    private ArrayList<Chromosome> sortIteration(ArrayList<Chromosome> inputChromosomes) {
        ArrayList<Chromosome> fGenerationMap = new ArrayList<>();

        for (int i = 0; i < inputChromosomes.size(); i++) {
            Chromosome chromosome = inputChromosomes.get(i);
            chromosome.setDominationCounter(0);
            // unter Umständen wieder auf "i+1" ändern -> dann kommen allerdings
            // false-Positives dazu
            for (Chromosome compareChromo : inputChromosomes) {
                if (isDominating(chromosome, compareChromo)) {
                    chromosome.getDominatedChromosomes().add(compareChromo);
                } else if (isDominating(compareChromo, chromosome)) {
                    // P (chromosome) wird von Q (compareChromosome) dominiert -> counter von P
                    // hochzählen
                    chromosome.setDominationCounter(chromosome.getDominationCounter() + 1);
                }
            }
            if (chromosome.getDominationCounter() == 0) {
                chromosome.setRank(1);
                fGenerationMap.add(chromosome);
            }
        }
        return fGenerationMap;
    }

    private void maxJaccardiValue() {

        maxJaccardi = 0;
        jaccardiAvg = 0.0;
        double jaccardiAvgBuffer = 0.0;
        for (Chromosome chromosome : population.getChromosomes()) {
            double jaccardiBuffer = 0.0;
            for (Gen gen : chromosome.getGenesOfChromosome()) {
                jaccardiBuffer = jaccardiBuffer + gen.getJaccardiSimilarity();
                jaccardiAvgBuffer = jaccardiAvgBuffer + gen.getJaccardiSimilarity();

            }
            if (maxJaccardi < jaccardiBuffer) {
                maxJaccardi = jaccardiBuffer;
            }
        }
        jaccardiAvg = jaccardiAvgBuffer / (population.getChromosomes().size() * population.getChromosomes().get(0).getGenesOfChromosome().size());
    }

    private void maxCosineValue() {

        maxCosineValue = 0.0;
        double avgBuffer = 0.0;
        for (Chromosome chromosome : population.getChromosomes()) {
            double cosineChromo2 = 0.0;
            for (Gen gen : chromosome.getGenesOfChromosome()) {
                cosineChromo2 = gen.getCosineSimilarity();
                avgBuffer = avgBuffer + cosineChromo2;
            }
            if (maxCosineValue < cosineChromo2) {
                maxCosineValue = cosineChromo2;
            }
        }
        cosineAvg = avgBuffer / (population.getChromosomes().size() * population.getChromosomes().get(0).getGenesOfChromosome().size());
    }

    private boolean isDominating(Chromosome chromo1, Chromosome chromo2) {

        Vec<double[]> vec1 = Vec.of(chromo1.getCumulatedJaccardAndCosineTuple().getValue1(), chromo1.getCumulatedJaccardAndCosineTuple().getValue0());
        Vec<double[]> vec2 = Vec.of(chromo2.getCumulatedJaccardAndCosineTuple().getValue1(), chromo2.getCumulatedJaccardAndCosineTuple().getValue0());

        if ((vec1.dominance(vec2) == 1 && ((vec1.data()[0] == maxCosineValue) && (vec1.data()[1] >= jaccardiAvg)))
                || ((vec1.dominance(vec2) == 1 && ((vec1.data()[1] == maxJaccardi) && (vec1.data()[0] >= cosineAvg))))
                || ((vec1.dominance(vec2) == 1) && (vec1.data()[0] >= maxCosineValue * 0.9) && (vec1.data()[1] >= maxJaccardi * 0.9))) {
            return true;
        } else {
            return false;
        }
    }

    public Map<Integer, List<Chromosome>> getRankmap() {
        return rankmap;
    }

}
