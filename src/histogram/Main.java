package histogram;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import filter.AccessPointOccurrence;

public class Main {

	public static void main(String[] args) {
		Histogram histogram = null;
		AccessPointOccurrence occurrency = new AccessPointOccurrence();
		String filepath = "/home/luis/Dropbox/School/Elective/Smart Phones Sensing/Doc/cellsdata/26May2014/Night";
		
		Path dir = Paths.get(filepath);
		
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			
			
			for(Path file : stream) {
				if(file.getFileName().toString().startsWith("c", 0) && 
						file.getFileName().toString().endsWith(".txt")) {
					
					
					
					histogram = new Histogram();
					histogram.generateHistogram(file.toFile());
					histogram.writeHistogramToFile(occurrency);
				}
				
				
			}
			occurrency.writeOccurrenceToFile(filepath);
			
		}
		catch(Exception e) {
			System.out.println("\n\nError: Main.main: \n\n"+e.getMessage());
		}
		
	}

}
