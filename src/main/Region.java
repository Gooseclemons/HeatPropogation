package main;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.IntVector;

import java.util.concurrent.ThreadLocalRandom;

public class Region {

    // Tracks temperature of this individual region
    double temperature;

    // Tracks the percentages of metal contained within this region, array based for access
    double[] metalContent;
    DoubleVector metalVector;

    // Keeps track of neighbors this region has
    Region[] neighbors;

    /**
     * Temperature will default to 0 degrees initially before being changed by heat sources
     */
    Region() {
        temperature = 0;
        metalContent = new double[4];
        generateMetalContent();
        metalVector = DoubleVector.fromArray(DoubleVector.SPECIES_256, metalContent, 0); // Can't have unused bits?
    }

    /**
     * Constructor for the purpose of simulating the heated corners without the heating temperature getting changed
     * @param temperature
     */
    Region(double temperature) {
        this.temperature = temperature;
        metalContent = new double[] {0.33, 0.34, 0.33, 0}; // Zero is needed as a placeholder value for the lane
        metalVector = DoubleVector.fromArray(DoubleVector.SPECIES_256, metalContent, 0);
    }

    void generateMetalContent() {
//        double remainingContent = 1.00;
//        for (int i = 0; i < 2; i++) {
//            double noise = ThreadLocalRandom.current().nextDouble(0.8, 1.20);
//            double content = 0.33 * noise;
//            metalContent[i] = content;
//            remainingContent -= content;
//        }
//        metalContent[2] = remainingContent;
        metalContent[0] = 0.33;
        metalContent[1] = 0.34;
        metalContent[2] = 0.33;
        metalContent[3] = 0;
    }

    void printMetalContent() {
        System.out.print("[" + metalContent[0] + ", " + metalContent[1] + ", " + metalContent[2] + "]");
    }

    void setNeighbors(Region[] neighbors) {
        this.neighbors = neighbors;
    }

    void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    double getTemperature() {
        return temperature;
    }

}
