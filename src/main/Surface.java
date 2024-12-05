package main;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class responsible for representing the regions on the piece of metal and providing record keeping/access for the
 *  worker threads to act on
 * Key functionalities include updating/adding to individual regions and
 */
public class Surface {

    // Dimensions of the surface
    int width, length;

    // Designed to indicate how far apart each subsurface is
    int delta;

    // Heat applied to corners
    double heat1, heat2;

    // Array of each individual region, [0,0] being the uppermost left region and [length-1,width-1] being the lowest
    //  right region
    Region[][] surface;

    // Sub-surfaces of the surface
    Subsurface[] sub_surfaces;

    // Thread counter for subsurface splitting
    int num_threads;

    Surface(int width, int length, int num_threads, double heat1, double heat2) {
        this.width = width;
        this.length = length;
        surface = new Region[width][length];
        this.heat1 = heat1;
        this.heat2 = heat2;

        this.num_threads = num_threads;
        if (length % num_threads == 0) {
            sub_surfaces = new Subsurface[num_threads];
            delta = length / num_threads;
        } else {
            sub_surfaces = new Subsurface[1];
            delta = length - 1;
        }

        initializeRegions();
        generateRegionNeighbors();
    }

    void createSubsurfaces() {
        for (int i = 0; i < num_threads; i++) {
            int left = i * delta;
            int right = left + delta - 1;
            sub_surfaces[i] = new Subsurface(this, left, right);
        }
    }

    void initializeRegions() {
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                surface[j][i] = new Region();
            }
        }
    }

    void generateRegionNeighbors() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < length; j++) {
                ArrayList<Region> neighbors = new ArrayList<>();
                Region region = surface[i][j];

                // Width edge case check
                if (i != 0 && i != width - 1) {
                    Region neighbor1 = surface[i+1][j];
                    Region neighbor2 = surface[i-1][j];
                    neighbors.add(neighbor1);
                    neighbors.add(neighbor2);
                } else {
                    Region neighbor = (i == 0) ? surface[i+1][j] : surface[i-1][j];
                    neighbors.add(neighbor);
                }

                // Length edge case check
                if (j != 0 && j != length - 1) {
                    Region neighbor1 = surface[i][j+1];
                    Region neighbor2 = surface[i][j-1];
                    neighbors.add(neighbor1);
                    neighbors.add(neighbor2);
                } else {
                    Region neighbor = (j == 0) ? surface[i][j+1] : surface[i][j-1];
                    neighbors.add(neighbor);
                }

                // Heated corner cases
                if (i == 0 && j == 0) {
                    Region neighbor = new Region(50);
                    neighbors.add(neighbor);
                }

                region.setNeighbors(Arrays.copyOf(neighbors.toArray(), neighbors.size(), Region[].class));
            }
        }
    }

    void printSurfaceTemperatures() {
        System.out.println();
        for (Region[] row : surface) {
            for (Region region : row) {
                System.out.print("[" + region.getTemperature() + "]");
            }
            System.out.println();
        }
    }

}
