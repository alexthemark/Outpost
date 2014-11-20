package outpost.group2;

import java.util.*;

import outpost.sim.Pair;
import outpost.sim.Point;
import outpost.sim.movePair;

public class Board {
	int size;
	int r;
	int[] value;
	int playerID;
	int outpostID;
	ArrayList<ArrayList<Pair>> king_outpostlist;
	Point[] grid;

	// CONSTRUCTORS
	Board(int size, Point grid[], ArrayList<ArrayList<Pair>> king_outpostlist,
			int playerID, int outpostID, int r) {
		this.size = size;
		this.grid = grid;
		value = new int[size * size];
		this.playerID = playerID;
		this.outpostID = outpostID;
		this.r = r;
		this.king_outpostlist = king_outpostlist;
	}

	Board(Board board) {
		this.size = board.size;
		this.value = board.value;
		this.playerID = board.playerID;
		this.outpostID = board.outpostID;
		this.r = board.r;
		this.king_outpostlist = board.king_outpostlist;
	}

	static double distance(Point a, Point b) {
		return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
	}

	static double distance(Point a, Pair b) {
		return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
	}

	static double distance(Pair a, Pair b) {
		return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
	}

	// PUBLIC METHODS

	public Resource caculatePointWaterAndLand(Pair pr) {
		// System.out.println("-------------------------");
		Resource re = new Resource();
		int water = 0;
		int land = 0;
		for (int i = 0; i < size * size; i++) {
			if (distance(grid[i], pr) < r) {
				if (grid[i].ownerlist.size() <= 1) {
					if (grid[i].ownerlist.size() == 0) {
						if (grid[i].water) {
							water++;
						} else {
							land++;
						}
						// System.out.println("neutral res becomes mine!");
					} else if (grid[i].ownerlist.get(0).x == this.playerID
							&& grid[i].ownerlist.get(0).y == this.outpostID) {
						if (grid[i].water) {
							water++;
						} else {
							land++;
						}
						// System.out.println("res belongs to prevois and current!");
					}
				}
			}
		}
		re.land = land;
		re.water = water;
		return re;
	}

	public double calResource() {
		return 0;
	}

	public double calDefensive(Pair pr) {
		double min_dis = 1000;
		double dis;
		for (int i = 0; i < king_outpostlist.get(playerID).size(); i++) {
			if (i != outpostID) {
				dis = distance(king_outpostlist.get(playerID).get(i), pr);
				if (dis < min_dis)
					min_dis = dis;
			}
		}
		if (min_dis < 20)
			return 0;
		else
			return (min_dis - 20) * 10;
	}

	public double calOffensive() {
		return 0;
	}

	public boolean validPair(Pair pair) {
		if (pair.x < 0 || pair.x >= size || pair.y < 0 || pair.y >= size)
			return false;
		if (grid[pair.x * size + pair.y].water) {
			return false;
		}

		return true;
	}

	public ArrayList<Pair> surround(Pair start) {
		// System.out.printf("start is (%d, %d)", start.x, start.y);
		ArrayList<Pair> prlist = new ArrayList<Pair>();
		for (int i = 0; i < 4; i++) {
			Pair tmp0 = new Pair(start);
			Pair tmp;
			if (i == 0) {
				// if (start.x>0) {
				tmp = new Pair(tmp0.x - 1, tmp0.y);
				// if (!PairtoPoint(tmp).water)
				if (validPair(tmp))
					prlist.add(tmp);
				// }
			}
			if (i == 1) {
				// if (start.x<size-1) {
				tmp = new Pair(tmp0.x + 1, tmp0.y);
				// if (!PairtoPoint(tmp).water)
				if (validPair(tmp))
					prlist.add(tmp);
				// }
			}
			if (i == 2) {
				// if (start.y>0) {
				tmp = new Pair(tmp0.x, tmp0.y - 1);
				// if (!PairtoPoint(tmp).water)
				if (validPair(tmp))
					prlist.add(tmp);
				// }
			}
			if (i == 3) {
				// if (start.y<size-1) {
				tmp = new Pair(tmp0.x, tmp0.y + 1);
				// if (!PairtoPoint(tmp).water)
				if (validPair(tmp))
					prlist.add(tmp);
				// }
			}

		}

		return prlist;
	}

	public movePair getNextMove() // calculate the value of each cell for the
									// outpost
	{
		double tmp_score;
		double max_score = -1000;
		double defensive_score;
		double offensive_score;
		double resource_score = 0;

		Pair best_pair = new Pair(
				king_outpostlist.get(playerID).get(outpostID).x + 1,
				king_outpostlist.get(playerID).get(outpostID).y);
		ArrayList<Pair> positions = new ArrayList<Pair>();
		positions = surround(king_outpostlist.get(playerID).get(outpostID));
		positions.add(king_outpostlist.get(playerID).get(outpostID));
		// for(int i = 0; i < size; i++) // for each cell, calculate the value
		for (int i = 0; i < positions.size(); i++) {
			Resource res = caculatePointWaterAndLand(positions.get(i));
			// tmp_score = (res.land + res.water) + res.water; // wield rule...
			if (playerID == 0)
				resource_score = res.land + res.water + positions.get(i).x
						+ positions.get(i).y;
			if (playerID == 1)
				resource_score = res.land + res.water + size - 1
						- positions.get(i).x + positions.get(i).y;
			if (playerID == 2)
				resource_score = res.land + res.water + positions.get(i).x
						+ size - 1 - positions.get(i).y;
			if (playerID == 3)
				resource_score = res.land + res.water + size - 1
						- positions.get(i).x + size - 1 - positions.get(i).y;
			defensive_score = calDefensive(positions.get(i));

			tmp_score = resource_score - defensive_score; // wield rule...
			// System.out.println("water: " + res.water + ", land: " +
			// res.land);
			// System.out.println("playerID: " + playerID + ", outpostID: "
			// + outpostID + "score: " + tmp_score);

			if (tmp_score > max_score) {
				max_score = tmp_score;
				best_pair = positions.get(i);
			}

		}
		/*
		 * System.out.println("playerID: " + playerID + ", outpostID: " +
		 * outpostID + "score: " + max_score);
		 * System.out.println("best pair x: " + best_pair.x + ", y: " +
		 * best_pair.y);
		 */
		return new movePair(outpostID, best_pair);
	}
}
