package main;

import java.util.concurrent.CyclicBarrier;

/**
 * Class responsible for doing work on individual sub-surfaces before writing results to a temporary surface that
 *  will later replace the main surface
 */
public class Worker extends Thread {

    // Parent surface and temp surface to write to
    Surface parent, temp;

    // Subsurface this thread possesses
    Subsurface subsurface;

    // Heating coefficients for the metal types
    double[] heatCoefficients;

    // Barrier Object for thread syncronization
    CyclicBarrier barrier;

    Worker(Subsurface subsurface, Surface parent, Surface temp, CyclicBarrier barrier) {
        this.subsurface = subsurface;
        this.parent = parent;
        this.temp = temp;
        heatCoefficients = new double[] {0.75, 1.0, 1.25};
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
                calculateRegions();
                barrier.await();
                Thread.sleep(1000);
            } catch (Exception e) {

            }
        }
    }

    void calculateRegions() {
        Region[][] surface = temp.surface;
        for (int i = 0; i < parent.width; i++) { // Width loop
            for (int j = subsurface.left; j <= subsurface.right; j++) { // Length loop
                Region region = surface[i][j];
                double newTemp = calculateTemperature(region);
                region.setTemperature(newTemp); // NOOOOOOOOOOOOOOOOOOOOOOOOO
                // Need to set the temperature of the
            }
        }
    }

    double calculateTemperature(Region region) {
        double newTemp = 0;
        for (int i = 0; i < heatCoefficients.length; i++) {
            double cm = heatCoefficients[i];
            int n = region.neighbors.length;
            for (int j = 0; j < n; j++) {
                Region neighbor = region.neighbors[j];
                double temp_n = neighbor.getTemperature();
                double pn = neighbor.metalContent[i];
                newTemp += cm * ((temp_n * pn) / n);
            }
        }
        return newTemp;
    }

}
