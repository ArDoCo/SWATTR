package nsgaii;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

//This Class calculates a Crowding Distance Value to each given Crowding Distance Attribute
public class CrowdingDistanceCalculator {

    private Map<Integer, List<Chromosome>> rankmap;
    double normalizeMin;
    double normalizeMax;
    List<Chromosome> chromosomesofPareto;

    public CrowdingDistanceCalculator(Map<Integer, List<Chromosome>> inputrm) {
        rankmap = inputrm;
    }

    public void calculateCrowdingDistance() {
        // Implementierung des Algorithmus "crowding-distance-assignment" aus dem "A
        // fast and elitist multiobjective genetic algorithm" NSGA-II Paper
        for (Entry<Integer, List<Chromosome>> entry : rankmap.entrySet()) {
            chromosomesofPareto = entry.getValue();
            for (int i = 0; i < 2; i++) {
                normalize(i);
                quickSort(0, chromosomesofPareto.size() - 1);
                chromosomesofPareto.get(0).setCrowdingDistance(Double.MAX_VALUE);
                chromosomesofPareto.get(chromosomesofPareto.size() - 1).setCrowdingDistance(Double.MAX_VALUE);
                int secondToLastElement = chromosomesofPareto.size() - 2;
                for (int j = 1; j < secondToLastElement; j++) {
                    double calculatedCrowdingDistance = chromosomesofPareto.get(j).getCrowdingDistance()
                            + ((Double) chromosomesofPareto.get(j + 1).getCumulatedJaccardAndCosineTuple().getValue(i)
                                    - (Double) chromosomesofPareto.get(j - 1).getCumulatedJaccardAndCosineTuple().getValue(i))
                                    / ((Double) chromosomesofPareto.get(chromosomesofPareto.size() - 1).getCumulatedJaccardAndCosineTuple().getValue(i)
                                            - (Double) chromosomesofPareto.get(0).getCumulatedJaccardAndCosineTuple().getValue(i));
                    chromosomesofPareto.get(j).setCrowdingDistance(calculatedCrowdingDistance);
                }
            }
        }
    }

    private void normalize(int objectvieFuncNr) {

        normalizeMin = Double.MAX_VALUE;
        normalizeMax = Double.MIN_VALUE;

        for (Chromosome chromosome : chromosomesofPareto) {
            double valueBuffer = (Double) chromosome.getCumulatedJaccardAndCosineTuple().getValue(objectvieFuncNr);
            if (valueBuffer > normalizeMax) {
                normalizeMax = valueBuffer;
            } else if (valueBuffer < normalizeMin) {
                normalizeMin = valueBuffer;
            }
        }

        for (Chromosome chromosome : chromosomesofPareto) {

            double normalizedValue = ((Double) chromosome.getCumulatedJaccardAndCosineTuple().getValue(objectvieFuncNr) - normalizeMin)
                    / (normalizeMax - normalizeMin);
            chromosome.setNormalizedBuffer(normalizedValue);
        }
    }

    private void quickSort(int startIndex, int endIndex) {
        if (startIndex < endIndex) {
            int pivotIndex = partition(chromosomesofPareto, startIndex, endIndex);
            quickSort(startIndex, pivotIndex);
            quickSort(pivotIndex + 1, endIndex);
        }
    }

    private int partition(List<Chromosome> array, int startIndex, int endIndex) {
        int pivotIndex = (startIndex + endIndex) / 2;
        double pivotValue = array.get(pivotIndex).getNormalizedBuffer();
        startIndex--;
        endIndex++;

        while (true) {
            do {
                startIndex++;
            } while (array.get(startIndex).getNormalizedBuffer() < pivotValue);
            do {
                endIndex--;
            } while (array.get(endIndex).getNormalizedBuffer() > pivotValue);
            if (startIndex >= endIndex) {
                return endIndex;
            }
            Chromosome temp = array.get(startIndex);
            array.set(startIndex, array.get(endIndex));
            array.set(endIndex, temp);
        }
    }

    public Map<Integer, List<Chromosome>> getRankmap() {
        return rankmap;
    }

}
