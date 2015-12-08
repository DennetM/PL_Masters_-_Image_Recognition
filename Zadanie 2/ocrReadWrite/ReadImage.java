package ocrReadWrite;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

//Another copy of ReadDatabase, but this time will read images instead.
public class ReadImage {
	
	//No constructors nor variables this is public static territory.
	
	public static int[][] read(String type, String file) throws IOException{
		BufferedImage img = ImageIO.read(new File("Train/"+type+"/"+file));
		int[][] intImg = new int[img.getWidth()][img.getHeight()];
		
		for(int x=0; x<img.getWidth(); x++){
			for(int y=0; y<img.getHeight(); y++){
				Color col = new Color(img.getRGB(x, y));
				intImg[x][y] = col.getRed(); // It's greyscale, so R=G=B.
			}
		}
		return intImg;
	}

}
