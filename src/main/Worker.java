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
    double c1, c2, c3;

    // Barrier Object for thread syncronization
    CyclicBarrier barrier;

    Worker(Subsurface subsurface, Surface parent, Surface temp, CyclicBarrier barrier) {
        this.subsurface = subsurface;
        this.parent = parent;
        this.temp = temp;
        c1 = 0.75; c2 = 1.0; c3 = 1.25;
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
                System.out.println("yay");
                barrier.await();
                Thread.sleep(1000);
            } catch (Exception e) {

            }
        }
    }

    double calculateNewTemperature() {
        return 0;
    }

}
