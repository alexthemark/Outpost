package outpost.group2;

import java.util.*;

import outpost.sim.Pair;
import outpost.sim.Point;
import outpost.sim.movePair;

public class Player extends outpost.sim.Player {
	private static int BOARD_SIZE = 100;
	private Pair homespace;
	private Resource ourResources;
	private int currentTick;
	
    public Player(int id_in) {
		super(id_in);
	}

	public void init() {
		if (id == 0) {
			homespace = new Pair(0,0);
		}
		else if (id == 1) {
			homespace = new Pair(BOARD_SIZE - 1,0);
		}
		else if (id == 2) {
			homespace = new Pair(BOARD_SIZE - 1,BOARD_SIZE - 1);
		}
		else {
			homespace = new Pair(0, BOARD_SIZE - 1);
		}
		currentTick = 0;
    }
    
    static double distance(Point a, Point b) {
        return Math.sqrt((a.x-b.x) * (a.x-b.x) +
                         (a.y-b.y) * (a.y-b.y));
    }
    
	public ArrayList<movePair> move(ArrayList<ArrayList<Pair>> outpostPairs, Point[] gridin, int influenceDist, int L, int W, int T){
    	currentTick++;
		ArrayList<movePair> ourNextMoves = new ArrayList<movePair>();   
		ArrayList<ArrayList<Outpost>> outposts = new ArrayList<ArrayList<Outpost>>();
		for (ArrayList<Pair> outpostPairList : outpostPairs) {
			ArrayList<Outpost> outpostList = new ArrayList<Outpost>();
			int id = 0;
			for (Pair outpostPair : outpostPairList) {
				outpostList.add(new Outpost(outpostPair, id));
				id++;
			}
			outposts.add(outpostList);
		}
		// First, let's update the game board based on the grid from the simulator
    	GameBoard board = new GameBoard(gridin, outposts);
    	board.updateCellOwners(influenceDist);
    	Resource ourResources = board.getResources(this.id); 
    	int waterMultiplier = calculateResourceMultiplier(outposts.get(id).size(), W, ourResources.water);
    	int landMultiplier = calculateResourceMultiplier(outposts.get(id).size(), L, ourResources.land);
    	board.calculateResourceValues(this.id, influenceDist, waterMultiplier, landMultiplier);
 
    	// Great, now we know the resource value of each cell. Let's now look at each outpost, and see
    	// where it will do best offensively and defensively.
    	for (int outpostId = 0; outpostId < outposts.get(id).size(); outpostId++) {
    		Outpost outpost = outposts.get(id).get(outpostId);
    		double bestScore = 0;
    		Pair bestPair = new Pair();
    		ArrayList<Pair> surroundingPairs = surroundingPairs(outpost, board);
    		for (Pair testPos : surroundingPairs) {
    			int defensiveVal = board.calculateDefensiveScore(outpost, testPos, id, homespace);
    			int resourceVal = board.getResourceVal(testPos);
    			int offensiveVal = board.calculateOffensiveScore(outpost, testPos, id, influenceDist);
    			double currentScore = 5 * resourceVal + defensiveVal + (currentTick/T) * offensiveVal;
    			if (currentScore > bestScore) {
    				bestScore = currentScore;
    				bestPair = testPos;
    			}
    		}
    		movePair thisMove = new movePair(outpostId, bestPair);
    		ourNextMoves.add(thisMove);
    	}
    	
    	return ourNextMoves;
	}
	
	private ArrayList<Pair> surroundingPairs(Pair startingPair, GameBoard board) {
		int MOVE_DIST = 1;
		Pair testPair;
		ArrayList<Pair> surroundingPairs = new ArrayList<Pair>();
		
		testPair = new Pair(startingPair.x, startingPair.y + MOVE_DIST);
		if (board.validCellForMoving(testPair))
			surroundingPairs.add(testPair);
		testPair = new Pair(startingPair.x, startingPair.y - MOVE_DIST);
		if (board.validCellForMoving(testPair))
			surroundingPairs.add(testPair);
		testPair = new Pair(startingPair.x + MOVE_DIST, startingPair.y);
		if (board.validCellForMoving(testPair))
			surroundingPairs.add(testPair);
		testPair = new Pair(startingPair.x - MOVE_DIST, startingPair.y);
		if (board.validCellForMoving(testPair))
			surroundingPairs.add(testPair);
		return surroundingPairs;
	}
	
	// Calculate how much we need a resource at this moment. 
	private int calculateResourceMultiplier(int numberOfCurrentOutposts, int resourceToBuildOutpost, int currentResourceAmount) {
		return 1;
	}
    
    // For now, we delete the newest outpost. Future work: 
    public int delete(ArrayList<ArrayList<Pair>> king_outpostlist, Point[] gridin) {
    	int del = king_outpostlist.get(id).size() - 1;
    	return del;
    }
}
