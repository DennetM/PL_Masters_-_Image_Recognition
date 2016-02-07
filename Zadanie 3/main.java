import java.io.IOException;

import algoMain.detectNut;
import algoSub.imageRW;


public class main {

	public static void main(String[] args) throws IOException{
		int[][][] img = imageRW.readImage("OreNew", "3.jpg");
		//int[][][] img = imageRW.readImage("OreNew", "F://Java Projects//PL_Masters_-_Image_Recognition//Zadanie 3//OrzeRaw//3.jpg");
		
		detectNut test = new detectNut(img, 24, 24, 12, 40);
		//imageRW.checkMapSave(test.checkMap, 800, 600, "testCheck.png");
		imageRW.saveCompound(test, 800, 562, "compound.png");
	}
}
