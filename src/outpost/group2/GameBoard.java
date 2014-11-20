package outpost.group2;

import java.util.ArrayList;

import outpost.sim.Pair;
import outpost.sim.Point;

public class GameBoard {
	private static final int BOARD_SIZE = 100;
	GridCell[][] grid = new GridCell[BOARD_SIZE][BOARD_SIZE];
	// this list is first indexed by the player's id, and then gives their list of outposts.
	ArrayList<ArrayList<Pair>> outposts;
	
	public GameBoard(Point[] oneDimensionalGrid) {
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				grid[i][j] = new GridCell(oneDimensionalGrid[i*BOARD_SIZE+j]);
			}
		}
	}
	
	public void calculateResourceValues(int influenceDistance, int waterMultiplier, int landMultiplier) {
		GridCell currentCell;
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				currentCell = grid[i][j];
				if (validCellForMoving(currentCell)) {
					ArrayList<GridCell> cellsUnderInfluence = getInfluencedCells(currentCell, influenceDistance);
					for (GridCell cell : cellsUnderInfluence) {
						if (cell.hasWater)
							currentCell.value += waterMultiplier;
						else
							currentCell.value += landMultiplier;
					}
				}
			}
		}
	}
	
	public void updateCellOwners(ArrayList<ArrayList<Pair>> outposts, int influenceDistance) {
		for (int outpostId = 0; outpostId < outposts.size(); outpostId++) {
			for (Pair outpost : outposts.get(outpostId)) {
				ArrayList<GridCell> cellsUnderInfluence = getInfluencedCells(outpost, influenceDistance);
				for (GridCell cell : cellsUnderInfluence) {
					int dist = manhattanDist(cell, outpost);
					if (dist < cell.distToOutpost) {
						cell.owner = outpostId;
						cell.distToOutpost = dist;
					}
				}
			}
		}
	}
	
	public Resource getResources(int id) {
		Resource resourcesToReturn = new Resource();
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				if (grid[i][j].hasWater && grid[i][j].owner == id) {
					resourcesToReturn.water++;
				}
				else if (grid[i][j].owner == id)
					resourcesToReturn.land++;
			}
		}
		return resourcesToReturn;
	}
	
	private ArrayList<GridCell> getInfluencedCells(Pair center, int influenceDistance) {
		Pair checkCell;
		ArrayList<GridCell> influencedCells = new ArrayList<GridCell>();
		for (int i = -1 * influenceDistance; i < influenceDistance; i++) {
			for (int j = -1 * influenceDistance; j < influenceDistance; j++) {
				checkCell = new Pair(center.x + i, center.y + j);
				if (validCellForAnalysis(checkCell)) {
					influencedCells.add(grid[center.x+i][center.y+j]);
				}
			}
		}
		return influencedCells;
	}
	
	private int manhattanDist(Pair pr1, Pair pr2) {
		return (Math.abs(pr1.x - pr2.x) + Math.abs(pr1.y - pr2.y));
	}
	
	private boolean validCellForAnalysis(Pair pair) {
		if (pair.x < 0 || pair.x >= BOARD_SIZE || pair.y < 0 || pair.y >= BOARD_SIZE)
			return false;
		else return true;
	}
	
	private boolean validCellForMoving(Pair pair) {
		if (!validCellForAnalysis(pair) || grid[pair.x][pair.y].hasWater)
			return false;
		return true;
	}
}
