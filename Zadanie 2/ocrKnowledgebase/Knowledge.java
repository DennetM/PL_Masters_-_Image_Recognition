package ocrKnowledgebase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import ocrRecognizer.DataExtractor;

public class Knowledge {

	//Variables
	private int size;
	private int sizeMatch;
	
	//Our knowledgebase, in essence the entire training set inserted in and categorized based on feature.
	//It's a long-ass 2D matrix, with the 0th layer being just the number while the 1th layer being a bit more descriptive...
	// 0 - our number identifier, i.e. 0, 1, 2, 3, ..., 9
	// 1 - first feature of the top box
	// 2 - second feature of the top box
	// 3 - first, second box
	// ...AND SO ON AND SO FORTH. Essentially, the 2nd array has to be the size of [Features]+1.
	public double[][] knowledgeBase;
	
	//The matchbase, essentially a much smaller copy of the database that's initialized during program start.
	//We'll load in our variables here with the index values being set to something entirely space.
	//The pattern of the fields is the same, though the ID field is not strictly locked to 60k examples.
	public double[][] matchBase;
	
	
	//Constructor
	//Constructs the Knowledgebase while setting the size of the array of pictures we want to match.
	public Knowledge(int s, int sm) throws IOException{
		this.size = s;
		this.sizeMatch = sm;
		this.knowledgeBase = new double[size][50];
		this.matchBase = new double[sizeMatch][50];
		
		//Prepare the arrays, setting _EVERY_ value to 0 just so that we're sure we're not dancing with random values.
		for(int i=0;i<size;i++){
			for(int ft=0; ft<50; ft++){
				this.knowledgeBase[i][ft] = 0;
			}
		}
		//Essentially the same algorithm, instead working with a different ID iterator so we won't go out of bounds.
		for(int i=0; i<sizeMatch; i++){
			for(int ft=0; ft<50; ft++){
				this.matchBase[i][ft] = 0;
			}
		}
	}
	
	//Functions
	//gatherKnowledge - this function will iterate itself through the table, calling our DataExtractor class to fill in the blanks.
	public void gatherKnowledge(DataExtractor extractor, String mode){
		//Mode - are we reading into the proper database, or are we reading into our matchbase?
		if (mode.equals("database")){
			//Run through everything.
			for(int i=0; i<size; i++){
				for(int ft=0; ft<50; ft++){
					//If our feature is equal zero, i.e. it's the index, gather it.
					if (ft==0) this.knowledgeBase[i][ft] = extractor.extractLabel(i);
					//If our feature has an index (1,2,3,4,5,6), extract the feature of that index.
					else this.knowledgeBase[i][ft] = extractor.extractFeature(i, ft);
					
					//System.out.println("For label ["+this.knowledgeBase[i][0]+"], index#"+i+", extracted feature index#"+ft+" was: "+this.knowledgeBase[i][ft]);
				}
			}
		}
		if (mode.equals("matchbase")){
			//We sorta just copied the above but changed a few things to make it work.
			for(int i=0; i<sizeMatch; i++){
				for(int ft=0; ft<50; ft++){
					//If our feature is equal zero, i.e. it's the index, gather it.
					if (ft==0) this.matchBase[i][ft] = extractor.extractLabel(i);
					//If our feature has an index (1,2,3,4,5,6), extract the feature of that index.
					else this.matchBase[i][ft] = extractor.extractFeature(i, ft);
					
					//System.out.println("For label ["+this.matchBase[i][0]+"], index#"+i+", extracted feature index#"+ft+" was: "+this.matchBase[i][ft]);
				}
			}
		}		
	}
	
	//testNumbers - using the k-nearest neighbours function, we'll try to give every set of features from the matchbase it's own
	//custom ID based on what we know in the knowledgebase. Afterwards, we'll match the given ID with the read ID and verify if it's correct.
	//NOTICE: INVOKE THIS FUNCTION -ONLY- AFTER BOTH DATABASES HAVE BEEN FILLED, OTHERWISE  YOU WILL ONLY GET GIBBERISH!!
	public void testNumbers(int k){
		int successValue = 0; //<-- we'll increment this each time we get a correct result.
		
		//Our match-table, made so we can view the exact amount of values we assigned vs how they were supposed
		//to be assigned.	
		double[][] resultTable = new double[11][11];
		resultTable[0][0] = 0;
		for(int i=1; i<11; i++){
			//Assign the indexes, becasue it's going to be a very simple matrix.
			resultTable[i][0] = i-1;
			resultTable[0][i] = i-1;
		}
		
		//Fly through the entire matchBase, going through each one.
		for (int i=0; i<this.sizeMatch; i++){
			double[][] distance = new double[this.size][2];
			//For each one, calculate the relative distance between itself and EVERY member of the knowledgebase.
			//We're using the MANHATTAN METRIC to calculate the distance (aka. pl. "Metryka Uliczna").
			for(int s=0; s<this.size; s++){
				double dstVal = 0;
				for(int ft=1; ft<50; ft++){
					dstVal+=Math.abs(this.matchBase[i][ft] - this.knowledgeBase[s][ft]);
				}
				distance[s][0] = dstVal;
				distance[s][1] = this.knowledgeBase[s][0];
				//System.out.println("Raw Order: "+distance[s][0]+" | "+distance[s][1]);
			}
			
			//When we have our distances, we sort them by size, from the closest to the farthest.
			java.util.Arrays.sort(distance, new java.util.Comparator<double[]>() {
			    public int compare(double[] a, double[] b) {
			        return Double.compare(a[0], b[0]);
			    }
			});
			
			//And we pick the k closest ones.			
			//Be democratic. Count the number of each labels appearing.
			double[] labels = new double[10];
			Arrays.fill(labels, 0.0);
			for(int pick=0; pick<k; pick++){
				labels[(int)distance[pick][1]] += 1;
			}
			//And assign the most common label.
			int memIndex = 0;
			double memValue = 0;
			for(int pickHigh=0; pickHigh<10; pickHigh++){
				if (labels[pickHigh] >= memValue){
					memValue=labels[pickHigh];
					memIndex = pickHigh;
				}
			}
			
			//Assign data to our match database.
			resultTable[(int)this.matchBase[i][0]+1][memIndex+1] += 1;

			//Now check if the label is the same as the label we know is true.
			if ((double)memIndex == this.matchBase[i][0]) successValue++;
		}
		
		
		//All done? Let's check our results:
		double success = ((double)successValue / (double)this.sizeMatch) * 100;
		System.out.println("We're done. Our effective guess success rate measures at: "+success+"%!");
		
		//Bonus task: match table, in other words let's see if we managed to get a good score
		System.out.println("Precise data distribution:");
		System.out.println(resultTable[0][0] + "   " + resultTable[1][0] + "   " + resultTable[2][0] + "   " + resultTable[3][0] + "   " + resultTable[4][0] + "   " + resultTable[5][0] + "   " + resultTable[6][0] + "   " + resultTable[7][0] + "   " + resultTable[8][0] + "   " + resultTable[9][0] + "   " + resultTable[10][0]);
		System.out.println(resultTable[0][1] + "   " + resultTable[1][1] + "   " + resultTable[2][1] + "   " + resultTable[3][1] + "   " + resultTable[4][1] + "   " + resultTable[5][1] + "   " + resultTable[6][1] + "   " + resultTable[7][1] + "   " + resultTable[8][1] + "   " + resultTable[9][1] + "   " + resultTable[10][1]);
		System.out.println(resultTable[0][2] + "   " + resultTable[1][2] + "   " + resultTable[2][2] + "   " + resultTable[3][2] + "   " + resultTable[4][2] + "   " + resultTable[5][2] + "   " + resultTable[6][2] + "   " + resultTable[7][2] + "   " + resultTable[8][2] + "   " + resultTable[9][2] + "   " + resultTable[10][2]);
		System.out.println(resultTable[0][3] + "   " + resultTable[1][3] + "   " + resultTable[2][3] + "   " + resultTable[3][3] + "   " + resultTable[4][3] + "   " + resultTable[5][3] + "   " + resultTable[6][3] + "   " + resultTable[7][3] + "   " + resultTable[8][3] + "   " + resultTable[9][3] + "   " + resultTable[10][3]);
		System.out.println(resultTable[0][4] + "   " + resultTable[1][4] + "   " + resultTable[2][4] + "   " + resultTable[3][4] + "   " + resultTable[4][4] + "   " + resultTable[5][4] + "   " + resultTable[6][4] + "   " + resultTable[7][4] + "   " + resultTable[8][4] + "   " + resultTable[9][4] + "   " + resultTable[10][4]);
		System.out.println(resultTable[0][5] + "   " + resultTable[1][5] + "   " + resultTable[2][5] + "   " + resultTable[3][5] + "   " + resultTable[4][5] + "   " + resultTable[5][5] + "   " + resultTable[6][5] + "   " + resultTable[7][5] + "   " + resultTable[8][5] + "   " + resultTable[9][5] + "   " + resultTable[10][5]);
		System.out.println(resultTable[0][6] + "   " + resultTable[1][6] + "   " + resultTable[2][6] + "   " + resultTable[3][6] + "   " + resultTable[4][6] + "   " + resultTable[5][6] + "   " + resultTable[6][6] + "   " + resultTable[7][6] + "   " + resultTable[8][6] + "   " + resultTable[9][6] + "   " + resultTable[10][6]);
		System.out.println(resultTable[0][7] + "   " + resultTable[1][7] + "   " + resultTable[2][7] + "   " + resultTable[3][7] + "   " + resultTable[4][7] + "   " + resultTable[5][7] + "   " + resultTable[6][7] + "   " + resultTable[7][7] + "   " + resultTable[8][7] + "   " + resultTable[9][7] + "   " + resultTable[10][7]);
		System.out.println(resultTable[0][8] + "   " + resultTable[1][8] + "   " + resultTable[2][8] + "   " + resultTable[3][8] + "   " + resultTable[4][8] + "   " + resultTable[5][8] + "   " + resultTable[6][8] + "   " + resultTable[7][8] + "   " + resultTable[8][8] + "   " + resultTable[9][8] + "   " + resultTable[10][8]);
		System.out.println(resultTable[0][9] + "   " + resultTable[1][9] + "   " + resultTable[2][9] + "   " + resultTable[3][9] + "   " + resultTable[4][9] + "   " + resultTable[5][9] + "   " + resultTable[6][9] + "   " + resultTable[7][9] + "   " + resultTable[8][9] + "   " + resultTable[9][9] + "   " + resultTable[10][9]);
		System.out.println(resultTable[0][10] + "   " + resultTable[1][10] + "   " + resultTable[2][10] + "   " + resultTable[3][10] + "   " + resultTable[4][10] + "   " + resultTable[5][10] + "   " + resultTable[6][10] + "   " + resultTable[7][10] + "   " + resultTable[8][10] + "   " + resultTable[9][10] + "   " + resultTable[10][10]);
	}
	
	//saveKnowledge - this function will write a simple text file with the entire knowledgebase printed inside of it.
	//The file lacks any sort of special and funky formatting since it's always set for 60k iterations of a 4-D matrix formula.
	public void saveKnowledge(String filename) throws IOException{
		File file = new File(filename);
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		for(int i=0;i<size;i++){
			for(int ft=0; ft<50;ft++){
				writer.write(Double.toString(this.knowledgeBase[i][ft]));
				writer.write(" ");
			}
			writer.newLine();
		}
		writer.flush();
	}
	
	//readKnowledge - this function will take our text file generated by the above and attempt to read it and fill out the respective values.
	//This function is a bit more advanced since we have to interpet the string values as doubles, also fill them in the apporpriate places.
	public void readKnowledge(String filename) throws IOException{
		File file = new File(filename);
		Scanner scan = new Scanner(file);
		
		while(scan.hasNext()){
			for(int i=0; i<size; i++){
				for(int ft=0; ft<50; ft++){
					this.knowledgeBase[i][ft] = Double.parseDouble(scan.next());
					System.out.println("Scanned: "+this.knowledgeBase[i][ft]);
				}
			}
		}
		
	}
	
}
