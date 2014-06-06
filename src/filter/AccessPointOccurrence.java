package filter;

import histogram.Histogram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class AccessPointOccurrence {
	private TreeMap <String, Integer> aps = new TreeMap <String, Integer>();
	
	
	public AccessPointOccurrence() {
		
	}
	
	
	/*
	 * This method increases the number of occurrences of the given access-point
	 */
	public void increaseOccurrenceOf(String name) {
		int occurance = 0;
		
		// checks if the given access-point already exists
		if(aps.containsKey(name)) {
			occurance = aps.get(name).intValue();
			occurance++;
			
			aps.put(name, Integer.valueOf(occurance));
		}
		else {
			// If the given access-point appears for the first time it 
			aps.put(name, Integer.valueOf(1));
		}
	}
	
	
	/*
	 * This method writes the occurrence of each access-point to a file
	 */
	public void writeOccurrenceToFile(String filepath) {
		
		try {
			String folder_name = "1_RawUnselected_AP/occurrence/";
			String filename = "occurrence.txt";
			
			//File file = new File(filepath+"/occurrence");
			File file = new File(filepath+folder_name);
			
			
			// If the directory does not exist, then make one
			if(!file.exists()) {
				if(file.mkdirs())
					System.out.println("Directory created!");
				else
					System.out.println("Failed to create directory!");
			}
			
			
			PrintWriter writer = new PrintWriter(file.getPath() +"/"+ filename);
			
			
			Set <String> setOfKeys =  aps.keySet();
			Iterator <String> iterator = setOfKeys.iterator();
			
			String access_point_name = "";
			int occurrence = 0;
			
			while(iterator.hasNext()) {
				access_point_name = iterator.next();
				occurrence = aps.get(access_point_name).intValue();
				
				
				// write the values to the file
				writer.append(
						access_point_name+
						","+
						occurrence+
						"\n"
						);
			}
			
			writer.close();
			
			
		}
		catch(FileNotFoundException fnfe) {
			System.out.println("\n\nFile not found: \n\n"+ fnfe.getMessage());
		}
		catch(SecurityException se) {
			System.out.println("\n\nSecurity: \n\n"+ se.getMessage());
		}
	}
}
