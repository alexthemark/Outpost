package outpost.group2;

import outpost.sim.Pair;
import outpost.sim.Point;

public class GridCell extends Pair {
    public boolean hasWater;
    public double distance;
    public int value;
    public int owner;
    public int distToOutpost;

    public GridCell(int xx, int yy, boolean wt) {
        x = xx;
        y = yy;
        hasWater = wt;
        this.value = 0;
        this.owner = -1;
        this.distToOutpost = Integer.MAX_VALUE;
    }

    public GridCell(Point o) {
        this(o.x, o.y, o.water);
    }

    
    public boolean equals(GridCell o) {
        return o.x == x && o.y == y && o.hasWater == hasWater;
    }
}
