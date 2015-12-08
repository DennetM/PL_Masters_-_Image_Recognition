package ocrReadWrite;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

//This class exists solely to host the two functions made solely to read the IDX format and work with it.
//As thus, they will lack variables or constructors of any form, and instead only hold two functions of public static void form.
//Meaning... we can use them anywhere with just a class function call, we don't really want to be wasting space making a ReadDatabase object.
public class ReadDatabase {

	//readLabel - similar to the above, instead we read labels only.
	//name - same as above, since we want to read the file specified by us.
	public static int[] readLabel(String filename, int num) throws IOException{
		//Start with the basic operation to open the file.
		FileInputStream File = null;
		String filepath = filename;
		
		File = new FileInputStream(filepath);
		//Read the magic number (wtf is that?)
		int magicNumber = (File.read() << 24) | (File.read() << 16) | (File.read() << 8) | (File.read());
		//Check:
		System.out.println("Magic Number: "+magicNumber);
		
		//Read the number of items in the list (important!)
		int itemNumber = (File.read() << 24) | (File.read() << 16) | (File.read() << 8) | (File.read());
		//Check:
		System.out.println("Item Number: "+itemNumber);
		
		//Read the labels one by one and slap them into an array of ints.
		int[] labels = new int[num];
		for (int i=0; i<num; i++){
			labels[i] = File.read();
			//Check(careful!):
			//if (i<99) System.out.println("Lavel "+i+": "+labels[i]);
		}
		File.close();
		return labels;
	}
	
	
	//readImage - reads the images from an IDX file, simple as that.
	//name - a String with the filename, so that we can dynamically flip the class to read whatever file we need.
	public static int[][][] readImage(String filename, int num) throws IOException{
		
		FileInputStream File = null;
		String filepath = filename;
		
		File = new FileInputStream(filepath);
		int magicNumber = (File.read() << 24) | (File.read() << 16) | (File.read() << 8) | (File.read());
		//Check:
		System.out.println("Magic Number: "+magicNumber);
		
		//Read the number of items in the list (important!)
		int itemNumber = (File.read() << 24) | (File.read() << 16) | (File.read() << 8) | (File.read());
		//Check:
		System.out.println("Item Number: "+itemNumber);
		
		//Read the number of rows and columns, respectively, that each number has:
		int rowNumber = (File.read() << 24) | (File.read() << 16) | (File.read() << 8) | (File.read());
		int columnNumber = (File.read() << 24) | (File.read() << 16) | (File.read() << 8) | (File.read());
		//Check:
		System.out.println("The standard image is a matrix sized "+rowNumber+"x"+columnNumber);
		
		//Compose our array of images. The pixels are valued from 0 to 255, each in 1 byte data storage.
		int[][][]images = new int[num][rowNumber][columnNumber];
		for(int i=0; i<num;i++){
			for(int k=0; k<columnNumber; k++){
				for(int r=0; r<rowNumber; r++){
					images[i][r][k] = File.read();
					//Check, again, careful with this:
					//if (i<2) System.out.println("Value["+i+"] : "+images[i][r][k]);
				}
			}
		}
		File.close();
		return images;
	}
}
