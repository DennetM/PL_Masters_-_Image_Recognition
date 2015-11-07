package ocrRecognizer;

import java.io.IOException;

import ocrReadWrite.ReadDatabase;


//The Data Extractor will fling itself through the database we loaded previously and extract the feature data from each image.
//Then, it'll arrange the data into the tables, and that will be that.
public class DataExtractor {

	//Variables
	private int[] labels; //A table for our labels.
	private int[][][] images; //A table for our images.
	private int[][] img; //Our image that we load down.
	private int size; //The size of our data, so we don't get lost in too many examples.
	
	//Constructor
	//Loads the data from the file right off the bat.
	public DataExtractor(int s) throws IOException{
		this.size = s;
		this.img = new int[28][28];
		for(int i=0; i<28; i++){
			for(int j=0; j<28; j++){
				this.img[i][j] = 0;
			}
		}
		this.labels = new int[size];
		this.images = new int[size][28][28];
	}
	
	//Functions
	//prepareLabel - fills out the labels from file, raw, up to a certain size
	public void prepareLabel(int[] labels){
		this.labels = labels;
		System.out.println("Raw Labels loaded!");
	}
	//prepareImage - fills out the images from file, raw, up to a certain number.
	public void prepareImage(int[][][] images){
		this.images = images;
		System.out.println("Raw images loaded!");
	}
	
	//extractLabel - extracts a single label from our file, it's rather simple.
	//This function is to be used in a loop.
	public double extractLabel(int index){
		return this.labels[index];
		
	}
	
	//extractFeature - extracts a single feature from the image, depending on the modifier.
	public double extractFeature(int index, int mod){
		//First, load the image into a temporary copy we'll fight with.
		for(int k=0; k<28; k++){
			for(int r=0; r<28; r++){
				this.img[r][k] = this.images[index][r][k];
			}
		}
		//The entire featurelist is solely based on pixel density, which we calculate using extractDensity.
		//The 'mod' is responsible for the sector, starting at sector 1 (top left) and finishing at sector 17 (bottom right).
		//To get 16 sectors, we split the image into equal parts, 4 by 4. Specifically: 0-6, 7-14, 15-21, 22-28.
		//The sectors advance from left to right, top to bottom, in that order.
		if (mod==1) return extractDensity(0, 6, 0, 6, this.img);
		if (mod==2) return extractDensity(7, 14, 0, 6, this.img);
		if (mod==3) return extractDensity(15, 21, 0, 6, this.img);
		if (mod==4) return extractDensity(22, 28, 0, 6, this.img);
		
		if (mod==5) return extractDensity(0, 6, 7, 14, this.img);
		if (mod==6) return extractDensity(7, 14, 7, 14, this.img);
		if (mod==7) return extractDensity(15, 21, 7, 14, this.img);
		if (mod==8) return extractDensity(22, 28, 7, 14, this.img);
		
		if (mod==9) return extractDensity(0, 6, 15, 21, this.img);
		if (mod==10) return extractDensity(7, 14, 15, 21, this.img);
		if (mod==11) return extractDensity(15, 21, 15, 21, this.img);
		if (mod==12) return extractDensity(22, 28, 15, 21, this.img);
		
		if (mod==13) return extractDensity(0, 6, 22, 28, this.img);
		if (mod==14) return extractDensity(7, 14, 22, 28, this.img);
		if (mod==15) return extractDensity(15, 21, 22, 28, this.img);
		if (mod==16) return extractDensity(22, 28, 22, 28, this.img);
		
		//If we got the wrong index, return 0.
		else return 0.0;
	}
	
	//extractDensity - extracts the density data from a certain range
	private double extractDensity(int xs, int xe, int ys, int ye, int[][] image){
		int countTotes = 0;
		int countWhite = 0;
		for(int i=xs; i<xe; i++){
			for(int j = ys; j<ye; j++){
				countTotes += 1;
				if(image[i][j] == 0) countWhite += 1;
			}
		}
		//System.out.println("Number of White: "+countWhite+"\n"+
		//					"Number Total: "+countTotes);
		double density = (double)countWhite / (double)countTotes;
		//System.out.println("Density: "+density);
		return density;
	}
	
	
}
