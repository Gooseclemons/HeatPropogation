package main;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorOperators;

import java.util.concurrent.CyclicBarrier;

/**
 * Class responsible for doing work on individual sub-surfaces before writing results to a temporary surface that
 *  will later replace the main surface
 */
public class Worker extends Thread {

    // Parent surface and temp surface to write to
    Surface parent;

    // Subsurface this thread possesses
    Subsurface subsurface;

    // Heating coefficients for the metal types
    double[] heatCoefficients;
    DoubleVector coefficientVector;

    // Barrier Object for thread syncronization
    CyclicBarrier barrier;

    Worker(Subsurface subsurface, Surface parent, CyclicBarrier barrier) {
        this.subsurface = subsurface;
        this.parent = parent;
        heatCoefficients = new double[] {0.75, 1.0, 1.25, 0};
        coefficientVector = DoubleVector.fromArray(DoubleVector.SPECIES_256, heatCoefficients, 0);
        this.barrier = barrier;
    }

    @Override
    public void run() {
        /*
         * - for region in subsurface bounds
         * - get neighbors and apply formula onto individual cell based on neighbor
         * - (optional) improve performance by having the equation apply both ways while ones loaded in memory
         * -
         */
        for (;;) {
            try {
//                calculateRegions();
                SIMD_Regions();
                barrier.await();
                Thread.sleep(5);
            } catch (Exception e) {

            }
        }
    }

    void calculateRegions() {
        Region[][] surface = parent.surface;
        double[][] next_temps = parent.next_temperatures;
        for (int i = 0; i < parent.width; i++) { // Width loop
            for (int j = subsurface.left; j <= subsurface.right; j++) { // Length loop
                Region region = surface[i][j];
                double newTemp = calculateTemperature(region);
                next_temps[i][j] = newTemp;
            }
        }
    }

    double calculateTemperature(Region region) {
        double newTemp = 0;
        int n = region.neighbors.length;
        for (int i = 0; i < heatCoefficients.length; i++) {
            double cm = heatCoefficients[i];
            for (int j = 0; j < n; j++) {
                Region neighbor = region.neighbors[j];
                double temp_n = neighbor.getTemperature();
                double pn = neighbor.metalContent[i];
                newTemp += cm * (temp_n * pn);
            }
        }
        return newTemp / n;
    }

    void SIMD_Regions() {
        Region[][] surface = parent.surface;
        double[][] next_temps = parent.next_temperatures; // Maybe vectorify for simd later, need simd in surface class in that case
        for (int i = 0; i < parent.width; i++) {
            for (int j = subsurface.left; j <= subsurface.right; j++) {
                Region region = surface[i][j];
                next_temps[i][j] = SIMD_Temperature(region);
            }
        }
    }

    double SIMD_Temperature(Region region) {
//        int n = region.neighbors.length;
//        double[] neighborSummedTemps = new double[4];
//        for (int i = 0; i < n; i++) { // Cycle through each neighbor
//            Region neighbor = region.neighbors[i];
//            DoubleVector metalPercentages = neighbor.metalVector;
//            DoubleVector tempByPercent = metalPercentages.mul(neighbor.getTemperature());
//            double percentTemp = tempByPercent.reduceLanes(VectorOperators.ADD);
//            neighborSummedTemps[i] = percentTemp;
//        }
//        DoubleVector vectorSumTemps = DoubleVector.fromArray(DoubleVector.SPECIES_256, neighborSummedTemps, 0);
//        DoubleVector cmTemps = coefficientVector.mul(vectorSumTemps).div(n);
//        return cmTemps.reduceLanes(VectorOperators.ADD);
        int n = region.neighbors.length;
        double[] neighborSums = new double[4];
        for (int i = 0; i < n; i++) {
            Region neighbor = region.neighbors[i];
            double temp_n = neighbor.getTemperature();
            DoubleVector tempByPercent = neighbor.metalVector.mul(temp_n); // [0.33temp_n, 0.34temp_n, 0.33temp_n]
            DoubleVector cmAdjustedVector = tempByPercent.mul(coefficientVector); // [0.33temp_n*c[0], 0.34temp_n*c[1], 0.33temp_n*c[2]]
            neighborSums[i] = cmAdjustedVector.reduceLanes(VectorOperators.ADD); // [sum of above vector values]
        }
        DoubleVector neighborTempVector = DoubleVector.fromArray(DoubleVector.SPECIES_256, neighborSums, 0);
        neighborTempVector = neighborTempVector.div(n);
        return neighborTempVector.reduceLanes(VectorOperators.ADD);
    }

}
