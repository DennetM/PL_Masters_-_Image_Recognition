import java.io.IOException;

import ocrKnowledgebase.TextureKnowledge;
import ocrRecognizer.TextureExtractor;


//For now, we just debug stuff.
public class main {
	
	public static void main(String[] args) throws IOException{
		
		TextureKnowledge txtKnow = new TextureKnowledge(814, 917, 858, 848, 5, 13);
		//TextureKnowledge txtKnow = new TextureKnowledge(8, 8, 8, 8, 5, 13);
		TextureExtractor txtExtr = new TextureExtractor(txtKnow.getLinenNum(), txtKnow.getSaltNum(), txtKnow.getStrawNum(), txtKnow.getWoodNum());
		txtKnow.gatherKnowledge("Flat", txtExtr);
		txtKnow.gatherKnowledge("FFT", txtExtr);
		
		txtKnow.testFFT(15, "KNN");
		txtKnow.testFlat(15, "KNN");
		//txtKnow.testFFT(15, "BAE");
		//txtKnow.testFlat(15, "BAE");
		
		
		
		/*
		//Testing the kNN Algorithm, also the proper program loop.
		
		//Size setting.
		int knowledgeSize = 60000;
		int matchSize = 10000;
		
		//Spawn the knowledgebase.
		Knowledge database = new Knowledge(knowledgeSize, matchSize);
		//Spawn the extractors
		DataExtractor extrData = new DataExtractor(knowledgeSize);
		DataExtractor extrMatch = new DataExtractor(matchSize);
		
		//Prepare both extractors.
		extrData.prepareLabel(ReadDatabase.readLabel("train-labels.idx1-ubyte", knowledgeSize));
		extrData.prepareImage(ReadDatabase.readImage("train-images.idx3-ubyte", knowledgeSize));
		
		extrMatch.prepareLabel(ReadDatabase.readLabel("t10k-labels.idx1-ubyte", matchSize));
		extrMatch.prepareImage(ReadDatabase.readImage("t10k-images.idx3-ubyte", matchSize));
		
		//Gather the knowledge from both extractors.
		database.gatherKnowledge(extrData, "database");
		database.gatherKnowledge(extrMatch, "matchbase");
		
		//All that done, let's see how effective are we.
		database.testNumbers(8);
		*/
		
		/*Success.
		//Initial read-extract-write test.
		
		//Set the core size, which we'll use throughout the document.
		//IT IS IMPERATIVE EVERYTHING RELATED TO DATA HANDLING IS LIMITED BY THIS NUMBER!!
		int testSize = 1000;
		int testMatchSize = 0;
		
		//Spawn our Knowledgebase.
		Knowledge testKnowledgebase = new Knowledge(testSize, testMatchSize);
		
		//Spawn our data extractor.
		DataExtractor testExtractor = new DataExtractor(testSize);
		
		//Prepare the extractor, filling it with raw data from the files.
		testExtractor.prepareLabel(ReadDatabase.readLabel("train-labels.idx1-ubyte", testSize));
		testExtractor.prepareImage(ReadDatabase.readImage("train-images.idx3-ubyte", testSize));
		
		//Gather the knowledge from the extractor.
		testKnowledgebase.gatherKnowledge(testExtractor, "database");
		
		//Save the knowledge from the database to a file.
		testKnowledgebase.saveKnowledge("testSave.txt");
		*/
	}

}
