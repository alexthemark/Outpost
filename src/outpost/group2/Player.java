package outpost.group2;

import java.util.*;

import outpost.sim.Pair;
import outpost.sim.Point;
import outpost.sim.movePair;

public class Player extends outpost.sim.Player {
	private static int BOARD_SIZE = 100;
	private Pair homespace;
	private int currentTick;
	
	public HashMap<Integer, Pair> homespaces = new HashMap<Integer, Pair>();
	
    public Player(int id_in) {
		super(id_in);
	}

	public void init() {
		homespaces.put(0, new Pair(0,0));
		homespaces.put(1, new Pair(BOARD_SIZE - 1,0));
		homespaces.put(2, new Pair(BOARD_SIZE -1, BOARD_SIZE -1));
		homespaces.put(3, new Pair(0, BOARD_SIZE -1));
		homespace = homespaces.get(id);
		currentTick = 0;
    }
    
    static double distance(Point a, Point b) {
        return Math.sqrt((a.x-b.x) * (a.x-b.x) +
                         (a.y-b.y) * (a.y-b.y));
    }
    
	public ArrayList<movePair> move(ArrayList<ArrayList<Pair>> outpostPairs, Point[] gridin, int influenceDist, int L, int W, int T){
    	long startTime = System.currentTimeMillis();
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
    	try {
	    	board.updateSupplyLines(homespaces);
	    	Resource ourResources = board.getResources(this.id); 
	    	int waterMultiplier = calculateResourceMultiplier(outposts.get(id).size(), W, ourResources.water);
	    	System.out.println("[GROUP2][LOG] Water multiplier: " + waterMultiplier);
	    	int landMultiplier = calculateResourceMultiplier(outposts.get(id).size(), L, ourResources.land);
	    	System.out.println("[GROUP2][LOG] Land multiplier: " + landMultiplier);
	    	board.calculateResourceValues(this.id, influenceDist, waterMultiplier, landMultiplier);
    	} catch (OwnersNotUpdatedException e) {
    		System.err.println("[GROUP2][ERROR] Must update the owners before calling this function");
    	}
 
    	// Great, now we know the resource value of each cell. Let's now look at each outpost, and see
    	// where it will do best offensively and defensively.
    	for (int outpostId = 0; outpostId < outposts.get(id).size(); outpostId++) {
    		Outpost outpost = outposts.get(id).get(outpostId);
    		double bestScore = 0;
    		Pair bestPair = new Pair();
    		ArrayList<Pair> surroundingPairs = surroundingPairs(outpost, board);
    		for (Pair testPos : surroundingPairs) {
    			int defensiveVal = board.calculateDefensiveScore(outpost, testPos, id, homespace);
    			double waterResourceVal = board.getWaterResourceVal(testPos);
    			double landResourceVal = board.getLandResourceVal(testPos);
    			int offensiveVal = board.calculateOffensiveScore(outpost, testPos, id, influenceDist);
    			double currentScore = waterResourceVal + landResourceVal + defensiveVal + (currentTick/T) * offensiveVal;
    			if (currentScore > bestScore) {
    				bestScore = currentScore;
    				bestPair = testPos;
    			}
    		}
    		movePair thisMove = new movePair(outpostId, bestPair);
    		ourNextMoves.add(thisMove);
    	}
    	long endTime = System.currentTimeMillis();
    	System.out.println("[GROUP2][LOG] Took " + (endTime - startTime) + "milliseconds");
    	return ourNextMoves;
	}
	
	private ArrayList<Pair> surroundingPairs(Pair startingPair, GameBoard board) {
		int MOVE_DIST = 1;
		Pair testPair;
		ArrayList<Pair> surroundingPairs = new ArrayList<Pair>();
		
		testPair = new Pair(startingPair.x, startingPair.y);
		if (board.validCellForMoving(testPair))
			surroundingPairs.add(testPair);
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
		int SAFE_OUTPOST_NO = 5;
		int START_COLLECTING_OUTPOST_NO = 3;
		int usedResources = resourceToBuildOutpost * numberOfCurrentOutposts;
		int freeResources = currentResourceAmount - usedResources;
		// First, if we have enough of the resource that we can build SAFE_OUTPOST_NO, we don't really need the resource
		if (freeResources > SAFE_OUTPOST_NO * resourceToBuildOutpost)
			return 0;
		// Next, if we want to start collecting that resource, return 1;
		else if (freeResources > START_COLLECTING_OUTPOST_NO * resourceToBuildOutpost)
			return 1;
		// Next, we won't be able to build at the end of this round without it, so we really want the resource
		else if (freeResources > 0)
			return 3;
		// Lastly, we don't have enough resources to support our current army... so we're really desperate
		else 
			return 5;	
	}
    
    // For now, we delete the newest outpost. Future work: 
    public int delete(ArrayList<ArrayList<Pair>> king_outpostlist, Point[] gridin) {
    	int del = king_outpostlist.get(id).size() - 1;
    	return del;
    }
}
