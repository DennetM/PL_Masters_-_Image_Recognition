package ocrRecognizer;

import java.io.IOException;

import ocrReadWrite.ReadImage;

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
		String magicNumber = Integer.toString(imgNumber+1);
		String file = ("000" + magicNumber).substring(magicNumber.length());
		//Access the file.
		int[][] imgRaw = ReadImage.read(type, type+file+".bmp");
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
			System.out.println("Label: "+feat);
		}
		if(featNumber==1){
			//Contrast
			feat = glcm.computeContrast();
			System.out.println("Contrast value: "+feat);
		}
		if(featNumber==2){
			//Correlation
			feat = glcm.computeCorrelation();
			System.out.println("Correlation value: "+feat);
		}
		if(featNumber==3){
			//Energy
			feat = glcm.computeEnergy();
			System.out.println("Energy value: "+feat);
		}
		if(featNumber==4){
			//Homogenity
			feat = glcm.computeHomogeneity();
			System.out.println("Homogeneity value: "+feat);
		}
		return feat;
	}
	
	//getFFTFeature - essentially a different flavour of the above, wherein we instead of using GLCM compute the
	//FFT spectrum of the image and gather information from therein.
	public double getFFTFeature(int imgNumber, int FeatNumber) throws IOException{
		//Step 1. Determine what sort of image we're dealing with.
		String type = determineType(imgNumber+1);
		//Step 2. Access the image specified by the image number (and it's type).
		//Prepare the file identifier based on the number.
		String magicNumber = Integer.toString(imgNumber+1);
		String file = ("000" + magicNumber).substring(magicNumber.length());
		//Access the file.
		int[][] imgRaw = ReadImage.read(type, type+file+".bmp");
		
		//Step 3. Perform the Fast Fourier Transform on the image so we can gather it's features.
		FFT finalFantasy = new FFT(imgRaw, 64, 64);
		finalFantasy.FFTStandard();
		
		return 0.0;
	}
}
