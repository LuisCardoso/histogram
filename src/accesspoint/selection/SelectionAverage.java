package accesspoint.selection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class SelectionAverage {
	private TreeMap<String, Float> avg = new TreeMap<String, Float>();
	
	// commulative: rssi * freq
	private int total_rssi;
	
	// commulative: freq
	private int total_freq;
	
	private String filepath;
	
	public SelectionAverage(String filepath) {
		this.filepath = filepath;
	}
	
	
	/*
	 * This method computes the total average of each selected access-point
	 */
	public void computeTotalAverage() {
		try {
			// Read in the cell file
			String folder_name="2_Filter/selection/";
			//String selection = filepath+"/selection/selection.txt";
			String selection = filepath+folder_name+"selection.txt";
			
			File file = new File(selection);
			
			Scanner reader = new Scanner(file);
			reader.useDelimiter("\\s*[,\n\r]\\s*");
			
			String access_point = "";
			
//			int debug_i = 0;
			
			// Check if there is a next line in the file
			while(reader.hasNext()) {
				
				// Get the next selected access-point name
				access_point = reader.next();
				
				computeAverage(access_point);
				
				reader.nextLine();
				
				// Reset variables for next iteration
				total_freq = 0;
				total_rssi = 0;
				}
				
				reader.close();
		}
		catch(Exception e) {
			System.out.println("\n\nHistogram.readCell:\n\n"+ e.getMessage());
		}
	}


	/*
	 * This function calculates the average of each access-point
	 */
	private void computeAverage(String access_point) {
		
//		String selection = "/home/luis/Dropbox/School/Elective/Smart Phones Sensing/Doc/cellsdata/26May2014/Night/selection/selection.txt";
		Scanner reader = null;
		
		// rssi value
		int rssi = 1;
		// freq of rssi
		int freq = 0;
		// pmf of rssi
		float pmf = 0;
		
		// Fetch the main dir
		//Path dir = Paths.get(filepath+"/histogram");
		Path dir = Paths.get(filepath+"1_RawUnselected_AP/histogram/");
		
		String filename;
		
		try (DirectoryStream<Path> dirs = Files.newDirectoryStream(dir)) {
			
			// Iterate over each subdir (cell) in the main dir
			for(Path subdir : dirs) {
				
				// Fetch all files of each subdir (cell)
				DirectoryStream<Path> files = Files.newDirectoryStream(subdir);
				
				// Iterate over each file in the current subdir
				for(Path file : files) {
					
					// Remove the extension .txt from the filename
					filename = file.getFileName().toString().substring(0, file.getFileName().toString().length()-4);
					
					// If the filename i.e. the access-point name equals the name of the selected access-point
					// then read it
					if(filename.equalsIgnoreCase(access_point)) {
						
						// Read the file
						reader = new Scanner(file.toAbsolutePath());
						
						// The values are comma separated
						reader.useDelimiter("\\s*[,\n\r]\\s*");
						
						// Iterate over each token
						while(reader.hasNext()) {
							rssi = reader.nextInt();
							freq = reader.nextInt();
							pmf = reader.nextFloat();
							
							// Compute the total rssi and freq
							compute(rssi, freq);
							
							reader.nextLine();
						} // while(reader.hasNext())
						
						break; // exit this subdir and move to the next if any
						
					} // if(file.getFileName().toString().equalsIgnoreCase(access_point))
				} // for(Path file : files)
			} // for(Path subdir : dirs)
			
			// compute the overall average of the current access-point
			float total_avg = total_rssi/total_freq;
			
			avg.put(access_point, Float.valueOf(total_avg));
			
		} // try
		catch(Exception e) {
			System.out.println("\n\nError: Main.main: \n\n"+e.getMessage());
		}
	}


	/*
	 * This function computes the total rssi and freq
	 */
	private void compute(int rssi, int freq) {
		total_freq += freq;
		total_rssi += rssi * freq;
	}
	
	
	/*
	 * This method writes the overall average of each access-point to a file
	 */
	public void writeOverallAverageToFile() {
		try {
			String filename = "selectionAvg.txt";
			String folder_name="2_Filter/selection/";		
			
		
			File file = new File(filepath+folder_name);
		//	File file = new File(filepath+"/selection");
			//String folder_name="2_Filter/selection/";
			//File file = new File(filepath+folder_name);
			
			
			// If the directory does not exist, then make one
			if(!file.exists()) {
				if(file.mkdirs())
					System.out.println("Directory created!");
				else
					System.out.println("Failed to create directory!");
			}
			
			
			PrintWriter writer = new PrintWriter(file.getPath() +"/"+ filename);
			
			
			Set <String> setOfKeys =  avg.keySet();
			Iterator <String> iterator = setOfKeys.iterator();
			
			String access_point_name = "";
			float average = 0;
			
			while(iterator.hasNext()) {
				access_point_name = iterator.next();
				average = avg.get(access_point_name).floatValue();
				
				
				// write the values to the file
				writer.append(
						access_point_name+
						","+
						average+
						"\n"
						);
			} // while(iterator.hasNext())
			
			writer.close();
			
		} // try
		catch(FileNotFoundException fnfe) {
			System.out.println("\n\nFile not found: \n\n"+ fnfe.getMessage());
		}
		catch(SecurityException se) {
			System.out.println("\n\nSecurity: \n\n"+ se.getMessage());
		}
	}
}
