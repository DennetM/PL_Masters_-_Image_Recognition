import java.io.IOException;

import algoMain.detectGrape;
import algoSub.imageRW;


public class main {

	public static void main(String[] args) throws IOException{
		int[][][] img = imageRW.readImage("Wino", "count1.bmp");
		
		detectGrape test = new detectGrape(img, 43, 43);
	}
}
