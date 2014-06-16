package main;

import filter.AccessPointOccurrence;
import filter.AccessPointRSSIStrength;
import filter.SelectionAverage;
import filter.SelectionCoverage;
import gui.DataTable;
import histogram.AccessPoint;
import histogram.Histogram;
import histogram.TrainingData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

import table.Table;
import Localizer.Bayesian;
import Localizer.LaplaceBayesian;
import Localizer.NaiveBayesian;
import Localizer.ProbabilisticBayesian;

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
	    
		//filter data by rssi strength
		// all rssi strength that are outside the std deviation are removed
		rssi_filter.filter_rssi_too_strong_Tree(Data_filterphase1);
			
	    
	   // rssi_filter.display_normalAP();
	    
	    // write to file the AP that remained after filtering
	    rssi_filter.save_filtered_AP();
	    
	    

	    //step 6: Choose X amount of Access points as TrainingData
	    Scanner keyboard = new Scanner(System.in);
	    ArrayList<String> chosen_ap_names = new ArrayList<String>();
	  
	    chosen_ap_names = getNewAccessPoints(keyboard,rssi_filter);
	    
		    
	    //step 7: Create PMF table for each chosen Access Point
	    
	    // Set of training data. Each training data is associated to one access-point
	    ArrayList<TrainingData> tds = new ArrayList<TrainingData>();
	    
	    // new training data
	    TrainingData td;
	    
	    // new access-point name to be associated with the training data
	    String name = null;
	    
	    

	    // create training data for each AP

	      for(int i = 0; i < chosen_ap_names.size(); i++) {
	           
	           name = chosen_ap_names.get(i);
	           
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
	      int current_cell = 0;
	      int current_cell2 = 0;
	      int current_cell3 = 0;
		     
	      
	      //Naive Bayesian classifier
	      NaiveBayesian naiveBayesian = new NaiveBayesian(filepath); //create classifier 
	      naiveBayesian.trainClassifier(tds); //train classifier 	   
	      naiveBayesian.setInitialBelieve();    //set the initial believe to uniform
	      
	  
	      /* Laplace classifier */
		  LaplaceBayesian laplaceClassifier = new LaplaceBayesian(filepath);
		  laplaceClassifier.trainClassifier(tds); //train classifier by updating training data. correction done automatically 
		  laplaceClassifier.setInitialBelieve();
		      
		  
		  /* Probablistic classifier */
		  ProbabilisticBayesian probabilisticClassifier = new ProbabilisticBayesian(filepath);
		  probabilisticClassifier.trainClassifier(tds); //train classifier by updating training data. correction done automatically 
		  probabilisticClassifier.setInitialBelieve();
		  
		  
		  
	      //fetch new testing data to classify
	      ArrayList<Integer> observations = new ArrayList<Integer>();  
	      
	      
	      observations = oberserveNewRssi(keyboard,tds);  
	     
	      /* apply classification */
	      current_cell=   naiveBayesian.classifyObservation(observations); 
	      current_cell2 = laplaceClassifier.classifyObservation(observations);
	      current_cell3 = probabilisticClassifier.classifyObservation(observations) ; 
	      
	      
	      
	      System.out.println("Results");
	      System.out.println(" Determinisitc Bayesian => Cell: " + current_cell + "\n Laplace Cell:"+ current_cell2 + "\n Probablistic Bayesian Cell:"+ current_cell3);
	 
	      System.out.println("Classifer Distribution");
	      
	      ArrayList<Object> classiffierList = new  ArrayList<Object>(); 
	      classiffierList.add(naiveBayesian);
	      classiffierList.add(laplaceClassifier);
	      classiffierList.add(probabilisticClassifier);
	      
	      saveTableToFile(naiveBayesian.tds, filepath,naiveBayesian,laplaceClassifier,probabilisticClassifier);
	      
	      
	}
	
	
	public static void printClassiferDistribution(Table classiferPMF){
		
		for(int i=classiferPMF.getTable().length-1; i>=0; i-- )
		{
			
	//		System.out.println("["+i:"]: "+ classiferPMF.getTable());
		}
		
	}
	
	
	
	/*
	 * This writes the generated histogram to a file
	 */
	public static void saveTableToFile( ArrayList<TrainingData>tds, String base_path,  NaiveBayesian naiveBayesian,  LaplaceBayesian laplaceClassifier,   ProbabilisticBayesian probablisticClassifier ) {
		try {
			PrintWriter writer = null;
			File file = null;
			File basefile = new File(base_path);
			String filename = null;
			
			
			String format = " %1$2s , %2$10s , %3$30s , %4$30s";
			String someLine;
			String rssi_index;
	
			/*folder hierarchy 
			 * 
			 *  Classification/Results/
			 *  Cell folders 
			 *  file with AP name
			 *  inside file each classifier probability against the rssi values
			 *  */
			
			for(int c=0; c<17; c++)
			{
				// Create new directory for each cell
				file = new File(
						basefile.getParent()+
						"/Results/Classification/c"+(c+1)+"/");
				
				// If the directory does not exist, then make one
				if(!file.exists()) {
					if(file.mkdirs())
						System.out.println("Directory created!");
					else
						System.out.println("Failed to create directory!");
				}
			
			
				//create a new file for each AP
				for(int t=0; t<tds.size(); t++)
				{
					filename = tds.get(t).getName();
					
					// Creates a new file in its corresponding directory
							writer = new PrintWriter(
										file.getPath()+
										"/"+filename+
										".txt"
										);
						
					
			        //put the header in the file 
			     	writer.append("rssi: \t");	
			     	
			
					writer.append(
							naiveBayesian.getClassiferName()+", \t" +
							laplaceClassifier.getClassiferName()+",\t" +
							probablisticClassifier.getClassiferName()+"," );
							
			
					writer.append("\n");
							
					
					
				for(int r=laplaceClassifier.tds.get(0).getPMF().getTable()[0].length-1; r>=0; r--)
				//	for(int r=99; r>=0; r--)
					{
		/*				writer.append(
							Float.toString(naiveBayesian.tds.get(t).getPMF().getValue(c,r))+": \t\t" +
							Float.toString(laplaceClassifier.getPersonalTrainingData().get(t).getPMF().getValue(c,r))+": \t\t" +
							Float.toString(probablisticClassifier.getPersonalTrainingData().get(t).getPMF().getValue(c,r))+": \n" 
						);*/
					 rssi_index= "["+r+"]: ";
					 someLine = String.format(format, rssi_index, naiveBayesian.tds.get(t).getPMF().getValue(c,r), laplaceClassifier.getPersonalTrainingData().get(t).getPMF().getValue(c,r), probablisticClassifier.getPersonalTrainingData().get(t).getPMF().getValue(c,r));
					   writer.append(someLine + "\n");
					
							
					
					
					
								
					}
					
				writer.close();
				
				}//end of training data loops
				
				
		
			
				
			
			
			
			
			
			
			}//end of cell loop
			
			
				
		
			
		}
		catch(FileNotFoundException fnfe) {
			System.out.println("\n\nFile not found: \n\n"+ fnfe.getMessage());
		}
		catch(SecurityException se) {
			System.out.println("\n\nSecurity: \n\n"+ se.getMessage());
		}	
	}
	
	
	
	
	
	/* 
	 * This function reads the new RSSI observation sample for only the training data 
	 * */
	public static ArrayList<Integer> oberserveNewRssi(Scanner keyboard, ArrayList<TrainingData>tds)
	{
		
		// ?????????????????? This function does nothing with the argument 'tds' besides printing its size and the corresponding name ??????????
		// ???????????? This function returns the index number of AP chosen by the user. And not rssi values of the corresponding AP ???????
		

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
					   
					   
					 //??????? id_scanner.nextInt() is not a rssi_value but the index of chosen AP, which was inserted by the user ???????
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
	
	/*
	 *Get A list of new chosen Access-Points 
	 *Selected by User via console.
	 *  */
	static public ArrayList<String>  getNewAccessPoints(Scanner keyboard, AccessPointRSSIStrength rssi_filter)
	{
		ArrayList<Integer> chosen_ap = new ArrayList<Integer>(); //save list of chosen access points
    	ArrayList<String> chosen_ap_names = new ArrayList<String>(); //save list of chosen access points names
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

		    System.out.println("number access points chosen:" + chosen_ap.size());
		    System.out.println("Display of chosen access-points");
		    
		    
		    for(int j=0; j<chosen_ap.size(); j++)
		    {
		    	ap_number = chosen_ap.get(j);//get id name given by user
		    	data= rssi_filter.get_list_of_normalAP().get(ap_number); //get ap name belonging to that ap id position 
		    	chosen_ap_names.add(data);
		    	
		 //   	System.out.println("AP:"+ data);
		    }
		    
		    
		    // Print on the screen the chosen APs
		    for(int m=0; m<chosen_ap_names.size(); m++)
		    {
		    	System.out.println("AP array name: "+chosen_ap_names.get(m));
		    }
		    
		
		return chosen_ap_names;
	
		
	}
	
	

}
