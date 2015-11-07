import java.awt.event.TextEvent;
import java.io.IOException;

import ocrKnowledgebase.Knowledge;
import ocrReadWrite.ReadDatabase;
import ocrRecognizer.DataExtractor;


//For now, we just debug stuff.
public class main {
	
	public static void main(String[] args) throws IOException{
		
		/*Success.
		//Initial read-extract-write test.
		
		
		//Set the core size, which we'll use throughout the document.
		//IT IS IMPERATIVE EVERYTHING RELATED TO DATA HANDLING IS LIMITED BY THIS NUMBER!!
		int testSize = 100;
		
		//Spawn our Knowledgebase.
		Knowledge testKnowledgebase = new Knowledge(testSize);
		
		//Spawn our data extractor.
		DataExtractor testExtractor = new DataExtractor(testSize);
		
		//Prepare the extractor, filling it with raw data from the files.
		testExtractor.prepareLabel(ReadDatabase.readLabel("train-labels.idx1-ubyte", testSize));
		testExtractor.prepareImage(ReadDatabase.readImage("train-images.idx3-ubyte", testSize));
		
		//Gather the knowledge from the extractor.
		testKnowledgebase.gatherKnowledge(testExtractor);
		
		//Save the knowledge from the database to a file.
		testKnowledgebase.saveKnowledge("testSave.txt");
		*/
	}

}
