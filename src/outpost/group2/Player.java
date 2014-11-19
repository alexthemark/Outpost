package outpost.group2;

import java.util.*;

import outpost.sim.Pair;
import outpost.sim.Point;
import outpost.sim.movePair;

public class Player extends outpost.sim.Player {
	 static int size =100;
	static Point[] grid = new Point[size*size];
	static Random random = new Random();
	static int[] theta = new int[100];
	static int counter = 0;
	
    public Player(int id_in) {
		super(id_in);
		// TODO Auto-generated constructor stub
	}

	public void init() {
    	for (int i=0; i<100; i++) {
    		theta[i]=random.nextInt(4);
    	}
    }
    
    static double distance(Point a, Point b) {
        return Math.sqrt((a.x-b.x) * (a.x-b.x) +
                         (a.y-b.y) * (a.y-b.y));
    }
    
    // Return: the next position
    // my position: dogs[id-1]

    public boolean cellBelong(ArrayList<Pair> ownerList, ArrayList<Pair> outpostList)
    {
    	if(ownerList.size() > 1 || ownerList.size() == 0)
    		return false;
    	else
    	{
    		for(int i = 0; i < outpostList.size(); i++)
    		{
    			if(ownerList.get(0).equals(outpostList.get(i)))
    				return true;
    		}
    	}
    	return false;	
    }
    
    public Resource caculateOurWaterAndLand(Point[] gridin,ArrayList<Pair> outpostList)
    {
    	Resource re = new Resource();
    	int water=0;
    	int land=0;
    	for(int i=0;i<size*size;i++)
    	{
    		if(cellBelong(grid[i].ownerlist,outpostList))
    		{
    		if (grid[i].water) {
    				water++;
    					}
    					else {
    					land++;
    				
    		}
    	}
    	}
    	re.water=water;
    	re.land =land;
    	return re;
    }
    
    public int delete(ArrayList<ArrayList<Pair>> king_outpostlist, Point[] gridin) {
    	//System.out.printf("haha, we are trying to delete a outpost for player %d\n", this.id);
    	int del = random.nextInt(king_outpostlist.get(id).size());
    	return del;
    }
    
	//public movePair move(ArrayList<ArrayList<Pair>> king_outpostlist, int noutpost, Point[] grid) {
    public ArrayList<movePair> move(ArrayList<ArrayList<Pair>> king_outpostlist, Point[] gridin, int r, int L, int W, int T){
    	
    	/*Resource re =  caculateOurWaterAndLand(gridin,king_outpostlist.get(this.id));
    	double notNeedWater = 0;
    	double notNeedLand  = 0;
    	
    	int cur_need_water = W * (king_outpostlist.get(this.id).size());
    	int cur_need_land  = L * (king_outpostlist.get(this.id).size());
    	
    	notNeedWater = re.water /  cur_need_water;
    	notNeedLand  = re.land / cur_need_water;*/
    	
    	ArrayList<movePair> nextlist = new ArrayList<movePair>();
    	
    	for(int i = 0; i < king_outpostlist.get(this.id).size(); i++)
    	{
    		Board board = new Board(this.size, gridin, king_outpostlist, this.id, i, r);
    		movePair next = board.getNextMove();
    		nextlist.add(next);
    		//System.out.println("-----i: " + i + ", move to x: " + next.pr.x + ", y: " + next.pr.y);
    	}
 
    	return nextlist;
    
    }
    
    
    static ArrayList<Pair> surround(Pair start) {
   // 	System.out.printf("start is (%d, %d)", start.x, start.y);
    	ArrayList<Pair> prlist = new ArrayList<Pair>();
    	for (int i=0; i<4; i++) {
    		Pair tmp0 = new Pair(start);
    		Pair tmp;
    		if (i==0) {
    			//if (start.x>0) {
    			tmp = new Pair(tmp0.x-1,tmp0.y);
    	//		if (!PairtoPoint(tmp).water)
    			prlist.add(tmp);
    		//	}
    		}
    		if (i==1) {
    			//if (start.x<size-1) {
    			tmp = new Pair(tmp0.x+1,tmp0.y);
    		//	if (!PairtoPoint(tmp).water)
    			prlist.add(tmp);
    			//}
    		}
    		if (i==2) {
    			//if (start.y>0) {
    			tmp = new Pair(tmp0.x, tmp0.y-1);
    			//if (!PairtoPoint(tmp).water)
    			prlist.add(tmp);
    			//}
    		}
    		if (i==3) {
    			//if (start.y<size-1) {
    			tmp = new Pair(tmp0.x, tmp0.y+1);
    			//if (!PairtoPoint(tmp).water)
    			prlist.add(tmp);
    			//}
    		}
    		
    	}
    	
    	return prlist;
    }
    
    static Point PairtoPoint(Pair pr) {
    	return grid[pr.x*size+pr.y];
    }
    static Pair PointtoPair(Point pt) {
    	return new Pair(pt.x, pt.y);
    }
}
