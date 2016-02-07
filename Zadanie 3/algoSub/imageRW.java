package algoSub;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import algoMain.detectGrape;
import algoMain.detectNut;


public class imageRW {
	
	//No constructors or variables - PUBLIC STATIC LAND~!
	
	//readImage - reads the image into a "3D" table.
	//The table's first column is the channel switch. 0 - red, 1 - green, 2 - blue.
	public static int[][][] readImage(String type, String filename) throws IOException{
		String path = type+"/"+filename;
		//String path = filename;
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
		System.out.println("Image read.");
		return intImg;
	}
	
	public static void checkMapSave(int[][] map, int xwid, int ywid, String name) throws IOException{
		BufferedImage saveImg = new BufferedImage(xwid, ywid, BufferedImage.TYPE_INT_RGB);
		for(int x=0;x<xwid;x++){
			for(int y=0;y<ywid;y++){
				Color col = new Color(map[x][y], map[x][y], map[x][y]);
				saveImg.setRGB(x,y,col.getRGB());
			}
		}
		
		File file = new File(name);
		ImageIO.write(saveImg, "PNG", file);
	}
	
	public static void saveCompound(detectGrape grep, int xwid, int ywid, String name) throws IOException{
		BufferedImage saveImg = new BufferedImage(xwid, ywid, BufferedImage.TYPE_INT_RGB);
		for(int x=0;x<xwid;x++){
			for(int y=0;y<ywid;y++){
				Color col = new Color(grep.img[0][x][y], grep.img[1][x][y], grep.img[2][x][y]);
				if(grep.checkMap[x][y]==0) col = new Color(grep.img[0][x][y]/4, grep.img[1][x][y]/4, grep.img[2][x][y]/4);
				
				saveImg.setRGB(x, y, col.getRGB());
			}
		}
		
		File file = new File(name);
		ImageIO.write(saveImg, "PNG", file);
	}
	
	public static void saveCompound(detectNut grep, int xwid, int ywid, String name) throws IOException{
		BufferedImage saveImg = new BufferedImage(xwid, ywid, BufferedImage.TYPE_INT_RGB);
		for(int x=0;x<xwid;x++){
			for(int y=0;y<ywid;y++){
				Color col = new Color(grep.img[0][x][y], grep.img[1][x][y], grep.img[2][x][y]);
				if(grep.checkMap[x][y]==0) col = new Color(grep.img[0][x][y]/4, grep.img[1][x][y]/4, grep.img[2][x][y]/4);
				
				saveImg.setRGB(x, y, col.getRGB());
			}
		}
		
		File file = new File(name);
		ImageIO.write(saveImg, "PNG", file);
	}

}
