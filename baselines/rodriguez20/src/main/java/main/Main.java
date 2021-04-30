package main;

import controller.Controller;

public class Main {

    /**
     * @param args Should be like the following: - 1. Path to the folder with the text documents - 2. Path to the folder
     *             containing the ".repository" file of a PCM - 3. Number of iterations - 4. Number of genes per
     *             chromosome - 5. Number of chromosomes per population - 6. Number of iterations of the whole algorithm
     *             with given parameters (used for calculation means because of randomness)
     */
    public static void main(String[] args) {
        String reqFolder = args[0];
        String srcFolder = args[1];
        int iterations = Integer.parseInt(args[2]);
        int genesPerChromosome = Integer.parseInt(args[3]);
        int populationSize = Integer.parseInt(args[4]);
        int rounds = Integer.parseInt(args[5]);
        execute(reqFolder, srcFolder, iterations, genesPerChromosome, populationSize, rounds);
        System.out.println("Done");
    }

    private static void execute(String reqFolder, String srcFolder, int iterations, int genesPerChromosome, int populationSize, int rounds) {
        Controller controller = new Controller(reqFolder, srcFolder);
        for (int i = 0; i < rounds; i++) {
            System.out.println("Running the complete NSGA-II algorithm for the " + (i + 1) + " time");
            controller.runNSGAII(iterations, genesPerChromosome, populationSize, i);
        }
    }
}
