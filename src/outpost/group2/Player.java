package outpost.group2;

import java.util.*;

import outpost.sim.Pair;
import outpost.sim.Point;
import outpost.sim.movePair;

public class Player extends outpost.sim.Player {
	static int size = 100;
	GridCell homespace;
	Resource ourResources;
	int ticks;
	
    public Player(int id_in) {
		super(id_in);
	}

	public void init() {
		if (id == 0) {
			homespace = new GridCell(0,0,false);
		}
		else if (id == 1) {
			homespace = new GridCell(size - 1,0,false);
		}
		else if (id == 2) {
			homespace = new GridCell(size - 1,size - 1,false);
		}
		else {
			homespace = new GridCell(0, size - 1,false);
		}
		ticks = 0;
    }
    
    static double distance(Point a, Point b) {
        return Math.sqrt((a.x-b.x) * (a.x-b.x) +
                         (a.y-b.y) * (a.y-b.y));
    }
    
	public ArrayList<movePair> move(ArrayList<ArrayList<Pair>> outposts, Point[] gridin, int influenceDist, int L, int W, int T){
    	ticks++;
		ArrayList<movePair> ourNextMoves = new ArrayList<movePair>();    	
    	GameBoard board = new GameBoard(gridin);
    	board.updateCellOwners(outposts, influenceDist);
    	Resource ourResources = board.getResources(this.id); 
    	int waterMultiplier = calculateResourceMultiplier(outposts.get(id).size(), W, ourResources.water);
    	int landMultiplier = calculateResourceMultiplier(outposts.get(id).size(), L, ourResources.land);
    	board.calculateResourceValues(influenceDist, waterMultiplier, landMultiplier);
 
    	return ourNextMoves;
	}
	
	private int calculateResourceMultiplier(int numberOfCurrentOutposts, int resourceToBuildOutpost, int currentResourceAmount) {
		return 0;
	}
    
    // For now, we delete the newest outpost. 
    public int delete(ArrayList<ArrayList<Pair>> king_outpostlist, Point[] gridin) {
    	int del = king_outpostlist.get(id).size();
    	return del;
    }
}
