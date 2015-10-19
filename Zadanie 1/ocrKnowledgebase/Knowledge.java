package ocrKnowledgebase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Knowledge {

	//Variables
	int knowledgeSize = 1000;
	
	//Our knowledgebase, in essence the entire training set inserted in and categorized based on feature.
	//It's a long-ass 2D matrix, with the 0th layer being just the number while the 1th layer being a bit more descriptive...
	// 0 - our number identifier, i.e. 0, 1, 2, 3, ..., 9
	// 1 - first feature of the top box
	// 2 - second feature of the top box
	// 3 - first, second box
	// ...AND SO ON AND SO FORTH. Essentially, the 2nd array has to be the size of [Features]+1.
	double[][] knowledgeBase = new double[knowledgeSize][7];
	
	//The matchbase, essentially a much smaller copy of the database that's initialized during program start.
	//We'll load in our variables here with the index values being set to something entirely space.
	//The pattern of the fields is the same, though the ID field is not strictly locked to 60k examples.
	double[][] matchBase;
	int size;
	
	//Constructor
	//Constructs the Knowledgebase while setting the size of the array of pictures we want to match.
	public Knowledge(int s){
		this.size = s;
		this.matchBase = new double[size][7];
		
		//Prepare the arrays, setting _EVERY_ value to 0 just so that we're sure we're not dancing with random values.
		for(int i=0;i<knowledgeSize;i++){
			for(int ft=0; ft<7; ft++){
				this.knowledgeBase[i][ft] = 0;
			}
		}
		//Essentially the same algorithm, instead working with a different ID iterator so we won't go out of bounds.
		for(int i=0; i<size; i++){
			for(int ft=0; ft<7; ft++){
				this.matchBase[i][ft] = 0;
			}
		}
	}
	
	//Functions
	//saveKnowledge - this function will write a simple text file with the entire knowledgebase printed inside of it.
	//The file lacks any sort of special and funky formatting since it's always set for 60k iterations of a 4-D matrix formula.
	public void saveKnowledge(String filename) throws IOException{
		File file = new File(filename);
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		for(int i=0;i<knowledgeSize;i++){
			for(int ft=0; ft<7;ft++){
				writer.write(Double.toString(this.knowledgeBase[i][ft]));
				writer.write(" ");
			}
			writer.newLine();
		}
		writer.flush();
	}
}
