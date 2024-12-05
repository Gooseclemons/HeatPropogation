package main;

import java.util.Arrays;
import java.util.concurrent.CyclicBarrier;

/**
 * Main class responsible for setting constants and running the simulation
 */
public class HeatPropagation {

    // Thread constants
    static int num_threads = 4;

    // Temperatures being applied to the top left (heat1) and bottom right (heat2) corners of the surface
    static double heat1 = 70;
    static double heat2 = 50;

    // Dimensions of the surface
    static int RECT_WIDTH = 4;
    static int RECT_LENGTH = RECT_WIDTH * 4;

    // java.Surface objects, the nextSurface variable acts as a template to map calculated changes onto
    //  before overwriting the currentSurface variable
    static Surface currentSurface = new Surface(RECT_WIDTH, RECT_LENGTH, num_threads, heat1, heat2);
    static Surface nextSurface = new Surface(RECT_WIDTH, RECT_LENGTH, num_threads, heat1, heat2);

    // Worker objects for managing the surfaces
    static Worker[] threads = new Worker[num_threads];

    // Barrier for Threads to act on
    static CyclicBarrier barrier = new CyclicBarrier(num_threads, HeatPropagation::barrierAction);

    public static void main(String[] args) {

        currentSurface.createSubsurfaces();
        for (int i = 0; i < currentSurface.sub_surfaces.length; i++) {
            System.out.println(currentSurface.sub_surfaces[i].toString());
        }

        for (Region[] regions : currentSurface.surface) {
            for (Region region : regions) {
                region.printMetalContent();
            }
            System.out.println();
        }

        initializeWorkers();
        startWorkers();

    }

    static void initializeWorkers() {
        Subsurface[] sub_surfaces = currentSurface.sub_surfaces;
        for (int i = 0; i < num_threads; i++) {
            Subsurface sub_surface = sub_surfaces[i];
            threads[i] = new Worker(sub_surface, currentSurface, nextSurface, barrier);
        }
    }

    static void startWorkers() {
        for (int i = 0; i < num_threads; i++)
            threads[i].start();
    }

    // Copy may be overwriting current surface to a brand new surface
    static void barrierAction() {
        currentSurface = nextSurface;
        nextSurface = new Surface(RECT_WIDTH, RECT_LENGTH, num_threads, heat1, heat2);
        barrier = new CyclicBarrier(num_threads, HeatPropagation::barrierAction);
        currentSurface.printSurfaceTemperatures();
    }

}
