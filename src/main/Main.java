package main;

import filter.AccessPointOccurrence;
import filter.AccessPointRSSIStrength;
import filter.SelectionAverage;
import histogram.Histogram;
import histogram.TrainingData;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.TreeMap;

import Localizer.Bayesian;

public class Main {

	public static void main(String[] args) {
		
		int User=1;  //0 = Javier pc, 1=Luis pc, 2=all phones,
		
		
		String folder_base_path = null;
		
		if(User==0)
			folder_base_path = "/home/swifferayubu/Dropbox/Doc/";
		else if (User==1)
			folder_base_path =	"/home/luis/Dropbox/School/Elective/Smart Phones Sensing/Doc/";
		else if (User==2)
			folder_base_path =	"/Downloads/";
		
		String folder_name = "cellsdata/";		//main folder
	
		String filepath= folder_base_path + folder_name;	
		//String filepath = "/home/luis/Dropbox/School/Elective/Smart Phones Sensing/Doc/cellsdata/26May2014/Night";

		
		
		/* *********************************************
		 * Phase 1: Create Histogram and PMF for all Access points
		 * Phase 2: Filter Access Points 
		 * ********************************************* */
		Histogram histogram = null;
		AccessPointOccurrence occurrency = new AccessPointOccurrence();
		//AccessPointRSSIStrength rssi_filter = new AccessPointRSSIStrength(filepath);
		
		SelectionAverage selAvg = new SelectionAverage(filepath);
				
		
		Path dir = Paths.get(filepath);
		
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			
			
			for(Path file : stream) {
				if(file.getFileName().toString().startsWith("c", 0) && 
						file.getFileName().toString().endsWith(".txt")) {
					
					histogram = new Histogram();  
					
					//step 1: Red raw data
					//step 2: Create histogram of raw data
					histogram.generateHistogram(file.toFile());
																
					//step 3: compute access-point occurrences 
					histogram.writeHistogramToFile(occurrency); 
				}
				
				
			} // for(Path file : stream)
			
			// Write occurrence of each access-point to a file
			occurrency.writeOccurrenceToFile(filepath);
			
			// Compute overall average
			//todo: Can the percentage be given as parameter?
			selAvg.computeTotalAverage(); // step 4: Filter by coverage, > 50%
			selAvg.writeOverallAverageToFile();
			
			
			
		}
		catch(Exception e) {
			System.out.println("\n\nError: Main.main: \n\n"+e.getMessage());
		}
		
		//step 5: Filter by RSSI average strength
		TreeMap<String,Float> Data_filterphase1 = new TreeMap<String, Float>();

		AccessPointRSSIStrength rssi_filter= new AccessPointRSSIStrength(filepath); //new
		
		Data_filterphase1 = rssi_filter.fetch_AP_and_RSSIavg(); //fetch access points from filter 1
	    rssi_filter.filter_rssi_too_strong_Tree(Data_filterphase1);//filter data by rssi strength
			
	
	    rssi_filter.display_normalAP();
	    rssi_filter.save_filtered_AP();
	

	    //step 6: Choose X amount of Access points 
	    System.out.println("Choose some access points ");
	    System.out.println("number access points chosen");
	    //display now the list of chosen access points 
	 
	    //read the accesspoints
		
		
	    
	    
	    
	    //step 7: Create PMF table for each chosen Access Point
		
	    // Set of trainingdata. Each trainingdata is associated to one access-point
	    ArrayList<TrainingData> tds = new ArrayList<TrainingData>();
	    
	    // Selected access-point names by the user
	    ArrayList<String> names = new ArrayList<String>();
	    names.add("Conferentie-TUD_00_1b_90_76_d3_f6");
	    names.add("eduroam_00_1b_90_76_d3_f0");
	    
	    // new trainingdata
	    TrainingData td;
	    
	    // new access-point name to be associated with the trainingdata
	    String name = null;
	    
	    
	      for(int i = 0; i < names.size(); i++) {
	           
	           name = names.get(i);
	           
	      		td = new TrainingData(name, filepath);
	      
	     	 	td.createPMFTable();
	      		td.createHistogramTable();
	      		
	      		tds.add(td);
	      }
	     
	    
	    
//	    createPMFTable();
	
		/* *********************************************
		 *  Phase 3: Apply Bayesian classification using the chosen Access points 
		 * ********************************************* */
//		String pmf_filepath = "/home/swifferayubu/Dropbox/Doc/cellsdata/3_Chosen_AP/2_PMF_AccessPoints_allCells/";
		folder_name="3_Chosen_AP/2_PMF_AccessPoints_allCells/";
		String pmf_filepath = filepath+folder_name;
		
		
		//fetch names of chosen access points 
		//todo: Fetch names automatically. ArrayList<String> Ap_name = new ArrayList<String>()
		String Ap1_name= "PMF_eduroam_00_1b_90_76_d3_f0";
		String Ap2_name= "PMF_Conferentie-TUD_00_1b_90_76_d3_f6";
		String Ap3_name= "PMF_tudelft-dastud_00_1b_90_76_ce_14";
		String Ap4_name= "PMF_TUvisitor_00_1b_90_76_d3_f3";
		
		
		//Chosen Access Points
		String file_ap1= pmf_filepath +Ap1_name;
		String file_ap2= pmf_filepath +Ap2_name;
		String file_ap3= pmf_filepath +Ap3_name;
		String file_ap4= pmf_filepath +Ap4_name;
	
		Bayesian Bclassifier = new Bayesian();
		
		//fetch Training Data
		float [][]ap1_pmf =Bclassifier.fetch_pmf(file_ap1);
		float [][]ap2_pmf =Bclassifier.fetch_pmf(file_ap2);
		float [][]ap3_pmf =Bclassifier.fetch_pmf(file_ap3);
		float [][]ap4_pmf =Bclassifier.fetch_pmf(file_ap4);
		
		
	    //initiate classifier
		//Todo: Allow Bayesian to accept multiple access points
		Bclassifier = new Bayesian(ap1_pmf,ap2_pmf, ap3_pmf, ap4_pmf); 	//step 8: Apply Bayesian classification
		
	    
		//begin classification
		Bclassifier.bayesian_classify(false);
		
		Bayesian Bclassifier_laplace = new Bayesian(ap1_pmf,ap2_pmf, ap3_pmf, ap4_pmf);
		//Bclassifier_laplace.bayesian_classify(true); 

		
		
		
	}

}
