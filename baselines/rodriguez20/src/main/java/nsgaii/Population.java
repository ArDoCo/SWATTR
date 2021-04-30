package nsgaii;

import java.util.ArrayList;
import java.util.List;

public class Population {

    private int populationId;
    private List<Chromosome> chromosomes = new ArrayList<>();

    public int getPopulationId() {
        return populationId;
    }

    public void setPopulationId(int populationId) {
        this.populationId = populationId;
    }

    public List<Chromosome> getChromosomes() {
        return chromosomes;
    }

    public void setChromosomes(List<Chromosome> chromosomes) {
        this.chromosomes = chromosomes;
    }

}
