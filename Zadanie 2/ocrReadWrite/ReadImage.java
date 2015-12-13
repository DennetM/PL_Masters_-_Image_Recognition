package ocrReadWrite;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

//Another copy of ReadDatabase, but this time will read images instead.
public class ReadImage {
	
	//No constructors nor variables this is public static territory.
	
	public static void saveTestFunction(int[][] img, String filename) throws IOException{
		BufferedImage saveImg = new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB);
		for(int x=0;x<512;x++){
			for(int y=0;y<512;y++){
				Color col = new Color(img[x][y], img[x][y], img[x][y]);
				saveImg.setRGB(x,y,col.getRGB());
			}
		}
		
		File file = new File(filename);
		ImageIO.write(saveImg, "PNG", file);
	}
	
	public static int[][] readTest(String file) throws IOException{
		String path = "Test/"+file;
		System.out.println("Reading: "+path);
		BufferedImage img = ImageIO.read(new File(path));
		Raster rastr = img.getData();
		int[][] intImg = new int[img.getWidth()][img.getHeight()];
		
		for(int x=0; x<img.getWidth(); x++){
			for(int y=0; y<img.getHeight(); y++){
				//Color col = new Color(img.getRGB(x, y));
				//intImg[x][y] = col.getRed(); // It's greyscale, so R=G=B.
				intImg[x][y] = rastr.getSample(x, y, 0);
			}
		}
		return intImg;
	}
	
	public static int[][] readTrain(String type, String file) throws IOException{
		String path = "Train/"+type+"/"+file;
		System.out.println("Reading: "+path);
		BufferedImage img = ImageIO.read(new File(path));
		Raster rastr = img.getData();
		int[][] intImg = new int[img.getWidth()][img.getHeight()];
		
		for(int x=0; x<img.getWidth(); x++){
			for(int y=0; y<img.getHeight(); y++){
				//Color col = new Color(img.getRGB(x, y));
				//intImg[x][y] = col.getRed(); // It's greyscale, so R=G=B.
				intImg[x][y] = rastr.getSample(x, y, 0);
			}
		}
		return intImg;
	}
	
	public static int[][] readFilter(String file) throws IOException{
		String path = "Filter/"+file;
		System.out.println("Reading: "+path);
		BufferedImage img = ImageIO.read(new File(path));
		Raster rastr = img.getData();
		int[][] intImg = new int[img.getWidth()][img.getHeight()];
		
		for(int x=0; x<img.getWidth(); x++){
			for(int y=0; y<img.getHeight(); y++){
				//Color col = new Color(img.getRGB(x, y));
				//intImg[x][y] = col.getRed(); // It's greyscale, so R=G=B.
				intImg[x][y] = rastr.getSample(x, y, 0);
			}
		}
		return intImg;
	}

}
