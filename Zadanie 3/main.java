import java.io.IOException;

import algoMain.detectGrape;
import algoSub.imageRW;


public class main {

	public static void main(String[] args) throws IOException{
		int[][][] img = imageRW.readImage("Wino", "count13.bmp");
		
		detectGrape test = new detectGrape(img, 50, 50, 2);
		//imageRW.checkMapSave(test.checkMap, 800, 600, "testCheck.png");
		imageRW.saveCompound(test, 800, 600, "compound.png");
	}
}
