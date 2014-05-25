package histogram;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

	public static void main(String[] args) {
		Histogram histogram = null;
		
		Path dir = Paths.get("/home/luis/Dropbox/School/Elective/Smart Phones Sensing/Doc/cellsdata/");
		
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			
			
			for(Path file : stream) {
				if(file.getFileName().toString().startsWith("c", 0) && 
						file.getFileName().toString().endsWith(".txt")) {
					
					histogram = new Histogram();
					histogram.generateHistogram(file.toFile());
					histogram.writeHistogramToFile();
					
//					System.out.println("file.getFileName: "+file.getFileName());
//					System.out.println("file.toAbsolutePath: "+file.toAbsolutePath());
//					System.out.println("file.getFileName.startsWith(c): "+file.getFileName().toString().startsWith("c", 0));
//					System.out.println("file.getFileName.endsWith(txt): "+file.getFileName().toString().endsWith(".txt"));
				}
				
				
			}
		}
		catch(Exception e) {
			System.out.println("\n\nError: Main.main: \n\n"+e.getMessage());
		}
		
	}

}
