package Localizer;

import histogram.TrainingData;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import com.sun.corba.se.impl.oa.poa.AOMEntry;

import table.Table;

public class Bayesian {
	//String folder_name="3_Chosen_AP/2_PMF_AccessPoints_allCells/";
	String filepath="";
	
	int nextMaxInded;
	
	
    // Set of training data. Each training data is associated to one access-point
    public ArrayList<TrainingData> tds = new ArrayList<TrainingData>();
	
    //for a given sample, keep track of all of the estimation calculated during each new posterior
    public ArrayList<Integer> ClassificationEstimations = new ArrayList<Integer>();
    
    //keep track of all the classification results and correct answer, in order to calculate accuracy of this classifier
   //   ArrayList<String> listOfClassification = new ArrayList<String>();
  //  ArrayList<String> listOfCorrectLocalization = new ArrayList<String>();
    
    
    static int numberOfCells = 17;
	static int numberOfObservations = 4;
	

	//information about classifier accuracy
	public static float error_percentage;
	public static float accuracy_percentage;
	
	
	public float[] prior =  new float [numberOfCells];	
	public float [] posterior = new float [numberOfCells];

	
	static int callcount = 0;
	
	//List of the PMF of the training data
	//static ArrayList<Table> PMF_TrainingDataSet = new ArrayList<Table>();
	
	//static float [][] PM = new float [numberOfCells] [numberOfObservations];
   	public ArrayList<Float[][]> TrainingData_PMF = new ArrayList<Float[][]>();
	
	
	public Bayesian(String filepath)
	{
		this.filepath=filepath;
	
		 /* initialize buffers */
		for(int i=0; i<this.posterior.length; i++) this.posterior[i]=(float)0;
		for(int i=0; i<this.prior.length; i++) this.prior[i]=(float)0;
		
	}
	
	
	public float [] get_posterior()
	{
		return this.posterior;
	}
	
	public float [] get_prior()
	{
		return this.prior;
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
    	 
    	System.out.println("\n rssi sample:" + ap_observation);
    	
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
		
		//String [][] array = new String [17+1][100+1] ;
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
			//		array[r][c] = token;
					
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
	
	

    /* This function restarts the localization classification
     * so the prior and posterior set back to their initial believe.
     * The initial believe for this application is uniform*/
	public void setInitialBelieve()
	{
		/* set uniform distribution for prior */
		for(int i=0; i<prior.length; i++)
		{
			this.prior[i]= 	1/(float)(numberOfCells); //initial prior is uniform
			this.posterior[i]= 	1/(float)(numberOfCells); //initial prior is uniform
			
		}
	
		
		
		return;
	}
    
    
    
	/*
	  *@parameter 1 :observation corresponding to a given training data. 
	  *@parameter 2: Training data pmf 
	  *Functionality is to apply the sense model. So for a given observation it fetches the probabilty of being in each individual cell and having that rssi value.
	  * 
	  * */
	    public static float [] senseOneAP(Integer ap_observation, Table pmfTable)
    
	    //public static float [] senseOneAP2(Integer ap_observation, Float[][] td_pmfTable)
	    {
	    	float [] sense_result = new float [numberOfCells]; //buffer to hold P(ei|H)
	    		    	 
	    	System.out.println("\n rssi sample:" + ap_observation.intValue());
	  
	    	//check if rssi value is within rssi range
	    	if(ap_observation>0 || ap_observation<pmfTable.getTable().length )
	    	{
	   		
	    		/* fetch the probability for each cell having that rssi value for that AP.  P(e1|H) */
    			for(int c=0; c<numberOfCells;  c++)
    			{
    				//sense_result[c]= pmfTable.getValue(3, 3); 
        			
    				sense_result[c]= pmfTable.getValue(c, Math.abs(ap_observation.intValue())); 
    				System.out.println("sense result["+c+"] : "+sense_result[c]);
    	    			
    			}
    		
	    	}else{
	    		System.out.println("rssi not in range");
	    	}
	    	
	    	
	    	   
	    	return sense_result;
	    }
    
    
    
    /* @parameter 1: a list of rssi value for a given sample
     * Find the next strongest rssi value 
     * */
		public int findNextMaxRssi(ArrayList<Integer> observations2)
		{
			int max_rssi;
			Integer [] temp = new Integer [observations2.size()];
			
			observations2.toArray(temp);
			
			//sort array in ascending order
	        Arrays.sort(temp);
	       
	        //take last index and then continue taking that to the left
	        max_rssi=temp[temp.length -1-nextMaxInded++].intValue();
	        
			return max_rssi;
			
		}
		

		/* @parameter 1: a list of rssi value for a given sample
		 * find the next Access-Point with the highest rssi value
		 * */
       public int NextStrongestAP(ArrayList<Integer> observations2)
       {
    	   int max_rssi =0;
    	   int ap_index = 0;
    	   
    		//find the next strongest AP signal 
			max_rssi=findNextMaxRssi(observations2);
			
			//find index of the next highest rssi value
	        for(int i=0; i<observations2.size(); i++)
	        {
	        	if(observations2.get(i).intValue() == max_rssi)
	        	{
	        		ap_index=i;
	        	}
	        }
			
    	    return ap_index;
    	   
       }
		
		

}
