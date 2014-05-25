package histogram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Histogram {
	
	File cell;
	private ArrayList<AccessPoint> aps = new ArrayList<AccessPoint>();
	
	
	public Histogram() {
		
	}
	
	
	/*
	 * This function reads data from a cell file
	 * This function generates a histogram from the corresponding file
	 */
	public void generateHistogram(File file) {
		try {
			// Read in the cell file
			cell = file;
			Scanner reader = new Scanner(cell);
			reader.useDelimiter("\\s*[,\n\r]\\s*");
			
			String ssid = "";
			String bssid = "";
			int level = 1;
			String trash = "";
			
			boolean skip_first_line = true;
			String debug = "";
			String debug_p = "";
			int debug_i = 0;
			
			// Check if there is a next line in the file
			while(reader.hasNext()) {
				
				// The first line of each file is just the header, no values in there
				if(skip_first_line) {
					
					skip_first_line = false;
					
					reader.nextLine();
//					
//					for(int i = 0; i < 7; i++) {
//						reader.next();
//					}
					
					continue;
				}
					
				
				
				// If there is a next line than iterate over its columns (tokens)
				for(int i = 0; i < 7; i++) {
					
					// note: if i > 3 then break
					
					
					// Retrieve only the required data
					switch(i) {
					case 1: ssid = reader.next(); break; // Retrieves the ssid
					case 2: bssid = reader.next(); break; // Retrieves the rssid
					case 3: level = reader.nextInt(); break; // Retrieves the level
					default: trash = reader.next(); // Otherwise just skip to the next token
					}
					
					if(i == 0){
						debug = trash;
					}
					
				}
				
				if(!debug.equalsIgnoreCase(debug_p) && debug_i <= 100) {
					System.out.println("SampleId: "+debug);
					debug_p = debug;
				}
				
				// If the accesspoint does not exist in the arraylist
				// then create a new accesspoint in put it in the arraylist
				if(!accessPointExist(ssid, bssid)) {
					aps.add(new AccessPoint(ssid, bssid, level));
				}
				else {
					// If the accesspoint already exists in the arraylist than increase its level (rssi) frequency of occurrence.
					aps.get(getAccessPointIndex(ssid, bssid)).increaseFrequency(level);
				}
				debug_i++;
			}
			
			reader.close();
			
			
		}
		catch(FileNotFoundException fnfe) {
			System.out.println("\n\nHistogram.readCell:\n\n"+ fnfe.getMessage());
		}
	}
	
	
	/*
	 * This function returns the corresponding index of the accesspoint in the arraylist
	 */
	private int getAccessPointIndex(String ssid, String bssid) {
		
		for(int i = 0; i < aps.size(); i++) {
			if(aps.get(i).getSSID().equalsIgnoreCase(ssid) && 
					aps.get(i).getBSSID().equalsIgnoreCase(bssid)) {
				
				return i;
			}
		}
		
		return -1;
	}
	
	
	/*
	 * This function checks if the access point already exists in the arraylist
	 * Returns true if it already exists, otherwise false
	 */
	private boolean accessPointExist(String ssid, String bssid) {
		
		AccessPoint ap = null;
		
		for(int i = 0; i < aps.size(); i++) {
			ap = aps.get(i);
			
			if(ap.getSSID().equalsIgnoreCase(ssid) &&
					ap.getBSSID().equalsIgnoreCase(bssid)) {
				
				return true;
			}
		}
		return false;
	}
	
	
	/*
	 * This writes the generated histogram to a file
	 */
	public void writeHistogramToFile() {
		try {
			PrintWriter writer = null;
			AccessPoint ap = null;
			int [] level_frequency = null;
			
			for(int i = 0; i < aps.size(); i++) {
				
				// Create new directory for each cell
				File file = new File(
						cell.getParent()+
						"/histogram/"+
						cell.getName().substring(0,(cell.getName().length()-4)) // extract the name of the file without the .txt extension
						);
				
				// If the directory does not exist, then make one
				if(!file.exists()) {
					if(file.mkdirs())
						System.out.println("Directory created!");
					else
						System.out.println("Failed to create directory!");
				}
				
				// Generate a new filename combining the SSID and BSSID of the accesspoint, such that this filename is unique
				String filename = aps.get(i).getSSID()+
						"_"+
						aps.get(i).getBSSID().replace(':', '_'); // replace the colons (:) with underscore (_), otherwise the filesystem will reject it
				
				// Creates a new file in its corresponding directory
				writer = new PrintWriter(
						file.getPath()+
						"/"+filename+
						".txt"
						);
				
				// get the ith accesspoint in the arraylist
				ap = aps.get(i);
				
				// get the frequency of occurrence of each level for this accesspoint
				level_frequency = ap.getLevelFrequencyAsArray();
				
				for(int j = 0; j < level_frequency.length; j += 2) {
					writer.append(level_frequency[j] +","+level_frequency[j+1]+"\n");
				}
				writer.close();
			}
			
		}
		catch(FileNotFoundException fnfe) {
			System.out.println("\n\nFile not found: \n\n"+ fnfe.getMessage());
		}
		catch(SecurityException se) {
			System.out.println("\n\nSecurity: \n\n"+ se.getMessage());
		}	
	}
}
