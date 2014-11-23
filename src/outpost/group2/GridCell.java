package outpost.group2;

import outpost.sim.Pair;
import outpost.sim.Point;

public class GridCell extends Pair {
    public boolean hasWater;
    public double distance;
    public double waterValue;
    public int landValue;
    public int owner;
    public int distToOutpost;
    // Array indexed by player ids
    public boolean[] hasSupplyLine;

    public GridCell(int xx, int yy, boolean wt) {
        x = xx;
        y = yy;
        hasWater = wt;
        this.waterValue = 0;
        this.owner = -1;
        this.distToOutpost = Integer.MAX_VALUE;
        this.hasSupplyLine = new boolean[4];
        for (int i = 0; i < hasSupplyLine.length; i++)
        	hasSupplyLine[i] = false;
    }

    public GridCell(Point o) {
        this(o.x, o.y, o.water);
    }

    
    public boolean equals(GridCell o) {
        return o.x == x && o.y == y && o.hasWater == hasWater;
    }
}
