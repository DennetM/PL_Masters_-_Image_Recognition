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
	public void testKnn(int k) throws IOException{
		double totalSuccessFlat = 0; // <-- we're starting our count from here.
		double totalSuccessFFT = 0;
		//We're going to fly through the three files one by one.
		for(int i=0;i<3;i++){
			double FlatSuccess = 0; // <-- ...and this is the count for the single picture.
			double FFTSuccess = 0;
			//Step 1. Obtain the labels.
			System.out.println("Testing file "+(i+1)+".");
			int[][] label = ReadImage.readTest("label"+(i+1)+".bmp");
			//Step 2. Get the picture.
			int[][] testImg = ReadImage.readTest("test"+(i+1)+".bmp");
			
			//Step 3. Set up the contemporary FlatResult and FFTResult pictures, as well as the FFT Filters.
			int[][] flatResult = new int[512][512];
			int[][] FFTResult = new int[512][512];
			int[][]fltr1 = ReadImage.readFilter("Filter1Small.png");
			int[][]fltr2 = ReadImage.readFilter("Filter2Small.png");
			int[][]fltr3 = ReadImage.readFilter("Filter3Small.png");
			int[][]fltr4 = ReadImage.readFilter("Filter4Small.png");
			
			
			//Step 4. Fly through the picture using a 4x4 window.
			//This window will screen the texture and gather the features we'll compare to the database.
			//It's small, and to the power of two, because we want to be as precise as possible. It's possible to increase
			//it though, so we have a degree of control over it.
			for(int x=0; x<512; x+=4){
				for(int y=0; y<512; y+=4){
					//Step 5. Get the window and fill it depending on where we are.
					int[][] window = new int[4][4]; // For FFT.
					double[][] doubWindow = new double[4][4]; //For GLCM.
					for(int scanx=0; scanx<4; scanx++){
						for(int scany=0; scany<4; scany++){
							window[scanx][scany] = testImg[x+scanx][y+scany];
						}
					}
					
					//Step 6. Compare Flat.
					double[] FlatFeatures = new double[this.flatFeatures];
					GLCM glcm = GLCM.createGLCM(doubWindow, 1, 1, 10, false, true, 1.0, 10.0);
					//Fill the features, excluding the label since it's not necessary.
					FlatFeatures[1] = glcm.computeContrast();
					FlatFeatures[2] = glcm.computeCorrelation();
					FlatFeatures[3] = glcm.computeEnergy();
					FlatFeatures[4] = glcm.computeHomogeneity();
					
					//Got it? Great. Compute the texture based on KNN!
					double[][] distance = new double[this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood][2];
					for(int s=0; s<this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood; s++){
						double dstVal = 0;
						for(int ft=1; ft<5; ft++){
							dstVal+=Math.abs(FlatFeatures[ft] - this.flatKnowledgebase[s][ft]);
						}
						distance[s][0] = dstVal;
						distance[s][1] = this.flatKnowledgebase[s][0];
					}
					java.util.Arrays.sort(distance, new java.util.Comparator<double[]>() {
					    public int compare(double[] a, double[] b) {
					        return Double.compare(a[0], b[0]);
					    }
					});
					//The labels are modified. We only have 4 of them. Linen, Salt, Straw and Wood.
					double[] labels = new double[4];
					Arrays.fill(labels, 0.0);
					for(int pick=0; pick<k; pick++){
						labels[(int)distance[pick][1]] += 1;
					}
					
					int memIndex = 0;
					double memValue = 0;
					//Same here - just 4 labels.
					for(int pickHigh=0; pickHigh<4; pickHigh++){
						if (labels[pickHigh] >= memValue){
							memValue=labels[pickHigh];
							memIndex = pickHigh;
						}
					}
					//Done? Assign the pixel value to our flat result table;
					int brush = 0;
					if(memIndex == 0.0) brush=241;
					if(memIndex == 1.0) brush=208;
					if(memIndex == 2.0) brush=165;
					if(memIndex == 3.0) brush=99;
					for(int scanx=0; scanx<4; scanx++){
						for(int scany=0; scany<4; scany++){
							flatResult[x+scanx][y+scany] = brush;
						}
					}
					
					//Step 7. Compare FFT.
					double[] FFTFeatures = new double[this.flatFeatures];
					//Fill the features, excluding the label since it's not necessary.
					FFT FFTTest1 = new FFT(window, 4, 4);
					double content1 = 0.0;
					for(int scanx=0; scanx<4; scanx++){
						for(int scany=0; scany<4; scany++){
							if(fltr1[scanx][scany] == 0) FFTTest1.FFTImage[x][y] = Complex.ZERO;
							content1 += FFTTest1.FFTImage[scanx][scany].abs();
						}
					}
					FFTFeatures[1] = content1 / (4*4);
					
					FFT FFTTest2 = new FFT(window, 4, 4);
					double content2 = 0.0;
					for(int scanx=0; scanx<4; scanx++){
						for(int scany=0; scany<4; scany++){
							if(fltr2[scanx][scany] == 0) FFTTest2.FFTImage[scanx][scany] = Complex.ZERO;
							content2+=FFTTest2.FFTImage[scanx][scany].abs();
						}
					}
					FFTFeatures[2] = content2 / (4*4);
					
					FFT FFTTest3 = new FFT(window, 4, 4);
					double content3 = 0.0;
					for(int scanx=0; scanx<4; scanx++){
						for(int scany=0; scany<4; scany++){
							if(fltr3[scanx][scany] == 0) FFTTest3.FFTImage[scanx][scany] = Complex.ZERO;
							content3+=FFTTest3.FFTImage[scanx][scany].abs();
						}
					}
					FFTFeatures[3] = content3 / (4*4);
					
					FFT FFTTest4 = new FFT(window, 4, 4);
					double content4 = 0.0;
					for(int scanx=0; scanx<4; scanx++){
						for(int scany=0; scany<4; scany++){
							if(fltr4[scanx][scany] == 0) FFTTest4.FFTImage[scanx][scany] = Complex.ZERO;
							content4+=FFTTest4.FFTImage[scanx][scany].abs();
						}
					}
					FFTFeatures[4] = content4 / (4*4);
					
					//Got it? Great. Compute the texture based on KNN!
					double[][] distanceFFT = new double[this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood][2];
					for(int s=0; s<this.sizeLinen+this.sizeSalt+this.sizeStraw+this.sizeWood; s++){
						double dstVal = 0;
						for(int ft=1; ft<5; ft++){
							dstVal+=Math.abs(FlatFeatures[ft] - this.flatKnowledgebase[s][ft]);
						}
						distanceFFT[s][0] = dstVal;
						distanceFFT[s][1] = this.flatKnowledgebase[s][0];
					}
					java.util.Arrays.sort(distanceFFT, new java.util.Comparator<double[]>() {
					    public int compare(double[] a, double[] b) {
					        return Double.compare(a[0], b[0]);
					    }
					});
					//The labels are modified. We only have 4 of them. Linen, Salt, Straw and Wood.
					double[] labelsFFT = new double[4];
					Arrays.fill(labels, 0.0);
					for(int pick=0; pick<k; pick++){
						labels[(int)distance[pick][1]] += 1;
					}
					int memIndexFFT = 0;
					double memValueFFT = 0;
					//Same here - just 4 labels.
					for(int pickHigh=0; pickHigh<4; pickHigh++){
						if (labels[pickHigh] >= memValueFFT){
							memValueFFT=labels[pickHigh];
							memIndexFFT = pickHigh;
						}
					}
					//Done? Assign the pixel value to our flat result table;
					brush = 0;
					if(memIndexFFT == 0.0) brush=241;
					if(memIndexFFT == 1.0) brush=208;
					if(memIndexFFT == 2.0) brush=165;
					if(memIndexFFT == 3.0) brush=99;
					for(int scanx=0; scanx<4; scanx++){
						for(int scany=0; scany<4; scany++){
							FFTResult[scanx+x][scany+y] = brush;
						}
					}
				}
			}
			//Step 8. We're done with an individual picture, so let's see how well we did.
			ReadImage.saveTestFunction(label, ("label"+i+".png"));
			ReadImage.saveTestFunction(flatResult, ("result"+i+".png"));
			for(int compx=0; compx<512;compx++){
				for(int compy=0;compy<512;compy++){
					if(flatResult[compx][compy] == label[compx][compy]){
						FlatSuccess++;
						totalSuccessFlat++;
					}
					if(FFTResult[compx][compy] == label[compx][compy]){
						FFTSuccess++;
						totalSuccessFFT++;
					}
				}
			}
			//Calculate the percentage for this picture.
			System.out.println("Flat success rate for Test#"+i+" :"+((FlatSuccess/(512*512))*100)+"%");
			System.out.println("FFT success rate for Test#"+i+" :"+FFTSuccess);
		}
	}
	
	//testAlt - test the three images against our database using the generalized method.
	public void testAlt(){
		//TODO: this.
	}
}
