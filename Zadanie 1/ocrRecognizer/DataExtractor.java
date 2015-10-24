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
		this.labels = ReadDatabase.readLabel("labels.idx1-ubyte", this.size);
		this.images = ReadDatabase.readImage("images.idx3-ubyte", this.size);
	}
	
	//Functions
	//extractLabel - extracts a single label from our file, it's rather simple.
	//This function is to be used in a loop.
	public double extractLabel(int index){
		return this.labels[index];
		
	}
	
	//extractFeature - extracts a single feature from the image, depending on the modifier.
	//int mod - the modifier extracts a numbered feature, as described in Knowledge->int[][] knowledgebase comment.
	//int index - the index of the image we're extracting from, should be same as label index.
	//This function is to be used in a loop.
	public double extractFeature(int index, int mod){
		//First, load the image
		for(int k=0; k<28; k++){
			for(int r=0; r<28; r++){
				this.img[r][k] = this.images[index][r][k];
			}
		}
		
		//Switch for extracting the data depending on the sector.
		//Sector 1, pixel density, i.e how many white spaces in picture?.
		if (mod == 1){
			int countTotes = 0;
			int countWhite = 0;
			for(int k=0;k<28;k++){
				for(int r=0;r<9;r++){
					countTotes += 1;
					if(this.img[r][k] == 0) countWhite += 1;
				}
			}			
			double density = countWhite / countTotes;
			return density;
		}
		//Sector 1, ???
		if (mod == 2){
			return 0.0;
		}
		//Sector 2, pixel density.
		if (mod == 3){
			int countTotes = 0;
			int countWhite = 0;
			for(int k=0;k<28;k++){
				for(int r=9;r<18;r++){
					countTotes += 1;
					if(this.img[r][k] == 0) countWhite += 1;
				}
			}			
			double density = countWhite / countTotes;
			return density;
		}
		//Sector 2, ???
		if (mod == 4){
			return 0.0;
		}
		//Sector 3, pixel density.
		if (mod == 5){
			int countTotes = 0;
			int countWhite = 0;
			for(int k=0;k<28;k++){
				for(int r=18;r<28;r++){
					countTotes += 1;
					if(this.img[r][k] == 0) countWhite += 1;
				}
			}			
			double density = countWhite / countTotes;
			return density;
		}
		//Sector 3, ???
		if (mod == 6){
			return 0.0;
		}
		
		//If no valid option selected, zero value.
		//So be careful with this!
		else return 0.0;
	}
	
	
}
