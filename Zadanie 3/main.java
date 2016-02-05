import java.io.IOException;

import algoMain.detectGrape;
import algoSub.imageRW;


public class main {

	public static void main(String[] args) throws IOException{
		int[][][] img = imageRW.readImage("Wino", "count1.bmp");
		//int[][][] img = imageRW.readImage("Wino", "F://Java Projects//PL_Masters_-_Image_Recognition//Zadanie 3//images//count1.bmp");
		
		detectGrape test = new detectGrape(img, 43, 43);
		imageRW.checkMapSave(test.checkMap, 800, 600, "testCheck.png");
	}
}
