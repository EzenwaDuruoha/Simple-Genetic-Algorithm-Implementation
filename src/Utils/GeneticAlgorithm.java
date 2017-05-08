package Utils;

import java.util.*;

public class GeneticAlgorithm {

    private static  String EXPECTED = "we can evolve this phrase";
    private static String[] LEXICON = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
                                        "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", " "};

    public static void main(String[] args){
        int populationSize =500;
        String[] target = EXPECTED.split("");
        String[][] population = createPopulation(populationSize, EXPECTED.length());

        boolean running = true;
        int generationCount = 0;
        while (running){
            Generation bestGenes = selection(population, 100);
            String[][] newpopulation = crossOver(bestGenes, populationSize, 0.01);
            for (int i = 0; i < newpopulation.length; i++){
                if (equals(newpopulation[i], target)){
                    System.out.println("[INFO] Found......." + Arrays.toString(newpopulation[i]));
                    running = false;
                }else {
                    population = newpopulation;
                    generationCount++;
                }
            }
        }
        System.out.println("[INFO] Completed in .......[" + generationCount + "]..Generations");

    }

    public static String[][] createPopulation(int populationSize, int geneSize){
        String[][] population = new String[populationSize][geneSize];
        Random gen = new Random(0);
        for (int i = 0; i < populationSize; i++){
            for(int j = 0; j < geneSize; j++){
                int index = gen.nextInt(LEXICON.length);
                population[i][j] = LEXICON[index];
            }
        }
        return population;
    }

    public static double fitnessFunction(String[] gene){
        int fitScore = 0;
        String[] target = EXPECTED.split("");
        double len = target.length;
        for (int i = 0; i < gene.length; i++){
            if(gene[i].equals(target[i])){
                fitScore++;
            }
        }
        double percentage = fitScore/len;
        return percentage;
    }

    public static Generation selection(String[][] population, int selectedNumber){
        String[][] bestFit = new String[selectedNumber][];
        double[] count = new double[population.length];
        int index = 0;

        for (String[] gene : population ) {
            double fitScore = fitnessFunction(gene);
            count[index] = fitScore;
            index++;
        }
        double[] countCopy = Arrays.copyOf(count, count.length);
        double[] fitnesses = new double[selectedNumber];
        for (int i = 0; i < selectedNumber; i++){
            double max = 0;
            int bestIndex = 0;
            for(int j = 0; j < count.length; j++){
                if(countCopy[j] > max){
                    max = countCopy[j];
                    bestIndex = j;
                }
            }
            bestFit[i] = population[bestIndex];
            fitnesses[i] = max;
            countCopy[bestIndex] = 0;
        }
        System.out.println("[INFO] Best Selection......." + Arrays.toString(bestFit[0]));
        Generation g = new Generation(bestFit, fitnesses);
        return g;
    }

    public static String[][] crossOver(Generation g, int populationSize, double mutationRate){
        List<Double> uniqueFitness = new ArrayList<>();
        String[][] parents = new String[populationSize][];

        for (int i = 0; i < g.getPopulation().length; i++) {
            Double cvrt = g.getFitnessScore()[i];
            if (!uniqueFitness.contains(cvrt)){
                uniqueFitness.add(cvrt);
            }
        }
        List<Double> selectionProbability = assignProbability(uniqueFitness);
        for (int i = 0; i < g.getPopulation().length; i++){
            for(int j = 0; j < uniqueFitness.size(); j++){
                if(g.getFitnessScore()[i] == uniqueFitness.get(j)){
                    g.setSelectionProbability(i, selectionProbability.get(j));
                }
            }
        }
        for (int i = 0; i < populationSize; i++){
            int index = weightedSelection(g.getSelectionProbability());
            parents[i] = g.getPopulation()[index];
        }
        String[][] children = breed(parents, populationSize, mutationRate);

        return children;
    }

    private static String[][] breed(String[][] parents, int populationSize, double mutationRate){
        Random gen = new Random();
        int geneLen = parents[0].length;
        int splitPoint = geneLen/2;
        String[][] newPopulation = new String[populationSize][];

        for (int i = 0; i < populationSize; i++){
            int indexA = gen.nextInt(parents.length);
            int indexB = gen.nextInt(parents.length);
            String[] parentA = parents[indexA];
            String[] parentB = parents[indexB];
            String[] child = new String[geneLen];
            for (int j = 0; j < geneLen; j++){
                if(j < splitPoint){
                    child[j] = parentA[j];
                }else {
                    child[j] = parentB[j];
                }
            }
            child = mutate(child, mutationRate);
            newPopulation[i] = child;
        }
        return newPopulation;
    }

    private static String[] mutate(String[] child, double mutationRate){
        Random gen = new Random();
        for (int i = 0; i < child.length; i++){
            double chance = gen.nextDouble() * 1;
            if (chance < mutationRate){
                child[i] = LEXICON[gen.nextInt(LEXICON.length)];
            }
        }
        return child;
    }

    private static List<Double> assignProbability(List<Double> scores){
        List<Double> probability = new ArrayList<>();
        Double max = Collections.max(scores);
        Double min = Collections.min(scores);

        for (Double score : scores){
            Double normalized = (score - min)/(max - min);
            probability.add(normalized*5);
        }
        return probability;
    }

    private static int weightedSelection(double[] probability){
        double totalWeight = 0;
        List<Double> range = new ArrayList<>();
        for (double prob : probability) {
            totalWeight += prob;
            range.add(totalWeight);
        }

        double index = new Random().nextDouble()*totalWeight;
        for(int i = 0; i < range.size(); i++){
            if (index < range.get(i)){
                return i;
            }
        }
        return 0;
    }

    private static boolean equals(String[] array, String[] target) {
        boolean check = false;
        int count = 0;
        for (int i = 0; i < target.length; i++) {
            if (array[i].equals(target[i])) {
                count++;
            }
        }
        if(count == target.length){
            check = true;
        }
        return check;
    }

    public static void displayPopulation(String[][] board){
        System.out.println("---------------------");
        for ( String[] row : board) {
            System.out.println(Arrays.toString(row));
        }
        System.out.println("---------------------");
    }

    private static Integer[] sort(Integer[] array1){
        Integer [] array = array1;
        for (int i = 0; i < array.length; i++){
            for(int j = 0; j < array.length - i - 1; j++){
                if (array[j] < array[j+1]){
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
        return array;
    }
}
