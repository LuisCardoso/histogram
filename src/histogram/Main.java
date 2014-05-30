package histogram;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import accesspoint.selection.SelectionAverage;
import filter.AccessPointOccurrence;

public class Main {

	public static void main(String[] args) {
//		Histogram histogram = null;
//		AccessPointOccurrence occurrency = new AccessPointOccurrence();
		
		String filepath = "/home/luis/Dropbox/School/Elective/Smart Phones Sensing/Doc/cellsdata/26May2014/Night";
		
		SelectionAverage selAvg = new SelectionAverage(filepath);
		
		
		Path dir = Paths.get(filepath);
//		
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
//			
//			
//			for(Path file : stream) {
//				if(file.getFileName().toString().startsWith("c", 0) && 
//						file.getFileName().toString().endsWith(".txt")) {
//					
//					
//					
//					histogram = new Histogram();
//					histogram.generateHistogram(file.toFile());
//					histogram.writeHistogramToFile(occurrency);
//				}
//				
//				
//			} // for(Path file : stream)
//			
//			// Write occurrence of each access-point to a file
//			occurrency.writeOccurrenceToFile(filepath);
			
			// Compute overall average
			selAvg.computeTotalAverage();
			selAvg.writeOverallAverageToFile();
			
		}
		catch(Exception e) {
			System.out.println("\n\nError: Main.main: \n\n"+e.getMessage());
		}
		
	}

}
