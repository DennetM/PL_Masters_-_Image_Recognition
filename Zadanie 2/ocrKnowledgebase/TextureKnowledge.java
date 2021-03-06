package ocrKnowledgebase;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.math3.complex.Complex;

import ocrReadWrite.ReadImage;
import ocrRecognizer.FFT;
import ocrRecognizer.GLCM;
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
				for(int j=0; j<this.FFTFeatures; j++){
					this.fftKnowledgebase[i][j] = extract.getFFTFeature(i, j);
				}
			}
		}
	}
	
	//testKnn - test the three images against our database using the KNN method.
	//This function will test both FTT and Flat recognition, so don't turn it on BEFORE filling out both databases.
	public void testFlat(int k, String TYPE) throws IOException{
		
		//Begin. For each image..
		for(int i=0; i<3; i++){
			//Get the images.
			int[][] img = ReadImage.readTest("test"+(i+1)+".bmp");
			int[][] label = ReadImage.readTest("label"+(i+1)+".bmp");
			int[][] result = new int[512][512];
			
			//Start pondering. We're operating on a 64x64 square that starts at (0,0) and moves through the image in 64 increments.
			//This loops serves as the main window. Going from 0,0 to 512,512 every 64 pixels.
			//CORRECTION: We go per pixel. This does give us a huge amount of redundancy but each time we correctly label at least 1 pixel.
			for(int x=0; x<448; x++){
				for(int y=0; y<448; y++){
					//Create a window that will be our "image" used for data extraction.
					double[][] window = new double[64][64]; //And fill it.
					for(int wx=0; wx<64; wx++){
						for(int wy=0; wy<64; wy++){
							window[wx][wy] = (double) img[wx+x][wy+y];
						}
					}
					//Now, create a new double table that will hold our features, sans labels for they're not necessary now.
					double[] windowFeatures = new double[this.flatFeatures];
					//And fill it out, as per TextureExtraction.
					GLCM glcm = GLCM.createGLCM(window, 1, 1, 10, false, true, 1.0, 10.0); //Create an GLCM.
					//Fill the features.
					windowFeatures[1] = glcm.computeContrast();
					windowFeatures[2] = glcm.computeCorrelation();
					windowFeatures[3] = glcm.computeEnergy();
					windowFeatures[4] = glcm.computeHomogeneity();
					
					//Done? Great. Count the KNN.
					double feat = 0;
					if (TYPE.equals("KNN")) feat = determineKNN(windowFeatures, k, "Flat");
					if (TYPE.equals("BAE")) feat = determineBayes2(windowFeatures, "Flat");
					
					//With our feature, set the colour of the brush we'll paint the result.
					int brush = 0;
					if(feat == 0.0) brush=224;
					if(feat == 1.0) brush=160;
					if(feat == 2.0) brush=96;
					if(feat == 3.0) brush=32;
					
					//Paint our window.
					for(int wx=0; wx<64; wx++){
						for(int wy=0; wy<64; wy++){
							result[wx+x][wy+y] = brush;
						}
					}
				}
			}
			//Once we're done with our window function, calculate the success values.
			int[][] overlap = new int[448][448];
			double successRate = 0.0; // <-- our success rate.
			double totalRate = 448*448;
			for(int x=0; x<448; x++){
				for(int y=0; y<448; y++){
					if(result[x][y] == label[x][y]){
						successRate++;
						overlap[x][y]=255;
					}
				}
			}
			//And see how well we did.
			ReadImage.saveTestFunction(result, "result"+(i+1)+".png");
			ReadImage.saveTestFunction(overlap,"overlapFlat"+(i+1)+".png");
			System.out.println("Success Rate (Flat) measures at: "+((successRate/totalRate)*100)+"%");
		}
	}
	public void testFFT(int k, String TYPE) throws IOException{
		//Prepare our universal values.
		int[][]fltr1 = ReadImage.readFilter("Filter1.png");
		int[][]fltr2 = ReadImage.readFilter("Filter2.png");
		int[][]fltr3 = ReadImage.readFilter("Filter3.png");
		int[][]fltr4 = ReadImage.readFilter("Filter4.png");
		int[][]fltr5 = ReadImage.readFilter("Filter5.png");
		int[][]fltr6 = ReadImage.readFilter("Filter6.png");
		int[][]fltr7 = ReadImage.readFilter("Filter7.png");
		int[][]fltr8 = ReadImage.readFilter("Filter8.png");
		int[][]fltr9 = ReadImage.readFilter("Filter9.png");
		int[][]fltr10 = ReadImage.readFilter("Filter10.png");
		int[][]fltr11 = ReadImage.readFilter("Filter11.png");
		int[][]fltr12 = ReadImage.readFilter("Filter12.png");
		
		//Begin. For each image..
		for(int i=0; i<3; i++){
			//Get the images.
			int[][] img = ReadImage.readTest("test"+(i+1)+".bmp");
			int[][] label = ReadImage.readTest("label"+(i+1)+".bmp");
			int[][] result = new int[512][512];
			
			//Start pondering. We're operating on a 64x64 square that starts at (0,0) and moves through the image in 64 increments.
			//This loops serves as the main window. Going from 0,0 to 512,512 every 64 pixels.
			//Same as before.
			for(int x=0; x<448; x++){
				for(int y=0; y<448; y++){
					//Create a window that will be our "image" used for data extraction.
					int[][] window = new int[64][64]; //And fill it.
					for(int wx=0; wx<64; wx++){
						for(int wy=0; wy<64; wy++){
							window[wx][wy] = img[wx+x][wy+y];
						}
					}
					//Now, create a new double table that will hold our features, sans labels for they're not necessary now.
					double[] windowFeatures = new double[this.FFTFeatures];
					//And fill it out, as per TextureExtraction.
					FFT windowFFT = new FFT(window, 64, 64); //Create an FFT.
					windowFFT.FFTStandard(); //Perform the FFT (+flip +normalize)
					//Fill the features.
					windowFeatures[1] = filterDensity(fltr1, windowFFT);
					windowFeatures[2] = filterDensity(fltr2, windowFFT);
					windowFeatures[3] = filterDensity(fltr3, windowFFT);
					windowFeatures[4] = filterDensity(fltr4, windowFFT);
					windowFeatures[5] = filterDensity(fltr5, windowFFT);
					windowFeatures[6] = filterDensity(fltr6, windowFFT);
					windowFeatures[7] = filterDensity(fltr7, windowFFT);
					windowFeatures[8] = filterDensity(fltr8, windowFFT);
					windowFeatures[9] = filterDensity(fltr9, windowFFT);
					windowFeatures[10] = filterDensity(fltr10, windowFFT);
					windowFeatures[11] = filterDensity(fltr11, windowFFT);
					windowFeatures[12] = filterDensity(fltr12, windowFFT);
					
					//Done? Great. Count the KNN.
					double feat = 0;
					if (TYPE.equals("KNN")) feat = determineKNN(windowFeatures, k, "FFT");
					if (TYPE.equals("BAE")) feat = determineBayes2(windowFeatures, "FFT");
					
					//With our feature, set the colour of the brush we'll paint the result.
					int brush = 0;
					if(feat == 0.0) brush=224;
					if(feat == 1.0) brush=160;
					if(feat == 2.0) brush=96;
					if(feat == 3.0) brush=32;
					
					//Paint our window.
					for(int wx=0; wx<64; wx++){
						for(int wy=0; wy<64; wy++){
							result[wx+x][wy+y] = brush;
						}
					}
				}
			}
			//Once we're done with our window function, calculate the success values.
			int[][] overlap = new int[448][448];
			double successRate = 0.0; // <-- our success rate.
			double totalRate = 448*448;
			for(int x=0; x<448; x++){
				for(int y=0; y<448; y++){
					if(result[x][y] == label[x][y]){
						successRate++;
						overlap[x][y] = 255;
					}
				}
			}
			//And see how well we did.
			ReadImage.saveTestFunction(result, "result"+(i+1)+".png");
			ReadImage.saveTestFunction(overlap, "overlapFFT"+(i+1)+".png");
			System.out.println("Success Rate (FFT) measures at: "+((successRate/totalRate)*100)+"%");
		}
	}
	
	//yep, same function as in the Extractor. We sort of really really need it.
	private double filterDensity(int[][] fltr, FFT fft){
		double content = 0.0;
		double densityBase = 0.0;
		for(int x=0;x<64;x++){
			for(int y=0;y<64;y++){
				if(fltr[x][y] != 0){
					content += fft.FreqSpectrum[x][y];
					densityBase += 1;
				}
			}
		}
		//System.out.println("Filtered Density: "+(content/densityBase));
		return (content/densityBase);
	}
	
	private double determineKNN(double[] features, int k, String TYPE){
		//Fly through the entire database and count the distance from our point of features to every other one.
		double[][] distance = new double[this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood][2];
		for(int j=0; j<(this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood); j++){
			double dstVal = 0;
			for(int ft=1; ft<features.length; ft++){
				if(TYPE.equals("Flat")) dstVal+=Math.abs(features[ft] - this.flatKnowledgebase[j][ft]);
				if(TYPE.equals("FFT")) dstVal+=Math.abs(features[ft] - this.fftKnowledgebase[j][ft]);
			}
			distance[j][0] = dstVal;
			if(TYPE.equals("Flat")) distance[j][1] = this.flatKnowledgebase[j][0];
			if(TYPE.equals("FFT")) distance[j][1] = this.fftKnowledgebase[j][0];
		}
		
		//Our distances are calculated. Sort and determine the closest one.
		java.util.Arrays.sort(distance, new java.util.Comparator<double[]>() {
		    public int compare(double[] a, double[] b) {
		        return Double.compare(a[0], b[0]);
		    }
		});
		//Count the k-nearest labels.
		double[] labels = new double[4];
		for(int pick=0; pick<k; pick++){
			labels[(int)distance[pick][1]] += 1;
		}
		//Assign the most common label and return it.
		double memIndex = 0;
		double memValue = 0;
		for(int pickHigh=0; pickHigh<4; pickHigh++){
			if (labels[pickHigh] >= memValue){
				memValue=labels[pickHigh];
				memIndex = pickHigh;
			}
		}
		//System.out.println("Feature determined: "+memIndex);
		return memIndex;
		
	}
	
	private double determineBayes(double[] features, String TYPE){
		//First determine the basic probabilities of belogning to the four groups.
		double probLinen = ((double) this.sizeLinen / (double) (this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood));
		double probSalt = (double) (this.sizeSalt / (double) (this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood));
		double probStraw = ((double) this.sizeStraw / (double) (this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood));
		double probWood = ((double) this.sizeWood / (double) (this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood));
		
		//Now in both cases, we want to copy our databases and floor them so that we get rid of pesky double values.
		//They'll remain doubles becaue Java and division by int is a messy, messy thing but we want to have an easier time matching them.
		double[][] simplifiedFlat = new double[this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood][this.flatFeatures];
		double[][] simplifiedFFT = new double[this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood][this.FFTFeatures];
		if(TYPE.equals("Flat")){
			for(int x=0; x<(this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood); x++){
				for(int y=0; y<this.flatFeatures; y++){
					simplifiedFlat[x][y] = Math.floor(this.flatKnowledgebase[x][y]);
					//Addition for Flat: turn NaNs into 0s, since it'll help our approximation.
					if(Double.isNaN(simplifiedFlat[x][y])) simplifiedFlat[x][y] = 0;
				}
			}
		}
		if(TYPE.equals("FFT")){
			for(int x=0; x<(this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood); x++){
				for(int y=0; y<this.FFTFeatures; y++){
					simplifiedFFT[x][y] = Math.floor(this.fftKnowledgebase[x][y]);
				}
			}
		}
		//Floor our base features as well for optimum matching.
		for(int x=0; x<features.length; x++){
			features[x] = Math.floor(features[x]);
			//Again, turn NaNs into 0s, will help.
			if(Double.isNaN(features[x])) features[x] = 0;
		}
		//Just floor ALL THE THINGS.
		
		//With that done, proceed to count the instances of each feature we have (from features) within the databases.
		double[] featLinen = new double[features.length];
		double[] featSalt = new double[features.length];
		double[] featStraw = new double[features.length];
		double[] featWood = new double[features.length];
		for(int x=1; x<features.length; x++){
			double numberLinen = 0.0;
			double numberSalt = 0.0;
			double numberStraw = 0.0;
			double numberWood = 0.0;
			for(int y=0; y<(this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood); y++){
				if(TYPE.equals("Flat")){
					if(features[x] == simplifiedFlat[y][x]){
						if(y<=this.sizeLinen) numberLinen++;
						if(y>this.sizeLinen && y<=(this.sizeLinen+this.sizeSalt)) numberSalt++;
						if(y>(this.sizeLinen+this.sizeSalt) && y<=(this.sizeLinen+this.sizeSalt+this.sizeStraw)) numberStraw++;
						if(y>(this.sizeLinen+this.sizeSalt+this.sizeStraw) && y<=(this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood)) numberWood++;
					}
				}
				if(TYPE.equals("FFT")){
					if(features[x] == simplifiedFFT[y][x]){
						if(y<=this.sizeLinen) numberLinen++;
						if(y>this.sizeLinen && y<=(this.sizeLinen+this.sizeSalt)) numberSalt++;
						if(y>(this.sizeLinen+this.sizeSalt) && y<=(this.sizeLinen+this.sizeSalt+this.sizeStraw)) numberStraw++;
						if(y>(this.sizeLinen+this.sizeSalt+this.sizeStraw) && y<=(this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood)) numberWood++;
					}
				}
			}
			featLinen[x] = numberLinen / (double) this.sizeLinen;
			featSalt[x] = numberSalt / (double) this.sizeSalt;
			featStraw[x] = numberStraw / (double) this.sizeStraw;
			featWood[x] = numberWood / (double) this.sizeWood;
		}
		//That was a handful but we have our base probabilities. Now let's calculate them into totals.
		//Start with having each probability at "1", and we'll shrink them by way of multiplication.
		double[] finalprob = new double[4];
		Arrays.fill(finalprob, 1.0);
		for(int x=0; x<features.length; x++){
			finalprob[0] *= featLinen[x];
			finalprob[1] *= featSalt[x];
			finalprob[2] *= featStraw[x];
			finalprob[3] *= featWood[x];
		}
		//And multiply by the final probability.
		finalprob[0] *= probLinen;
		finalprob[1] *= probSalt;
		finalprob[2] *= probStraw;
		finalprob[3] *= probWood;
		
		//Finally, find the highest probability and give us it's index.
		double val=0;
		double prob=finalprob[0];
		for(int x=0; x<4; x++){
			if(prob > finalprob[x]){
				prob = finalprob[x];
				val=x;
			}
		}
		
		return (double) val;
	}
	
	private double determineBayes2(double[] features, String TYPE){
		//First determine the basic probabilities of belogning to the four groups.
		double probLinen = ((double) this.sizeLinen / (double) (this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood));
		double probSalt = (double) (this.sizeSalt / (double) (this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood));
		double probStraw = ((double) this.sizeStraw / (double) (this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood));
		double probWood = ((double) this.sizeWood / (double) (this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood));
		
		//Now in both cases, we want to copy our databases and floor them so that we get rid of pesky double values.
		//They'll remain doubles becaue Java and division by int is a messy, messy thing but we want to have an easier time matching them.
		double[][] simplifiedFlat = new double[this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood][this.flatFeatures];
		double[][] simplifiedFFT = new double[this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood][this.FFTFeatures];
		if(TYPE.equals("Flat")){
			for(int x=0; x<(this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood); x++){
				for(int y=0; y<this.flatFeatures; y++){
					simplifiedFlat[x][y] = Math.floor(this.flatKnowledgebase[x][y]);
					//Addition for Flat: turn NaNs into 0s, since it'll help our approximation.
					if(Double.isNaN(simplifiedFlat[x][y])) simplifiedFlat[x][y] = 0;
				}
			}
		}
		if(TYPE.equals("FFT")){
			for(int x=0; x<(this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood); x++){
				for(int y=0; y<this.FFTFeatures; y++){
					simplifiedFFT[x][y] = Math.floor(this.fftKnowledgebase[x][y]);
				}
			}
		}
		//Floor our base features as well for optimum matching.
		for(int x=0; x<features.length; x++){
			features[x] = Math.floor(features[x]);
			//Again, turn NaNs into 0s, will help.
			if(Double.isNaN(features[x])) features[x] = 0;
		}
		//Just floor ALL THE THINGS.
		
		//With that done, proceed to count the instances of each feature we have (from features) within the databases.
		double[] featLinen = new double[features.length];
		double[] featSalt = new double[features.length];
		double[] featStraw = new double[features.length];
		double[] featWood = new double[features.length];
		//Addition: Set the distance to gather the data. One for Flat and one for FFT.
		double flatDist = 0.5;
		double fourDist = 15;
		
		for(int x=1; x<features.length; x++){
			double numberLinen = 1.0;
			double numberSalt = 1.0;
			double numberStraw = 1.0;
			double numberWood = 1.0;
			for(int y=0; y<(this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood); y++){
				if(TYPE.equals("Flat")){
					//While 'ifs' stay, we generalize them so there's a much higher chance of catching values.
					//We've got a set distance for each data type. If the distance of the number we're comparing is equal
					//or lower than the set distance, we consider it a 'hit'.
					double currentDist = Math.abs(simplifiedFlat[y][x] - features[x]);
					//System.out.println(currentDist + " vs " +flatDist); Usually 0 or 1. Very binary.
					if(currentDist <= flatDist){
						//System.out.println("Hit.");
						if(y<=this.sizeLinen) numberLinen++;
						if(y>this.sizeLinen && y<=(this.sizeLinen+this.sizeSalt)) numberSalt++;
						if(y>(this.sizeLinen+this.sizeSalt) && y<=(this.sizeLinen+this.sizeSalt+this.sizeStraw)) numberStraw++;
						if(y>(this.sizeLinen+this.sizeSalt+this.sizeStraw) && y<=(this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood)) numberWood++;
					}
				}
				if(TYPE.equals("FFT")){
					//As above.
					double currentDist = Math.abs(simplifiedFFT[y][x] - features[x]);
					//System.out.println(currentDist + " vs " +fourDist); //Anywhere from 0 to 25.
					if(currentDist <= fourDist){
						//System.out.println("Hit.");
						if(y<=this.sizeLinen) numberLinen++;
						if(y>this.sizeLinen && y<=(this.sizeLinen+this.sizeSalt)) numberSalt++;
						if(y>(this.sizeLinen+this.sizeSalt) && y<=(this.sizeLinen+this.sizeSalt+this.sizeStraw)) numberStraw++;
						if(y>(this.sizeLinen+this.sizeSalt+this.sizeStraw) && y<=(this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood)) numberWood++;
					}
				}
			}
			featLinen[x] = numberLinen / (double) this.sizeLinen;
			featSalt[x] = numberSalt / (double) this.sizeSalt;
			featStraw[x] = numberStraw / (double) this.sizeStraw;
			featWood[x] = numberWood / (double) this.sizeWood;
		}
		//That was a handful but we have our base probabilities. Now let's calculate them into totals.
		//Start with having each probability at "1", and we'll shrink them by way of multiplication.
		double[] finalprob = new double[4];
		Arrays.fill(finalprob, 1.0);
		for(int x=1; x<features.length; x++){
			finalprob[0] *= featLinen[x];
			finalprob[1] *= featSalt[x];
			finalprob[2] *= featStraw[x];
			finalprob[3] *= featWood[x];
		}

		
		//And multiply by the final probability.
		finalprob[0] *= probLinen;
		finalprob[1] *= probSalt;
		finalprob[2] *= probStraw;
		finalprob[3] *= probWood;
		
		//Finally, find the highest probability and give us it's index.
		double val=0;
		double prob=finalprob[0];
		for(int x=0; x<4; x++){
			if(prob > finalprob[x]){
				prob = finalprob[x];
				val=x;
			}
		}
		
		return (double) val;
	}
}
