package algoMain;

public class detectGrape {
	
	//Variables
	private int[][] checkMap; //A "binary" map that exists to hold greyscale values.
	private int[][][] img; //The image we're currently assessing.
	private int avgGW; // average grape width;
	private int avgGH; // average grape height;
	
	public int numberGreen =0; //number of green grapes.
	public int numberPurpl =0; //number of purple grapes.
	
	//Constructor - take in the image to test and the user-created average grape width and height.
	public detectGrape(int[][][] img, int x, int y){
		this.img = img;
		this.avgGW = x;
		this.avgGH = y;
		
		this.checkMap = new int[800][600]; //Fill it flatly, each grape image is standarized.
		
		countGrapes(); //Immedaitelly count them, no need to wait.
		
		System.out.println("Grapes counted for image. The results are in.");
		System.out.println("Number of green grapes: "+this.numberGreen);
		System.out.println("Number of purple grapes: "+this.numberPurpl);
	}
	
	
	//Functions:
	//countGrapes - this is a funny one. Basically, we'll scan the document from top to bottom, left to right.
	//When we encounter a pixel that's not pure white, we'll start marking it in the checkMap.
	//Then, we'll count the colours on that square and determine what sort of grape it is.
	//To prevent double-checking, we have to check two things. A) In the new checkbox, did we reach a previous one?
	//														   B) Is the new checkbox within X distance of another one?
	//Of course, the latter is set by us and has to be somewhat adjusted based on the minimal grape size, but that's fine-tuning.
	private void countGrapes(){
		//First loop set. Scan. By height.
		for(int x=0; x<800; x++){
			for(int y=0; y<600; y++){
				
				//If the pixel's colour average is off, meaning it's colourful, and not white or gray.
				int rg = Math.abs(this.img[0][x][y] - this.img[1][x][y]);
				int gb = Math.abs(this.img[1][x][y] - this.img[2][x][y]);
				int br = Math.abs(this.img[2][x][y] - this.img[0][x][y]);
				
				if(rg>30 || gb>30 || br>30){
					//Spawn a segment. Memory conservation be damned.
					int[][][] seg = new int[3][this.avgGW][this.avgGH];
					for(int xloc = 0; xloc<this.avgGW; xloc++){
						for(int yloc = 0; yloc<this.avgGH; yloc++){
							if(x+xloc<800 && y+yloc<600){
								seg[0][x+xloc][y+yloc] = this.img[0][x+xloc][y+yloc];
								seg[1][x+xloc][y+yloc] = this.img[1][x+xloc][y+yloc];
								seg[2][x+xloc][y+yloc] = this.img[2][x+xloc][y+yloc];
								
							}
						}
					}
					//After spawning, verify it's validity.
					if(checkSegmentValidity(seg)){
						//if it's true, count the green or purple.
					}
					//...if not, tough luck!
				}
				
			}
		}
	}
	
	//Helper function - checks if there's a region in the checkMap in the <custom> vincinity of the given pixel.
	private boolean checkSegmentValidity(int[][][] seg){
		return false;
		
	}

}
