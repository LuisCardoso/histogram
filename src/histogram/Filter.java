package histogram;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//import static java.lang.Math;

public class Filter {
	
	
	
	/* This function does the following: 
	 * 1. Goes through each cell
	 * 2. find each access point in a given cell
	 * 3. Filters out Access Points by their rssi strength
	 * */
	static public void filter_rssi_too_strong(){
		
		/* search through each file */
	/*Path dir = Paths.get("/home/luis/Dropbox/School/Elective/Smart Phones Sensing/Doc/cellsdata/26May2014/Night");
		
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			
			
			for(Path file : stream) {
				if(file.getFileName().toString().startsWith("c", 0) && 
						file.getFileName().toString().endsWith(".txt")) {
					
			//		histogram = new Histogram();
			//		histogram.generateHistogram(file.toFile());
			//		histogram.writeHistogramToFile();
				}
				
				
			}
		}
		catch(Exception e) {
			System.out.println("\n\nError: Main.main: \n\n"+e.getMessage());
		}
	
	*/	
		
	    float [] datatest = new float [6]; 
	    datatest[0]= (float)1; 
	    datatest[1]= (float)4; 
	    datatest[2]= (float)2; 
	    datatest[3]= (float)4; 
	    datatest[4]= (float)6; 
	    datatest[5]= (float)9; 
		
	    float std = get_standardDeviation(datatest, get_mean(datatest));
	    
	    System.out.println("Std is:"+std);
		/* find each access point */
		
		
		
		
		
	}
	
	
	
	
	/*get average of an array */
	static public float get_mean(float [] data){
		float average=(float)0;
		float sum=(float)0;
		
		for (int i=0; i<data.length; i++)
		{
			sum += data[i];
		}
		
		average = sum/data.length;
		
		return average;
		
	}
	
	
	
	/* get stadard divation of an array */
	static public float get_standardDeviation(float []data, float data_mean){
		float std=(float)0;
		double temp= (double)0;
	
		/* get variance */
		for(int i=0; i<data.length; i++)
		{
			temp=Math.pow((data[i]-data_mean),2);
			
		}
	    std=(float)temp;
		
		return std;
	}
	
}
