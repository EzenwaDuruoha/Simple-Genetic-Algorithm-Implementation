package Utils;

import java.util.Arrays;

public class Generation {
    private String[][] population;
    private double[] fitnessScore;
    private double[] selectionProbability;


    public Generation(String[][] population, double[] fitnessScore){
        this.population = population;
        this.fitnessScore = fitnessScore;
        this.selectionProbability = new double[fitnessScore.length];
    }

    public double[] getFitnessScore() {
        return fitnessScore;
    }

    public String[][] getPopulation() {
        return population;
    }

    public void setSelectionProbability(int index, double prob){
        this.selectionProbability[index] = prob ;
    }

    public double[] getSelectionProbability(){
        return selectionProbability;
    }


    public void displayPopulation(){
        System.out.println("---------------------");
        int index = 0;
        for ( String[] row : this.population) {
            System.out.println(Arrays.toString(row) + " -----> " + this.fitnessScore[index]);
            index++;
        }
        System.out.println("---------------------");
    }


}
