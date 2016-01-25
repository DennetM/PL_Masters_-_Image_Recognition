package algoSub;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class imageRW {
	
	//No constructors or variables - PUBLIC STATIC LAND~!
	
	//readImage - reads the image into a "3D" table.
	//The table's first column is the channel switch. 0 - red, 1 - green, 2 - blue.
	public static int[][][] readImage(String type, String filename) throws IOException{
		String path = type+"/"+filename;
		BufferedImage img = ImageIO.read(new File(path));
		int[][][] intImg = new int[3][img.getWidth()][img.getHeight()];
		
		for(int x=0; x<img.getWidth(); x++){
			for(int y=0; y<img.getHeight(); y++){
				Color col = new Color(img.getRGB(x, y));
				intImg[0][x][y] = col.getRed();
				intImg[1][x][y] = col.getGreen();
				intImg[2][x][y] = col.getBlue();
			}
		}
		return intImg;
	}

}
