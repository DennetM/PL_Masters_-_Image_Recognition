package ocrKnowledgebase;

import java.io.IOException;

import ocrRecognizer.TextureExtractor;

//Essentially a copy of Knowledge from numbers, just modified to fit textures instead.
public class TextureKnowledge {

	//Variables
	//The sizes of our training sets, so we know just how precise we are with training our program.
	private int sizeLinen;
	private int sizeSalt;
	private int sizeStraw;
	private int sizeWood;
	//No variable for test set, it's always gonna be 3 labels and 3 images.
	
	private int flatFeatures;
	private int FFTFeatures;
	
	//Our two knowledgebases. They follow a similar formula to the first program, namely...
	//double[x][y], where x is the normal index of the entry and [y] are it's features, with y=0 being it's identifier.
	//0 - Linen, 1 - Salt, 2 - Straw, 3 - Wood.
	//We need the same database to check for FFT features.
	public double[][] flatKnowledgebase;
	public double[][] fftKnowledgebase;
	
	
	
	//Constructor
	public TextureKnowledge(int a, int b, int c, int d, int ff, int ft){
		this.sizeLinen = a;
		this.sizeSalt = b;
		this.sizeStraw = c;
		this.sizeWood = d;
		
		this.flatFeatures = ff;
		this.FFTFeatures = ft;
		
		//We're holding all the texture data together, so we need to scale the first size to all four types.
		//Second size is decided by us, since we need to decide on how many features we want to test for.
		this.flatKnowledgebase = new double[a+b+c+d][ff];
		this.fftKnowledgebase = new double[a+b+c+d][ft];
		//Fun fact: No need to prepare the arrays. Java initializes all primitive data types with 0s!
	}
	
	//Functions.
	public int getLinenNum(){ return this.sizeLinen; }
	public int getStrawNum(){ return this.sizeStraw; }
	public int getSaltNum(){ return this.sizeSalt; }
	public int getWoodNum(){ return this.sizeWood; }
	public int getFeatureNumFlat(){ return this.flatFeatures; }
	public int getFeatureNumFFT(){ return this.FFTFeatures; }
	
	//gatherKnowledge - runs through the tables and fills them up with juicy juicy knowledge. Distinguished by mode.
	public void gatherKnowledge(String mode, TextureExtractor extract) throws IOException{
		int totalSize = this.sizeLinen + this.sizeSalt + this.sizeStraw + this.sizeWood;
		
		if(mode.equals("Flat")){
			for(int i=0; i<totalSize; i++){
				for(int j=0; j<this.flatFeatures; j++){
					this.flatKnowledgebase[i][j] = extract.getFlatFeature(i, j);
				}
			}
		}
		
		if(mode.equals("FFT")){
			for(int i=0; i<totalSize; i++){
				for(int j=0; j<this.FFTFeatures; i++){
					this.fftKnowledgebase[i][j] = extract.getFFTFeature(i, j);
				}
			}
		}
		
		else System.out.println("Wrong mode!");
	}
	
	//testKnn - test the three images against our database using the KNN method.
	public void testKnn(int k){
		//TODO: this.
	}
	
	//testAlt - test the three images against our database using the generalized method.
	public void testAlt(){
		//TODO: this.
	}
}
