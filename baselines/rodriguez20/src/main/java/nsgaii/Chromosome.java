package nsgaii;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.javatuples.Pair;

public class Chromosome {

    private int chromosomeId;
    private int rank;
    private int dominationCounter;
    private double crowdingDistance = 0.0;
    private double normalizedBuffer = 0.0;
    private Set<Chromosome> dominatedChromosomes = new HashSet<>();
    private List<Gen> genesOfChromosome = new ArrayList<>();
    private Pair<Double, Double> cumulatedJaccardAndCosineTuple;

    public int getChromosomeId() {
        return chromosomeId;
    }

    public void setChromosomeId(int chromosomeId) {
        this.chromosomeId = chromosomeId;
    }

    public List<Gen> getGenesOfChromosome() {
        return genesOfChromosome;
    }

    public void setGenesOfChromosome(List<Gen> genesOfChromosome) {
        this.genesOfChromosome = genesOfChromosome;
    }

    public Set<Chromosome> getDominatedChromosomes() {
        return dominatedChromosomes;
    }

    public void setDominatedChromosomes(Set<Chromosome> dominatedChromosomes) {
        this.dominatedChromosomes = dominatedChromosomes;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getDominationCounter() {
        return dominationCounter;
    }

    public void setDominationCounter(int dominationCounter) {
        this.dominationCounter = dominationCounter;
    }

    public double getCrowdingDistance() {
        return crowdingDistance;
    }

    public void setCrowdingDistance(double crowdingDistance) {
        this.crowdingDistance = crowdingDistance;
    }

    public Pair<Double, Double> getCumulatedJaccardAndCosineTuple() {
        return cumulatedJaccardAndCosineTuple;
    }

    public void setCumulatedJaccardAndCosineTuple(Pair<Double, Double> cumulatedJaccardAndCosineTuple) {
        this.cumulatedJaccardAndCosineTuple = cumulatedJaccardAndCosineTuple;
    }

    public double getNormalizedBuffer() {
        return normalizedBuffer;
    }

    public void setNormalizedBuffer(double normalizedBuffer) {
        this.normalizedBuffer = normalizedBuffer;
    }

}
