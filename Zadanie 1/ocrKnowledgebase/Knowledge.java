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
		this.knowledgeBase = new double[size][17];
		this.matchBase = new double[sizeMatch][17];
		
		//Prepare the arrays, setting _EVERY_ value to 0 just so that we're sure we're not dancing with random values.
		for(int i=0;i<size;i++){
			for(int ft=0; ft<17; ft++){
				this.knowledgeBase[i][ft] = 0;
			}
		}
		//Essentially the same algorithm, instead working with a different ID iterator so we won't go out of bounds.
		for(int i=0; i<sizeMatch; i++){
			for(int ft=0; ft<17; ft++){
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
				for(int ft=0; ft<17; ft++){
					//If our feature is equal zero, i.e. it's the index, gather it.
					if (ft==0) this.knowledgeBase[i][ft] = extractor.extractLabel(i);
					//If our feature has an index (1,2,3,4,5,6), extract the feature of that index.
					else this.knowledgeBase[i][ft] = extractor.extractFeature(i, ft);
					
					System.out.println("For label ["+this.knowledgeBase[i][0]+"], index#"+i+", extracted feature index#"+ft+" was: "+this.knowledgeBase[i][ft]);
				}
			}
		}
		if (mode.equals("matchbase")){
			//We sorta just copied the above but changed a few things to make it work.
			for(int i=0; i<sizeMatch; i++){
				for(int ft=0; ft<17; ft++){
					//If our feature is equal zero, i.e. it's the index, gather it.
					if (ft==0) this.matchBase[i][ft] = extractor.extractLabel(i);
					//If our feature has an index (1,2,3,4,5,6), extract the feature of that index.
					else this.matchBase[i][ft] = extractor.extractFeature(i, ft);
					
					System.out.println("For label ["+this.matchBase[i][0]+"], index#"+i+", extracted feature index#"+ft+" was: "+this.matchBase[i][ft]);
				}
			}
		}		
	}
	
	//testNumbers - using the k-nearest neighbours function, we'll try to give every set of features from the matchbase it's own
	//custom ID based on what we know in the knowledgebase. Afterwards, we'll match the given ID with the read ID and verify if it's correct.
	//NOTICE: INVOKE THIS FUNCTION -ONLY- AFTER BOTH DATABASES HAVE BEEN FILLED, OTHERWISE  YOU WILL ONLY GET GIBBERISH!!
	public void testNumbers(int k){
		int successValue = 0; //<-- we'll increment this each time we get a correct result.
		
		//Fly through the entire matchBase, going through each one.
		for (int i=0; i<this.sizeMatch; i++){
			double[][] distance = new double[this.size][1];
			//For each one, calculate the relative distance between itself and EVERY member of the knowledgebase.
			//We're using the MANHATTAN METRIC to calculate the distance (aka. pl. "Metryka Uliczna").
			for(int s=0; s<this.size; s++){
				double dstVal = 0;
				for(int ft=1; ft<17; ft++){
					dstVal+=Math.abs(this.matchBase[i][ft] - this.knowledgeBase[s][ft]);
				}
				distance[s][0] = dstVal;
				distance[s][1] = this.knowledgeBase[s][0];
			}
			
			//When we have our distances, we sort them by size, from the closest to the farthest.
			java.util.Arrays.sort(distance, new java.util.Comparator<double[]>() {
			    public int compare(double[] a, double[] b) {
			        return Double.compare(a[0], b[0]);
			    }
			});
			//And we pick the k closest ones.			
			//Be democratic. Count the number of each labels appearing.
			double[] labels = new double[9];
			for(int clr=0;clr<9;clr++){
				labels[clr]=0;
			}
			for(int pick=0; pick<k; pick++){
				labels[(int)distance[pick][1]] += 1;
			}
			
			//And assign the most common label.
			Arrays.sort(labels);
			//Now check if the label is the same as the label we know is true.
		}
		
		
		//All done? Let's check our results:
		double success = ((double)successValue / (double)this.sizeMatch) * 100;
		System.out.println("We're done. Our effective guess success rate measures at: "+success+"%!");
	}
	
	//saveKnowledge - this function will write a simple text file with the entire knowledgebase printed inside of it.
	//The file lacks any sort of special and funky formatting since it's always set for 60k iterations of a 4-D matrix formula.
	public void saveKnowledge(String filename) throws IOException{
		File file = new File(filename);
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		for(int i=0;i<size;i++){
			for(int ft=0; ft<17;ft++){
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
				for(int ft=0; ft<17; ft++){
					this.knowledgeBase[i][ft] = Double.parseDouble(scan.next());
					System.out.println("Scanned: "+this.knowledgeBase[i][ft]);
				}
			}
		}
		
	}
	
}
