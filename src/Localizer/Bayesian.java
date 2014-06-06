package Localizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Bayesian {
     
	static int numberOfCells = 17;
	static int numberOfObservations = 4;
	static int RSSI_Range = 100;
	
	static boolean Laplace_Correction = false;
	static String classifier_type = "";
	
	static float error_percentage;
	static float accuracy;
	
	static int callcount = 0;
	static float [][][] TrainingData; // per access point hold their histogram. [Access point ID][Cell ID][RSSI value]

	//List of the PMF of the training data
	//static ArrayList<Table> PMF_TrainingDataSet = new ArrayList<Table>();
	
	
	//static float [][] PM = new float [numberOfCells] [numberOfObservations];
   	public ArrayList<Float[][]> TrainingData_PMF = new ArrayList<Float[][]>();
	
   	//training data to classify
	static public float [][]ap1_pmf;
	static public float [][]ap2_pmf ;
	static public float [][]ap3_pmf ;
	static public float [][]ap4_pmf ;
	
	
	/* Constructor which takes in the PMF distributions of the  four chosen Access Points  */
    public  Bayesian(float [][]ap1_pmf,float [][]ap2_pmf,float [][]ap3_pmf,float [][]ap4_pmf ){
   
    	 //2d array copies well
    	this.ap1_pmf=ap1_pmf;
        this.ap2_pmf=ap2_pmf;
        this.ap3_pmf=ap3_pmf;
        this.ap4_pmf=ap4_pmf;
    }
        
    
    public void create_trainingTable(float [][][] trainingdata){
    	
    	TrainingData = new float [numberOfObservations][numberOfCells][RSSI_Range];   	
    
    	//for each Access Point copy the 2d histogram data 
    	for(int a=0; a<TrainingData.length; a++)
    	{
    		TrainingData[a]=trainingdata[a];
    	}
    		
    
    }
    
    
    public void set_numberofAccesspoints(int Accesspoints_count)
    {
    	this.numberOfObservations= Accesspoints_count;
    }

    
    public  String bayesian_classify(boolean use_laplace){
    	String bayesian_result=null;
        
    	float [][]temp1_pmf;
    	float [][]temp2_pmf;
    	float [][]temp3_pmf;
    	float [][]temp4_pmf;
    	float [] classification_result = new float [2];
    	
    	float[] prior =  new float [numberOfCells];	
    	float [] posterior = new float [numberOfCells];
    	float[] sense_results = new float [numberOfCells];         
    	float [] observations = new float [numberOfObservations];
    
    	this.Laplace_Correction = use_laplace;
    	
    	
    	//array copies well
        temp1_pmf=ap1_pmf;
        temp2_pmf=ap2_pmf;
        temp3_pmf=ap3_pmf;
        temp4_pmf=ap4_pmf;
        
        
    	observations[0] = -74;
    	observations[1] = -73;
    	observations[2] = -83;
    	observations[3] = -72;

            	
    	//eduroam / confer / tudelft /  TUvistor
    	/*observations[0] = -73;
    	observations[1] = -69;
    	observations[2] = -78;
    	observations[3] = -72;

    	observations[0] = -75;
    	observations[1] = -76;
    	observations[2] = -83;
    	observations[3] = -76;

    	observations[0] = -60;
    	observations[1] = -67;
    	observations[2] = -56;
    	observations[3] = -37;

*/
        /* correct 0 conditional probability for a given cell and access point if
	    	 * Send all PMF to the Laplace filter
	    	 * */
	    	/*if(Laplace_Correction)
	    	{
	    		
	    		temp1_pmf=laplace_correction(ap1_pmf);
	    		System.out.println("temp array  1 height:"+temp1_pmf.length);
	    		System.out.println("temp array 1 width:"+temp1_pmf[0].length);
	    		
	    		
	    		//temp1_pmf=laplace_correction(ap3_pmf);
	    		
	    		ap1_pmf=laplace_correction(ap1_pmf);
		    	ap2_pmf=laplace_correction(ap2_pmf);
	    		ap3_pmf=laplace_correction(ap3_pmf);
	    		ap4_pmf=laplace_correction(ap4_pmf);
	          
	    		System.out.println("LAPLACE CORRECTION..............");
	    
	    	}
        */
	    	
	    	
	    	float rssi_observation = 0; //value observed for a given access point 
	    	
		    /* initialize buffers */
			for(int i=0; i<posterior.length; i++) posterior[i]=(float)0;
			for(int i=0; i<prior.length; i++) prior[i]=(float)0;
			for(int i=0; i<sense_results.length; i++) sense_results[i]=(float)0;
			
			/* set uniform distribution for prior */
			for(int i=0; i<prior.length; i++)
			{
				prior[i]= 	1/(float)(numberOfCells); //initial prior is uniform
			}
		
				
			//Probability Model for a specific AP, Histogram of e[i]
			//output: conditional probability of being in all cells and having the rssi for that particular AP rssi,  P(ei=... |H)
			//function layout: senseOneAP(AP[i] BSSID , AP[i] rssi value, AP[i] histogram )
			
	
			//rssi_observation=0;	
			sense_results = senseOneAP(observations[0], this.ap1_pmf); //P(e[i]=r|H)
			
			//do monitor correction if necessary, if permitted
		/*	if(Laplace_Correction)
			{
				
			}
		*/
			
			posterior = vector_mult(prior, sense_results);
			System.out.println("prior !! ");
			display_1D(prior);
			System.out.println("Sense Model !!");
			display_1D(sense_results);
			System.out.println("Posterior !!");
			display_1D(posterior);
			
			System.arraycopy(posterior, 0, prior, 0, posterior.length); // update prior after 1 step.    
		
			//	rssi_observation=2;
			sense_results = senseOneAP(observations[1], this.ap2_pmf); //P(e[i]=r|H)
			posterior = vector_mult(prior, sense_results);
			System.out.println("prior !! ");
			display_1D(prior);
			System.out.println("Sense Model !!");
			display_1D(sense_results);
			System.out.println("Posterior !!");
			display_1D(posterior);
			System.arraycopy(posterior, 0, prior, 0, posterior.length); //update prior after 2nd step
		
			
			//rssi_observation=0;
			sense_results = senseOneAP(observations[2], this.ap3_pmf); //P(e[i]=r|H)
			posterior = vector_mult(prior, sense_results);
			System.out.println("prior !! ");
			display_1D(prior);
			System.out.println("Sense Model !!");
			display_1D(sense_results);
			System.out.println("Posterior !!");
			display_1D(posterior);
			System.arraycopy(posterior, 0, prior, 0, posterior.length); //update prior after 3rd step
		
			
//			rssi_observation=1;
			sense_results = senseOneAP(observations[3], this.ap4_pmf); //P(e[i]=r|H)
			posterior = vector_mult(prior, sense_results);
			System.out.println("prior !! ");
			display_1D(prior);
			System.out.println("Sense Model !!");
			display_1D(sense_results);
			System.out.println("Posterior !!");
			display_1D(posterior);
			System.arraycopy(posterior, 0, prior, 0, posterior.length);//update prior after 4th step
		
			
			System.out.println(" ~~~~~~~~~~~~~Final Posterior ~~~~~~~~~~~~");
			display_1D(posterior);
					
			classification_result=getMaxValueandClassify(posterior);
			
			System.out.println("Classification cell: " + (classification_result[0]+1) + " -- value " + classification_result[1]  );
			System.out.println(" ~~~~~~~~~~~~~ ~~~~~~~~~~~~");	
			/* update prior after 1 step.    */
	//		System.arraycopy(posterior, 0, prior, 0, posterior.length);
	
			
			/* get P(E|H)*/
			//perception = sense(prior,perception_model);
			
			
			/* update prior */
			//System.arraycopy(posterior, 0, prior, 0, posterior.length);
			
		    return bayesian_result;
    }

    
    
    
    
    
    
    /*
  * This functions receives measured rssi from a given accesspoint (ei), and the distribution of a specific AP over all cells
  * returns the P(ei|H), so the probability being in each cell, and having the given rssi for that specific AP
  * 
  * */
    
    public static float [] senseOneAP(float ap_observation, float [][] pmf_accesspoint)
    {
    	float [] sense_result = new float [numberOfCells]; //buffer to hold P(ei|H)
    	boolean rssi_found = false;
    	 
    	System.out.println("rssi sample:" + ap_observation);
    	
    	/* find the column of the rssi value */
    	for(int r=0; r<pmf_accesspoint[0].length; r++)
    	{
    		/* rssi value in PM found*/
    		if(pmf_accesspoint[0][r] == ap_observation){
    			rssi_found = true;
    			
    			/* fetch the probability for each cell having that rssi value for that AP.  P(e1|H) */
    			for(int c=0; c<numberOfCells;  c++)
    			{
    				sense_result[c]= pmf_accesspoint[c+1][r]; //for the PM skip first row, since its the label ID
    			
    			}
    			break;
    		} 
    		
    	}
    	if(rssi_found == false){
    		System.out.println("Rssi value not found");
     		/* if rssi is not present. find the closest rssi value nearby*/		
    	}
   
    	return sense_result;
    }
 
     
    
    public static float [] vector_mult(float[] vectorA,  float[] vectorB){
    	float [] result = new float [numberOfCells];
    	
    	for(int i=0; i<vectorA.length; i++)
    	{
    		result[i] = vectorA[i] * vectorB[i];
    		
    	}
    	return result;
    	
    	
    	
    	
    }
    

    public static float [] getMaxValueandClassify(float[] numbers){  
    	  float maxValue = numbers[0];
    	  float cellid = 0;
    	  float []results = new float [2];
    	  
    	  for(int i=1;i < numbers.length;i++){  
    	    if(numbers[i] > maxValue){  
    	      maxValue = numbers[i];  
    	      cellid= (float)i;
    	    }  
    	  }  
    	  results [0]= cellid;
    	  results [1]= maxValue;
    	  
    	  return results;  
    	}  
    	  
    
    
    
    
    
    /* This function takes the PMF of a given accesspoint, and adjust it according to the Laplace Filter*/
    public float [][] laplace_correction(float [][]pmf)
    {
    	float p;   //p1 + p2 + ... + pn = 1
    	float u;   // (a + u*pi)/p  where i= # of distinct values of rssi. and u*pi=1
    	
    	float [][] temp = new float[18][100];

		// System.out.println("pmf array length:"+pmf[0].length);
		    	
    	// run through each cell distribution. skip first line since it holds the rssi values
    	for(int c=1; c<numberOfCells; c++)
    	{
    		//copy data from Cell [i] for further processing if and only 0 probability exist in cell distribution
    	   if( contains(pmf[c], 0)) 
    	   {
    		   
    		   
    		  u= pmf[0].length;
    		  p=1/u;
    		  
    		  
    		  //copy the rssi values in the first row of array. this is the label 
    		  System.arraycopy(pmf[0], 0, temp[0], 0, pmf[0].length);
    		  
    		  // update each element in cell's array
    		 for(int r=0; r<u; r++)
    		 {
    			 temp[c][r]= (pmf[c][r] + u*p)/ u;
    			//	temp[c][r]= 3;
    		 }
    		 
    		   
    	   }
    		
    	}

    	
    	
    	return temp;
    	
    }

    //this functions checks if the array contains at least one value v
    public static <T> boolean contains( final float[] array, final float v ) {
        for ( final float e : array )
            if ( e == v || v != 0f && v==e )
                return true;

        return false;
    }
    
    

    
    
    public void display_1D(float [] data)
    {
    	for(int i=0; i<data.length; i++)
    	{
    		System.out.println(" ["+i + "]: "+ data[i]);
    	}
    }
    
    
/*This function takes the path of the PMF file of a given access point,
 * and returns it as a 2D array */    
	static public float [][] fetch_pmf(String filename_path)
	{
		
		String [][] array = new String [17+1][100+1] ;
		float [][] array_float = new float [17+1][100] ;
		
	//	Table table = new Table("TestTable");
		int r=0,c=0;
		
		try{
			File file = new File(filename_path);
		
		
			Scanner lineScanner= new Scanner(file);
	
			int file_id= 1;
			
	//		Table PMF_AP = new Table("fileid " +file_id++);
			
			// The values are comma separated
			lineScanner.useDelimiter("\\s*[,\n\r]\\s*");
			
			
			// Iterate over each token
			while(lineScanner.hasNextLine()) {
				
				String line =lineScanner.nextLine();
				
				Scanner textScanner = new Scanner(line);
				textScanner.useDelimiter("\\s*[,\n\r]\\s*");
			
				
				while(textScanner.hasNext())
				{
				
					String token = textScanner.next();
					array[r][c] = token;
					
					if(c!=0)
					{
						array_float[r][c-1]= Float.valueOf(token).floatValue(); 
					}
					
					
					c++;
				}
				
				textScanner.close();
				c=0;
				r++;

			} 
			
			lineScanner.close();
			r=0;
			}
			catch(FileNotFoundException e){
				System.out.println(" File not found"+e.getMessage());
			}
			
			return array_float;	
	}
	
	

    
    
    
    
    
    
    
    
    
    
    

}
