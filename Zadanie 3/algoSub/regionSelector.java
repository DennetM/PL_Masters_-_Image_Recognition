package algoSub;

public class regionSelector {

	//Variables.
	private boolean[][] checkMap; //This map will remember which pixels we've already run through.
	
	//Constructor - each selector is tied to only one image!
	public regionSelector(int sizeX, int sizeY){
		this.checkMap = new boolean[sizeX][sizeY];
		for(int x=0; x<sizeX; x++){
			for(int y=0; y<sizeY; y++){
				this.checkMap[sizeX][sizeY] = false;
			}
		}
	}
	
	//Functions.
	//Gets, since we're not setting anything in this class.
	public boolean[][] getMap(){ return this.checkMap; }
	public boolean checkXY(int x, int y){ return this.checkMap[x][y]; }
	
	//markRegion - taking in the seed x and y, as well as a threshold value, this function updates the checkMap.
	public void markRegion(int seedX, int seedY, double threshold, int[][][] img){
		//TODO:EVERYTHING
	}
}