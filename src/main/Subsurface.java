package main;

import java.util.concurrent.atomic.AtomicBoolean;

public class Subsurface {

    // Parent surface object
    Surface parent;

    // Bounds of the subsurface in relation to the parent surface
    int left, right;

    Subsurface(Surface parent, int left, int right) {
        this.parent = parent;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "left: " + left + "   right: " + right;
    }

}
