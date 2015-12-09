package ocrRecognizer;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

//Hello darkness my old friend.
//HIGHLY REPURPOSED FROM OUR OLD FOURIER TRANSFORM PROGRAM.
public class FFT {
	
	//Variables
	private int powerIndex;
	public Complex[][] FFTImage;
	private double[][] transImage;
	FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
	
	//Constructor
	public FFT(int[][] img, int width, int height){
		//Shortened pad() function of the original.
		int padding;
		if(width > height) padding = width;
		else if (height > width) padding = height;
		else padding = width; // Doesn't matter in this scenario, since both are equal.
		
		//Now we introduce the powerIndex, also known as the current power of two.
		int powerIndex = 1;
		//First, by width.
		while(powerIndex < padding && powerIndex < 8388608){
			powerIndex *=2;
		}
		
		transImage = new double[powerIndex][powerIndex];
		FFTImage = new Complex[powerIndex][powerIndex];
		this.powerIndex = powerIndex;
		
		for(int i=0; i<powerIndex; i++){
			for(int j=0; j<powerIndex;j++){
				//If you go over the base values, instead input zeroes.
				if(i>=width || j>=height){
					transImage[i][j] = 0;
				}
				else{
					transImage[i][j] = (double) img[i][j];
				}
			}
		}	
	}
	
	//Functions
	public void FFTStandard(){
		//First pass - by columns.
		//There's as many columns as the WIDTH of the table.
		for(int clmn = 0; clmn < powerIndex; clmn++){
			//First thing - we create a temporary table that's a single column, then fill it out.
			double tempColumn[] = new double[powerIndex];
			for (int i = 0; i<powerIndex; i++){
				tempColumn[i] = transImage[clmn][i];
				
			}
			//Note: we are still in a single pass of the main loop.
			//We now have three tables, each of them is a single column from each of the spectrums we're Fourier-ing.
			//Enter three supplementary Complex tables wherein each of the previous tables is Fourier'd.
			Complex[] FFTTemp = fft.transform(tempColumn, TransformType.FORWARD);
			//Now we reverse the previous. We cram each of these tables into its corresponding final table.
			for (int i = 0; i<FFTTemp.length; i++){
				FFTImage[clmn][i] = FFTTemp[i];
			}
		}
		System.out.println("Finished FFT by columns.");
		
		
		//We have a full rCom table that's been partially transformed. Now we repeat the entire algorithm by rows.
		//Second pass - by rows.
		for(int row = 0; row < powerIndex; row++){
			//Notice - we make three temporary rows as before, but since we've already passed the Fourier, they're COMPLEX now!
			Complex[] tempRow = new Complex[powerIndex];
			for (int i=0; i<powerIndex; i++){
				tempRow[i] = FFTImage[i][row];
			}
			//And since they're complex, we can 'abuse' them for this task.
			tempRow = fft.transform(tempRow, TransformType.FORWARD);
			//Finally, we cram them in once more.
			for(int i =0; i<tempRow.length; i++){
				FFTImage[i][row] = tempRow[i];
			}
		}
		System.out.println("Finished FFT by rows. Finished FFT.");
	}

}
