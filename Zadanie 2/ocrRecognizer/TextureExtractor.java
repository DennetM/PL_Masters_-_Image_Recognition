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
		
		if(num<this.linenLimit) type="linen";
		if(num>this.linenLimit && num<=this.saltLimit) type="salt";
		if(num>this.saltLimit && num<=this.strawLimit) type="straw";
		if(num>this.strawLimit && num<=this.woodLimit) type="wood";
		
		return type;
	}
	
	//getFlatFeature - the master function that will extract the feature of a certain image.
	//Features increase normally from 0 (label) to the last feature. Labels/Types increase based on the limits.
	public double getFlatFeature(int imgNumber, int featNumber) throws IOException{
		//Step 1. Determine what sort of image we're dealing with.
		String type = determineType(imgNumber);
		//Step 2. Access the image specified by the image number (and it's type).
		//Prepare the file identifier based on the number.
		String magicNumber = Integer.toString(imgNumber);
		String file = ("000" + magicNumber).substring(magicNumber.length());
		//Access the file.
		int[][] img = ReadImage.read(type, type+file+".bmp");
		
		//Step 3. Determine the feature we want to push onward and prepare to dump it on.
		double feat = 0.0;
		if(featNumber==0){
			if(type.equals("linen")) feat = 0.0;
			if(type.equals("salt")) feat = 1.0;
			if(type.equals("straw")) feat = 2.0;
			if(type.equals("wood")) feat = 3.0;
		}
		
		

		return feat;
	}
}
