package main;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorOperators;

import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.CyclicBarrier;

/**
 * Main class responsible for setting constants and running the simulation
 */
public class HeatPropagation {

    // Thread constants
    static int num_threads = 1;

    // Temperatures being applied to the top left (heat1) and bottom right (heat2) corners of the surface
    static double heat1 = 100;
    static double heat2 = 50;

    // Dimensions of the surface
    static int RECT_WIDTH = 4;
    static int RECT_LENGTH = RECT_WIDTH * 4;

    // GUI containing surface object
    static HeatDisplay display;

    // java.Surface objects, the nextSurface variable acts as a template to map calculated changes onto
    //  before overwriting the currentSurface variable
    static Surface currentSurface = new Surface(RECT_WIDTH, RECT_LENGTH, num_threads, heat1, heat2);

    // Worker objects for managing the surfaces
    static Worker[] threads = new Worker[num_threads];

    // Barrier for Threads to act on
    static CyclicBarrier barrier = new CyclicBarrier(num_threads, HeatPropagation::barrierAction);

    public static void main(String[] args) {

        display = new HeatDisplay(currentSurface);

        initializeWorkers();
        startWorkers();

//        IntVector vec = IntVector.fromArray(IntVector.SPECIES_128, new int[] {1, 2, 9, 2}, 0);
//        System.out.println(vec.reduceLanes(VectorOperators.ADD));

    }

    static void initializeWorkers() {
        Subsurface[] sub_surfaces = currentSurface.sub_surfaces;
        for (int i = 0; i < num_threads; i++) {
            Subsurface sub_surface = sub_surfaces[i];
            threads[i] = new Worker(sub_surface, currentSurface, barrier);
        }
    }

    static void startWorkers() {
        for (int i = 0; i < num_threads; i++)
            threads[i].start();
    }

    // Copy may be overwriting current surface to a brand new surface
    static void barrierAction() {
        currentSurface.updateTemperatures();
        display.updateGrid();
        barrier = new CyclicBarrier(num_threads, HeatPropagation::barrierAction);
        currentSurface.printSurfaceTemperatures();
    }

}
