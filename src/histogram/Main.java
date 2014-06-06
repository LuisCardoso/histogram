package histogram;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import Localizer.Bayesian;
import accesspoint.selection.SelectionAverage;
import filter.AccessPointOccurrence;
import filter.AccessPointRSSIStrength;

public class Main {

	public static void main(String[] args) {
		Histogram histogram = null;
		AccessPointOccurrence occurrency = new AccessPointOccurrence();
		
		
		
		String filepath = "/home/luis/Dropbox/School/Elective/Smart Phones Sensing/Doc/cellsdata/26May2014/Night";
		String pmf_filepath = "/home/swifferayubu/Dropbox/Doc/cellsdata/3_Chosen_AP/2_PMF_AccessPoints_allCells/";

		//Chosen Access Points
		String file_ap1= pmf_filepath +"HISTOGRAM_eduroam_00_1b_90_76_d3_f0";
		String file_ap2= pmf_filepath +"HISTOGRAM_Conferentie-TUD_00_1b_90_76_d3_f6";
		String file_ap3= pmf_filepath +"HISTOGRAM_tudelft-dastud_00_1b_90_76_ce_14";
		String file_ap4= pmf_filepath +"HISTOGRAM_TUvisitor_00_1b_90_76_d3_f3";
	
		Bayesian Bclassifier;
		
		//fetch Training Data
		float [][]ap1_pmf =Bclassifier.fetch_pmf(file_ap1);
		float [][]ap2_pmf =Bclassifier.fetch_pmf(file_ap2);
		float [][]ap3_pmf =Bclassifier.fetch_pmf(file_ap3);
		float [][]ap4_pmf =Bclassifier.fetch_pmf(file_ap4);
	
		
		//AccessPointRSSIStrength rssi_filter = new AccessPointRSSIStrength(filepath);
			
		SelectionAverage selAvg = new SelectionAverage(filepath);
		
		
		Path dir = Paths.get(filepath);
//		
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			
			
			for(Path file : stream) {
				if(file.getFileName().toString().startsWith("c", 0) && 
						file.getFileName().toString().endsWith(".txt")) {
					
					
					
					histogram = new Histogram();
					histogram.generateHistogram(file.toFile());
					histogram.writeHistogramToFile(occurrency);
				}
				
				
			} // for(Path file : stream)
			
			// Write occurrence of each access-point to a file
			occurrency.writeOccurrenceToFile(filepath);
			
			// Compute overall average
			selAvg.computeTotalAverage();
			selAvg.writeOverallAverageToFile();
			
		}
		catch(Exception e) {
			System.out.println("\n\nError: Main.main: \n\n"+e.getMessage());
		}
		
		
	    //initiate classifier
		Bayesian Bclassifier = new Bayesian(ap1_pmf,ap2_pmf, ap3_pmf, ap4_pmf);
	    
		//begin classification
		Bclassifier.bayesian_classify(false);
		
		Bayesian Bclassifier_laplace = new Bayesian(ap1_pmf,ap2_pmf, ap3_pmf, ap4_pmf);
		//Bclassifier_laplace.bayesian_classify(true); 

		
		
		
	}

}
