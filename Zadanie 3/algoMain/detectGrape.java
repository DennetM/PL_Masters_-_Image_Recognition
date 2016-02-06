package algoMain;

public class detectGrape {
	
	//Variables
	public int[][] checkMap; //A "binary" map that exists to hold greyscale values.
	public int[][][] img; //The image we're currently assessing.
	private int avgGW; // average grape width;
	private int avgGH; // average grape height;
	
	public int numberGreen =0; //number of green grapes.
	public int numberPurpl =0; //number of purple grapes.
	
	//Constructor - take in the image to test and the user-created average grape width and height.
	public detectGrape(int[][][] img, int x, int y, double cutoff){
		this.img = img;
		this.avgGW = x;
		this.avgGH = y;
		
		this.checkMap = new int[800][600]; //Fill it flatly, each grape image is standarized.
		
		countGrapes(cutoff); //Immedaitelly count them, no need to wait.
		
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
	private void countGrapes(double thresh){
		//First loop set. Scan. By height.
		for(int x=0; x<800; x++){
			for(int y=0; y<600; y++){
				
				//If the pixel's colour average is off, meaning it's colourful, and not white or gray.
				int rg = Math.abs(this.img[0][x][y] - this.img[1][x][y]);
				int gb = Math.abs(this.img[1][x][y] - this.img[2][x][y]);
				int br = Math.abs(this.img[2][x][y] - this.img[0][x][y]);
				
				if(rg>30 || gb>30 || br>30){
					//If the segment ain't some shade of green, let's start considering it.
					//First, check if it's valid for consideration.
					if(checkSegmentValidity(x,y, this.avgGW/4, this.avgGH/4, thresh)){
						//If the segment is fresh and clean, mark that we're here.
						markSegment(x,y);
						//And proceed to do some fancy colour math.
						int totalRed = 0;
						int totalGreen = 0;
						int totalBlue = 0;
						int hash = 0;
						for(int locx=0; locx<this.avgGW; locx++){
							for(int locy=0; locy<this.avgGH; locy++){
								if(x+locx<800 && y+locy<600){
									totalRed+=this.img[0][x+locx][y+locy];
									totalGreen+=this.img[1][x+locx][y+locy];
									totalBlue+=this.img[2][x+locx][y+locy];
									hash++;
								}
							}
						}
						totalRed/=hash;
						totalGreen/=hash;
						totalBlue/=hash;
						//System.out.println("totalRed: "+totalRed);
						//System.out.println("totalGreen: "+totalGreen);
						//System.out.println("totalBlue: "+totalBlue);
						//And now, our grapes are un-uniform fuckers. The purple we're dealing with is muddy,
						//meaning it's red value is high but green and blue are about even.
						//The green is also a reddish one, so it's green and red values are high while blue is insignificant.
						int avgRG = (totalRed+totalGreen)/2;
						int avgGB = (totalGreen+totalBlue)/2;
						//System.out.println("AverageRG: "+avgRG);
						//System.out.println("AverageGB :"+avgGB);
						//Check for purple, since it's much easier. If it's not purple, it's green.
						if (totalRed>avgGB && totalRed>totalGreen+25) this.numberPurpl++;
						else this.numberGreen++;
						
						//TODO: Write the colour-comparison function, eventually.
					}
					//...if not, tough luck, we've already been to this area.
				}
				
			}
		}
	}
	
	//Helper function - checks if there's a region in the checkMap in the <custom> vincinity of the given pixel.
	private boolean checkSegmentValidity(int startx, int starty, int distx, int disty, double threshold){
		//System.out.println("Checking segment validity...");
		//Case A) The segment is placed on an existing checkmap.
		for(int x=0; x<this.avgGW; x++){
			for(int y=0; y<this.avgGH; y++){
				if(x+startx<800 && y+starty<600){
					if(this.checkMap[x+startx][y+starty]!=0) return false;
				}
			}
		}
		
		//Case B) Discard the segment if it contains more than 2% white pixels.
		int total = 0;
		int white = 0;
		for(int x=0; x<this.avgGW; x++){
			for(int y=0; y<this.avgGH; y++){
				if(x+startx<800 && y+starty<600){
					total++;
					int r = this.img[0][x+startx][y+starty];
					int g = this.img[1][x+startx][y+starty];
					int b = this.img[2][x+startx][y+starty];
					if(r>200 && g>200 && b>200) white++;
				}
			}
		}
		double ratio = ((double)white/(double)total)*100;
		//System.out.println("ratio: "+ratio);
		if(ratio>threshold) return false;
		
		//Case C) There's a segment nearby, by a factor of a quarter of the average distances in x and y axis.
		//C1) Below.
		for(int x=0; x<this.avgGW; x++){
			for(int y=0; y<disty; y++){
				if(x+startx<800 && y+starty+this.avgGH<600){
					if(this.checkMap[x+startx][y+starty+this.avgGH]!=0) return false;
				}
			}
		}
		//C2) To the right.
		for(int y=0; y<this.avgGH; y++){
			for(int x=0; x<distx; x++){
				if(y+starty<600 & x+startx+this.avgGW<800){
					if(this.checkMap[x+startx+this.avgGW][y+starty]!=0) return false;
				}
			}
		}
		//C3) Above.
		for(int x=0; x<this.avgGW; x++){
			for(int y=0; y<disty; y++){
				if(x+startx<800 && starty-y>=0){
					if(this.checkMap[x+startx][starty-y]!=0) return false;
				}
			}
		}
		//C4) To the left.
		for(int y=0; y<this.avgGH; y++){
			for(int x=0; x<distx; x++){
				if(y+starty < 600 && startx-x>=0){
					if(this.checkMap[startx-x][y+starty]!=0) return false;
				}
			}
		}
		//C5)Edge - top left.
		for(int x=0; x<distx; x++){
			for(int y=0; y<disty; y++){
				if(starty-y>=0 && startx-x>=0){
					if(this.checkMap[startx-x][starty-y]!=0) return false;
				}
			}
		}
		//C6)Edge - top right.
		for(int x=0; x<distx; x++){
			for(int y=0; y<disty; y++){
				if(x+startx+this.avgGW < 800 && starty-y>=0){
					if(this.checkMap[startx+x+this.avgGW][starty-y]!=0) return false;
				}
			}
		}
		//C7) Edge - bottom left
		for(int x=0; x<distx; x++){
			for(int y=0; y<disty; y++){
				if(startx-x>=0 && starty+y < 600){
					if(this.checkMap[startx-x][starty+y]!=0) return false;
				}
			}
		}
		//C8) Edge - bottom right.
		for(int x=0; x<distx; x++){
			for(int y=0; y<disty; y++){
				if(x+startx+this.avgGW<800 && y+starty+this.avgGH<600){
					if(this.checkMap[x+startx+this.avgGW][y+starty+this.avgGH]!=0) return false;
				}
			}
		}
		
		//If we didn't find any issues with it, it's considered good to go.
		return true;
		
	}
	
	//Marks the segment we're in on the checkmap.
	private void markSegment(int startx, int starty){
		for(int x=0; x<this.avgGW; x++){
			for(int y=0; y<this.avgGH; y++)
				if(x+startx<800 && y+starty<600){
					this.checkMap[x+startx][y+starty] = 255;
				}
		}
	}

}
