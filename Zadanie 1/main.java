import java.io.IOException;

import ocrKnowledgebase.Knowledge;


//For now, we just debug stuff.
public class main {
	
	public static void main(String[] args) throws IOException{
		//ReadDatabase.readLabel("labels.idx1-ubyte");
		//ReadDatabase.readImage("images.idx3-ubyte");
		Knowledge k = new Knowledge(100);
		k.saveKnowledge("testfile.txt");
	}

}
