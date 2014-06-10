package main;

import filter.AccessPointOccurrence;
import filter.AccessPointRSSIStrength;
import filter.SelectionAverage;

import filter.SelectionCoverage;
import gui.DataTable;

import histogram.Histogram;
import histogram.TrainingData;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

import Localizer.Bayesian;

public class Main {

	public static void main(String[] args) {
		
		int User=0;  //0 = Javier pc, 1=Luis pc, 2=all phones,
		
		
		String folder_base_path = null;
		
		if(User==0){
			folder_base_path = "/home/swifferayubu/Dropbox/Test/";
			//folder_base_path = "/home/swifferayubu/Dropbox/Doc/";
		}
		else if (User==1)
			folder_base_path =	"/home/luis/Dropbox/School/Elective/Smart Phones Sensing/Doc/";
		else if (User==2)
			folder_base_path =	"/Downloads/";
		
		String root_folder_name = "cellsdata/";		//main folder
	
		String filepath= folder_base_path + root_folder_name;	
			
		
		/* *********************************************
		 * Phase 1: Create Histogram and PMF for all Access points
		 * Phase 2: Filter Access Points 
		 * ********************************************* */
		Histogram histogram = null;
		AccessPointOccurrence occurrency = new AccessPointOccurrence();
	
		int numberOfCells =17;
		int coverage_percentage= 50;
		SelectionCoverage selCvg = new SelectionCoverage(numberOfCells,coverage_percentage,filepath);
		SelectionAverage selAvg = new SelectionAverage(filepath);
				
		//fetch path to the Raw sampled data, saved in .txt format
		String pathToRawData= "1_RawUnselected_AP/";
		Path dir = Paths.get(filepath+pathToRawData);
		
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
		
			// step 4: Filter by coverage, > 50%
			//filter Access-Point by coverage and save results
			selCvg.generateSelection();
			selCvg.writeSelectionToFile();
			
			// Compute overall average
			//todo: Can the percentage be given as parameter?
			selAvg.computeTotalAverage(); 
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
			
	    
	   // rssi_filter.display_normalAP();
	    rssi_filter.save_filtered_AP();
	    
	    

	    //step 6: Choose X amount of Access points as TrainingData
	    Scanner keyboard=new Scanner(System.in);
	    ArrayList<String> chosen_ap_names = new ArrayList<String>();
	  
	    chosen_ap_names = getNewAccessPoints(keyboard,rssi_filter);
	    
		    
	    //step 7: Create PMF table for each chosen Access Point
	    
	    // Set of trainingdata. Each trainingdata is associated to one access-point
	    ArrayList<TrainingData> tds = new ArrayList<TrainingData>();
	    
	    // Selected access-point names by the user
	    ArrayList<String> names = new ArrayList<String>();
	  // names.add("Conferentie-TUD_00_1b_90_76_d3_f6");
	   // names.add("eduroam_00_1b_90_76_d3_f0");
	    
	    names = chosen_ap_names;
	    
	    // new trainingdata
	    TrainingData td;
	    
	    // new access-point name to be associated with the training data
	    String name = null;
	    
	    
	      for(int i = 0; i < names.size(); i++) {
	           
	           name = names.get(i);
	           
	      		td = new TrainingData(name, filepath);
	      
	     	 	td.createPMFTable();
	      		td.createHistogramTable();
	      		
	      		tds.add(td);
	      }
	     
	      
	      // Show training data in a table
	      DataTable dt = new DataTable(tds);
	      dt.showTables();
//	      dt.showHistogramTable();
	    
	      
	      
	
		/* *********************************************
		 *  Phase 3: Apply Bayesian classification using the chosen Access points 
		 * ********************************************* */
	      int current_cell=0;
	      
	      //naive bayesian classifier
	      Bayesian naiveBayesian = new Bayesian(filepath);
	      
	      naiveBayesian.trainClassifier(tds);
	      
	     //fetch new testing data to classify
	      ArrayList<Integer> observations = new ArrayList<Integer>();  
	      observations = oberserveNewRssi(keyboard,tds);
	 
	      current_cell=   naiveBayesian.classifyObservation(observations);
	      
	      System.out.println("My location is Cell "+current_cell);
	     	 
		
	}
	
	/* 
	 * This function reads the new RSSI observation sample for only the training data 
	 * */
	public static ArrayList<Integer> oberserveNewRssi(Scanner keyboard, ArrayList<TrainingData>tds)
	{

			Scanner id_scanner=null;
			keyboard.useDelimiter("\\s*[,\n\r]\\s*");
		
			ArrayList<Integer> rssi_value=new ArrayList<Integer>();
			
			String data=null;
				
			System.out.println("\nFetching new rssi values");
			    //read input as long enter is not pressed with with no access-point name
			   //access-point captured  with pressing enter	    
			  

			   //number position of access-point in filtered access-point array list
			   //read each line from user input, each line is ended with pressing enter
			   //for each line read the number
			   //stop reading, when no id is given and enter is pressed
			
			System.out.println("Number of TrainingData:"+tds.size());
		
			for(int t=0; t<tds.size(); t++)
			   //while((data=keyboard.nextLine()).length() > 0)
			   {

				  //  System.out.print(tds.get(i)+":");
				//    data2=keyboard2.nextLine();
				  //  if(data2.length()>0)

				   System.out.print(""+tds.get(t).getName()+": ");
				   if((data=keyboard.nextLine()).length() > 0)
					{
					
					   id_scanner = new Scanner(data); //read new line
					
					 //fetch number for that line. 
					   
					   rssi_value.add(id_scanner.nextInt());  
					   System.out.println(" ");
					   
					}
					else 
						System.out.println("Sample missed");

					   id_scanner.close();
			   }
			  
			   //keyboard.close();
			

		      System.out.println("Sampling done");
	
		
			
		return rssi_value;
		
			
		
	}
	

	static public ArrayList<String>  getNewAccessPoints(Scanner keyboard, AccessPointRSSIStrength rssi_filter)
	{
		ArrayList<Integer> chosen_ap = new ArrayList<Integer>(); //save list of chosen access points
    	ArrayList<String> chosen_ap_names = new ArrayList<String>(); //save list of chosen access points
    	Scanner id_scanner = null;
        keyboard.useDelimiter("\\s*[,\n\r]\\s*");
	    String data=null;
		int data_id;
		int ap_number;
		   	
    	rssi_filter.display_normalAP();
    
		
		
		   
		
		   System.out.println("\n\n................User Interface Access-Points selection.................");
		   System.out.println("Choose some access points by id number");
		   System.out.println("Put id number then press Enter to put access-point");
		   System.out.println("Press enter with no id, to finalize");
		   System.out.println(".......................................................................\n\n\n");
		   
		   //number position of access-point in filtered access-point array list
		   //read each line from user input, each line is ended with pressing enter
		   //for each line read the number
		   //stop reading, when no id is given and enter is pressed
		   while((data=keyboard.nextLine()).length() > 0)
		   {
			   id_scanner = new Scanner(data); //read new line
			
			 //fetch number for that line. 
			   //substract 1 since ap are displayed from 1 until X, instead from 0-X.
			   ap_number = id_scanner.nextInt()-1;  
			   
			   chosen_ap.add(ap_number);
			   id_scanner.close();
		   }
		   
	//	   keyboard.close();

		    System.out.println("number access points chosen:" + chosen_ap.size());
		    System.out.println("Display of chosen access-points");
		    
		    
		    for(int j=0; j<chosen_ap.size(); j++)
		    {
		    	ap_number = chosen_ap.get(j);//get id name given by user
		    	data= rssi_filter.get_list_of_normalAP().get(ap_number); //get ap name belonging to that ap id position 
		    	chosen_ap_names.add(data);
		    	
		 //   	System.out.println("AP:"+ data);
		    }
		    
		    
		    for(int m=0; m<chosen_ap_names.size(); m++)
		    {
		    	System.out.println("AP array name: "+chosen_ap_names.get(m));
		    }
		    
		       

		
		return chosen_ap_names;
		
		
		
		
		
		
		
		
	}
	
	

}
