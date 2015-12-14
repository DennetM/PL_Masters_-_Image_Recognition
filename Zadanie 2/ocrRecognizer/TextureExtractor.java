package ocrRecognizer;

import java.io.IOException;

import ocrReadWrite.ReadImage;

import org.apache.commons.math3.complex.Complex;

//The extractor class, which will gather features of images one by one and prepare them to be sent to Knowledge class.
public class TextureExtractor {
	
	//Variables
	//We only need to remember the limits, and we'll -always- read the data in this order.
	//Linen -> Salt -> Straw -> Wood.
	private int linenLimit;
	private int saltLimit;
	private int strawLimit;
	private int woodLimit;
	
	
	//Constructor
	public TextureExtractor(int li, int sa, int st, int wo){
		this.linenLimit = li;
		this.saltLimit = this.linenLimit+sa;
		this.strawLimit = this.saltLimit+st;
		this.woodLimit = this.strawLimit+wo;
	}
	
	//Functions
	//No gets for limits, since they're inherited from the Knowledge so the same as there.
	
	//determineType - determines the type of the texture based on the given number.
	//Remember that these limits should count themselves from 1 and finish at the last number!
	private String determineType(int num){
		String type = null;
		
		if(num<=this.linenLimit) type="linen";
		if(num>this.linenLimit && num<=this.saltLimit) type="salt";
		if(num>this.saltLimit && num<=this.strawLimit) type="straw";
		if(num>this.strawLimit && num<=this.woodLimit) type="wood";
		
		return type;
	}
	
	//getFlatFeature - the master function that will extract the feature of a certain image.
	//Features increase normally from 0 (label) to the last feature. Labels/Types increase based on the limits.
	public double getFlatFeature(int imgNumber, int featNumber) throws IOException{
		//Step 1. Determine what sort of image we're dealing with.
		String type = determineType(imgNumber+1);
		//Step 2. Access the image specified by the image number (and it's type).
		//Prepare the file identifier based on the number.
		int magicNumber = imgNumber+1;
		if(type.equals("salt")) magicNumber -= this.linenLimit;
		if(type.equals("straw")) magicNumber -= this.saltLimit;
		if(type.equals("wood")) magicNumber -= this.strawLimit;
		String mag = Integer.toString(magicNumber);
		String file = ("000" + magicNumber).substring(mag.length());
		//Access the file.
		int[][] imgRaw = ReadImage.readTrain(type, type+file+".bmp");
		double[][] imgMatrix = new double[64][64];
		
		//Step 3. Prepare the Grey-Level Co-Occurence (GLCM) Matrix, from which we'll extract the data for the greyscale texture.
		for(int i=0; i<64; i++){
			for(int j=0; j<64; j++){
				imgMatrix[i][j] = (double) imgRaw[i][j];
			}
		}
		//Thank you Mr. Julien Amelot!
		GLCM glcm = GLCM.createGLCM(imgMatrix, 1, 1, 10, false, true, 1.0, 10.0);
		
		//Step 4. Determine the feature we want to push onward and prepare to dump it on.
		double feat = 0.0;
		if(featNumber==0){
			//Label
			if(type.equals("linen")) feat = 0.0;
			if(type.equals("salt")) feat = 1.0;
			if(type.equals("straw")) feat = 2.0;
			if(type.equals("wood")) feat = 3.0;
			//System.out.println("Label: "+feat);
		}
		if(featNumber==1){
			//Contrast
			feat = glcm.computeContrast();
			//System.out.println("Contrast value: "+feat);
		}
		if(featNumber==2){
			//Correlation
			feat = glcm.computeCorrelation();
			//System.out.println("Correlation value: "+feat);
		}
		if(featNumber==3){
			//Energy
			feat = glcm.computeEnergy();
			//System.out.println("Energy value: "+feat);
		}
		if(featNumber==4){
			//Homogenity
			feat = glcm.computeHomogeneity();
			//System.out.println("Homogeneity value: "+feat);
		}
		return feat;
	}
	
	//getFFTFeature - essentially a different flavour of the above, wherein we instead of using GLCM compute the
	//FFT spectrum of the image and gather information from therein.
	public double getFFTFeature(int imgNumber, int featNumber) throws IOException{
		//Step 1. Determine what sort of image we're dealing with.
		String type = determineType(imgNumber+1);
		//Step 2. Access the image specified by the image number (and it's type).
		//Prepare the file identifier based on the number.
		int magicNumber = imgNumber+1;
		if(type.equals("salt")) magicNumber -= this.linenLimit;
		if(type.equals("straw")) magicNumber -= this.saltLimit;
		if(type.equals("wood")) magicNumber -= this.strawLimit;
		String mag = Integer.toString(magicNumber);
		String file = ("000" + magicNumber).substring(mag.length());
		//Access the file.
		int[][] imgRaw = ReadImage.readTrain(type, type+file+".bmp");
		
		//Step 3. Perform the Fast Fourier Transform on the image so we can gather it's features.
		FFT finalFantasy = new FFT(imgRaw, 64, 64);
		finalFantasy.FFTStandard();
		
		//Step 4. Apply the filters to determine the feature.
		double feat = 0.0;
		if(featNumber==0){
			//Label
			if(type.equals("linen")) feat = 0.0;
			if(type.equals("salt")) feat = 1.0;
			if(type.equals("straw")) feat = 2.0;
			if(type.equals("wood")) feat = 3.0;
			//System.out.println("Label: "+feat);
		}
		if(featNumber==1){
			//Filter 1
			int[][]fltr = ReadImage.readFilter("Filter1.png");
			feat = filterDensity(fltr, finalFantasy);
		}
		if(featNumber==2){
			//Filter 1
			int[][]fltr = ReadImage.readFilter("Filter2.png");
			feat = filterDensity(fltr, finalFantasy);
		}
		if(featNumber==3){
			//Filter 1
			int[][]fltr = ReadImage.readFilter("Filter3.png");
			feat = filterDensity(fltr, finalFantasy);
		}
		if(featNumber==4){
			//Filter 1
			int[][]fltr = ReadImage.readFilter("Filter4.png");
			feat = filterDensity(fltr, finalFantasy);
		}
		if(featNumber==5){
			//Filter 1
			int[][]fltr = ReadImage.readFilter("Filter5.png");
			feat = filterDensity(fltr, finalFantasy);
		}
		if(featNumber==6){
			//Filter 1
			int[][]fltr = ReadImage.readFilter("Filter6.png");
			feat = filterDensity(fltr, finalFantasy);
		}
		if(featNumber==7){
			//Filter 1
			int[][]fltr = ReadImage.readFilter("Filter7.png");
			feat = filterDensity(fltr, finalFantasy);
		}
		if(featNumber==8){
			//Filter 1
			int[][]fltr = ReadImage.readFilter("Filter8.png");
			feat = filterDensity(fltr, finalFantasy);
		}
		if(featNumber==9){
			//Filter 1
			int[][]fltr = ReadImage.readFilter("Filter9.png");
			feat = filterDensity(fltr, finalFantasy);
		}
		if(featNumber==10){
			//Filter 1
			int[][]fltr = ReadImage.readFilter("Filter10.png");
			feat = filterDensity(fltr, finalFantasy);
		}
		if(featNumber==11){
			//Filter 1
			int[][]fltr = ReadImage.readFilter("Filter11.png");
			feat = filterDensity(fltr, finalFantasy);
		}
		if(featNumber==12){
			//Filter 1
			int[][]fltr = ReadImage.readFilter("Filter12.png");
			feat = filterDensity(fltr, finalFantasy);
		}		
		return feat;
	}
	
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
}
