package main;

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

    // Array of each individual region, [0,0] being the uppermost left region and [length-1,width-1] being the lowest
    //  right region
    Region[][] surface;

    // Sub-surfaces of the surface
    Subsurface[] sub_surfaces;

    // Thread counter for subsurface splitting
    int num_threads;

    Surface(int width, int length, int num_threads) {
        this.width = width;
        this.length = length;
        surface = new Region[width][length];
        initializeRegions();
        this.num_threads = num_threads;
        if (length % num_threads == 0) {
            sub_surfaces = new Subsurface[num_threads];
            delta = length / num_threads;
        } else {
            sub_surfaces = new Subsurface[1];
            delta = length - 1;
        }
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

}
