package algoMain;

//This class is essentially a copy of DetectGrape with changed variables and a different colour-sorting function.
//Please kindly ignore all comments that perpetuate to how we distinguish grapes. They're not real. There is no spoon.
public class detectNut {
	
	//Variables
		public int[][] checkMap; //A "binary" map that exists to hold greyscale values.
		public int[][][] img; //The image we're currently assessing.
		private int avgGW; // average grape width;
		private int avgGH; // average grape height;
		
		public int numberDark =0; //number of racist nuts.
		public int numberLight =0; //number of reverse-racist nuts.
		public int numberCurve =0; //number of curved nuts.
		
		private double darkDistance =122.995; //The distance of a dark colour from our RGB zeropoint (0,0,0).
		//private double lightDistance =225.869; //The distance of a light colour from our RGB zeropoint (0,0,0).
		
		//Constructor - take in the image to test and the user-created average grape width and height.
		public detectNut(int[][][] img, int x, int y, double cutoff, int distance){
			this.img = img;
			this.avgGW = x;
			this.avgGH = y;
			
			this.checkMap = new int[800][562]; //Fill it flatly, each grape image is standarized.
			
			countNuts(cutoff, distance); //Immedaitelly count them, no need to wait.
			
			System.out.println("Nuts counted for image. The results are in.");
			System.out.println("Number of dark nuts: "+this.numberDark);
			System.out.println("Number of light nuts: "+this.numberLight);
			System.out.println("Number of curved light nuts: "+this.numberCurve);
		}
		
		
		//Functions:
		//countGrapes - this is a funny one. Basically, we'll scan the document from top to bottom, left to right.
		//When we encounter a pixel that's not pure white, we'll start marking it in the checkMap.
		//Then, we'll count the colours on that square and determine what sort of grape it is.
		//To prevent double-checking, we have to check two things. A) In the new checkbox, did we reach a previous one?
		//														   B) Is the new checkbox within X distance of another one?
		//Of course, the latter is set by us and has to be somewhat adjusted based on the minimal grape size, but that's fine-tuning.
		private void countNuts(double thresh, int dist){
			//First loop set. Scan. By height.
			for(int x=0; x<800; x++){
				for(int y=0; y<562; y++){
					
					//If the pixel's colour average is off, meaning it's colourful, and not white or gray.
					int r = this.img[0][x][y];
					int g = this.img[1][x][y];
					int b = this.img[2][x][y];
					int arg = (r+g)/2;
					int agb = (g+b)/2;
					
					if((arg>b+30) || (r>agb+40)){
						//If the segment ain't some shade of green, let's start considering it.
						//First, check if it's valid for consideration.
						if(checkSegmentValidity(x,y, dist, dist, thresh)){
							//If the segment is fresh and clean, mark that we're here.
							markSegment(x,y);
							//And proceed to do some fancy colour math.
							int totalRed = 0;
							int totalGreen = 0;
							int totalBlue = 0;
							int hash = 0;
							for(int locx=0; locx<this.avgGW; locx++){
								for(int locy=0; locy<this.avgGH; locy++){
									if(x+locx<800 && y+locy<562){
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
							double currentDist = Math.sqrt((totalRed*totalRed)+(totalGreen*totalGreen)+(totalBlue*totalBlue));
							//And now the colour detection strikes again.
							if(currentDist>this.darkDistance-40 && currentDist<this.darkDistance+40) this.numberDark++;
							else countOblong(x, y);
							
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
					if(x+startx<800 && y+starty<562){
						if(this.checkMap[x+startx][y+starty]!=0) return false;
					}
				}
			}
			
			//Case B) Discard the segment if it contains more than 2% white pixels.
			int total = 0;
			int white = 0;
			for(int x=0; x<this.avgGW; x++){
				for(int y=0; y<this.avgGH; y++){
					if(x+startx<800 && y+starty<562){
						total++;
						int r = this.img[0][x+startx][y+starty];
						int g = this.img[1][x+startx][y+starty];
						int b = this.img[2][x+startx][y+starty];
						if(r>190 && g>190 && b>190) white++;
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
					if(x+startx<800 && y+starty+this.avgGH<562){
						if(this.checkMap[x+startx][y+starty+this.avgGH]!=0) return false;
					}
				}
			}
			//C2) To the right.
			for(int y=0; y<this.avgGH; y++){
				for(int x=0; x<distx; x++){
					if(y+starty<562 & x+startx+this.avgGW<800){
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
					if(y+starty < 562 && startx-x>=0){
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
					if(startx-x>=0 && starty+y < 562){
						if(this.checkMap[startx-x][starty+y]!=0) return false;
					}
				}
			}
			//C8) Edge - bottom right.
			for(int x=0; x<distx; x++){
				for(int y=0; y<disty; y++){
					if(x+startx+this.avgGW<800 && y+starty+this.avgGH<562){
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
					if(x+startx<800 && y+starty<562){
						this.checkMap[x+startx][y+starty] = 255;
					}
			}
		}
		
		//Count oblong - this function attempts to draw a circle starting at the middle-right side of the detected sector.
		//If the circle fits the nut thoroughly (as in, the whitness level is low), then we consider it a light nut.
		//If the lightness level is higher than, say, ~30%, it means the circle didn't entirely fit and we got an oblong nut.
		private void countOblong(int startx, int starty){
			int radius = (this.avgGH + this.avgGW)/2;
			int total = 0;
			int white = 0;
			int centerx = startx+this.avgGW;
			int centery = starty+(this.avgGH/2);
			
			for(int y=-radius; y<radius; y++){
				for(int x=-radius; x<radius; x++){
					if(x*x+y*y <= radius*radius){
						if(centerx+x>=0 && centery+y>=0 && centerx+x<800 && centery+y<562){
							total++;
							int r = this.img[0][x+centerx][y+centery];
							int g = this.img[1][x+centerx][y+centery];
							int b = this.img[2][x+centerx][y+centery];
							if(r>190 && g>190 && b>190) white++;
						}
					}
				}
			}
			double ratio = ((double)white/(double)total)*100;
			//System.out.println("ratio: "+ratio);
			if(ratio<40.2) this.numberLight++;
			else this.numberCurve++;
		}

}
