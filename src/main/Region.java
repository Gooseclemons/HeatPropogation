package main;

import java.util.concurrent.ThreadLocalRandom;

public class Region {

    // Tracks temperature of this individual region
    double temperature;

    // Tracks the percentages of metal contained within this region, array based for access
    double[] metalContent;

    // Keeps track of neighbors this region has
    Region[] neighbors;

    /**
     * Temperature will default to 0 degrees initially before being changed by heat sources
     */
    Region() {
        temperature = 0;
        metalContent = new double[3];
        generateMetalContent();
    }

    Region(double temperature) {
        this.temperature = temperature;
        metalContent = new double[3];
        generateMetalContent();
    }

    void generateMetalContent() {
        double remainingContent = 1.00;
        for (int i = 0; i < 2; i++) {
            double noise = ThreadLocalRandom.current().nextDouble(0.8, 1.20);
            double content = 0.33 * noise;
            metalContent[i] = content;
            remainingContent -= content;
        }
        metalContent[2] = remainingContent;
    }

    void calculateHeat() {

    }

    void printMetalContent() {
        System.out.print("[" + metalContent[0] + ", " + metalContent[1] + ", " + metalContent[2] + "]");
    }

    void setNeighbors(Region[] neighbors) {
        try {
            this.neighbors = neighbors;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    double getTemperature() {
        return temperature;
    }

}
