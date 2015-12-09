package ocrRecognizer;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

//Hello darkness my old friend.
//HIGHLY REPURPOSED FROM OUR OLD FOURIER TRANSFORM PROGRAM.
public class FFT {
	
	//Variables - none.
	
	//Constructor - none.
	
	//Function - welcome to PublicStaticLand!	
	public static double[][] FFT(String Direction, int[][] image, int width, int height){
		
		FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
		
		//Padding function - this function checks the width and height of the image we've grabbed and pads it to the nearest power of 2.
		//This is necessary to perform an FFT later on, so we'll have to bear with it.
		//We'll pad the array to a rectangle for clarity, so let's first check which is greater - width or height.
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
		//Resize the array.
		double[][] dbImage = new double[powerIndex][powerIndex];
		for(int i=0; i<powerIndex; i++){
			for(int j=0; j<powerIndex;j++){
				//If you go over the base values, instead input zeroes.
				if(i>=width || j>=height){
					dbImage[i][j] = 0;
				}
				else{
					dbImage[i][j] = (double)image[i][j];
				}
			}
		}
		//Our table is now padded and should work for the FFT transform.
		
		
		//WE BEGIN WORKING ON THE FFT TRANSFORMATION.
		Complex[][] FFTImage = new Complex[powerIndex][powerIndex];
			
		//2D Fourier Transform is simply a 1D fouriter transform performed twice. Once by width, once by height.
		//This is going to be a very lengthy algorithm, so even with a simple FFT (the fastest FT out there), it may take a while.
		
		//First pass - by columns.
		//There's as many columns as the WIDTH of the table.
		for(int clmn = 0; clmn < powerIndex; clmn++){
			//First thing - we create a temporary table that's a single column, then fill it out.
			double temp[] = new double[powerIndex];
			for (int i = 0; i<powerIndex; i++){
				//System.out.println("Clmn: "+clmn+" i: "+i);
				temp[i] = dbImage[clmn][i];
			}
			//Note: we are still in a single pass of the main loop.
			//We now have three tables, each of them is a single column from each of the spectrums we're Fourier-ing.
			//Enter three supplementary Complex tables wherein each of the previous tables is Fourier'd.
			Complex[] rTemp = fft.transform(temp, TransformType.FORWARD);
			//Now we reverse the previous. We cram each of these tables into its corresponding final table.
			for (int i = 0; i<temp.length; i++){
				dbImage[clmn][i] = temp[i];
			}
		}
		System.out.println("Finished FFT by columns.");
		//We have a full rCom table that's been partially transformed. Now we repeat the entire algorithm by rows.
		//Second pass - by rows.
		for(int row = 0; row < powerIndex; row++){
			//Notice - we make three temporary rows as before, but since we've already passed the Fourier, they're COMPLEX now!
			Complex[] temp = new Complex[powerIndex];
			for (int i=0; i<powerIndex; i++){
				temp[i] = dbImage[i][row];
			}
			//And since they're complex, we can 'abuse' them for this task.
			tempSingleR = fft.transform(tempSingleR, TransformType.FORWARD);
			//Finally, we cram them in once more.
			for(int i =0; i<tempSingleR.length; i++){
				rCom[i][row] = tempSingleR[i];
				gCom[i][row] = tempSingleG[i];
				bCom[i][row] = tempSingleB[i];
			}
		}
		System.out.println("Finished FFT by rows. Finished FFT.");
		
		return null;
	}

}
