package Localizer;

import histogram.TrainingData;

import java.util.ArrayList;

import table.Table;

//import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;


public class LaplaceBayesian extends Bayesian implements ClassifierAPI{

	/* 
	 * formula for update 
	 * 
	 * (xi + alpha)/(N + alpha*beta)
	 * For a given Access-Point
	 * N is the number of samples
	 * alpha the offset to add to each rssi value sample
	 * beta is the number of distinct rssi values. 101 in our example
	 * xi is the previous occurrence of the rssi value
	 * */
	 private static int alpha=1; 
	// private static int beta=101; 

    // Save a next set of training data after doing laplace correction
    public ArrayList<TrainingData> tds_laplace = new ArrayList<TrainingData>();
	
	
    /*
     * Constructor
     *  */	
	public LaplaceBayesian(String filepath) {
		super(filepath, "Laplace Bayesian");
		// TODO Auto-generated constructor stub
	
	
	}
	
	
	/* 
	 * Get Training Data for this unique Classifier
	 * */
	public ArrayList<TrainingData>  getPersonalTrainingData()
	{
		return tds_laplace;
	}
	

	
	/* @parameter 1: list of training data made from the chosen Access Points
	 * Train classifier, to know what PMF Table to use
	 * This function takes the old training data and corrects
	 *  */
	public void trainClassifier(ArrayList<TrainingData> trainingDataList){
		this.tds = trainingDataList; //save original training data
		
		String ap_name = null;
		Float [][] table_histogram;
		Float []cell_histogram;
		Float [] cell_pmf;
		
		//for each training data, update the cell distribution data
		for(int t=0; t<tds.size(); t++)
		{	
			ap_name=tds.get(t).getName();
			
			//get histogram  table from Training Data
			table_histogram=tds.get(t).getHistogram().getTable();
		
			//create a new training data 
			TrainingData td = new TrainingData(ap_name, filepath);
						
			//for each cell correct the Histogram and PMF Table
			for(int c=0; c<table_histogram.length; c++)      
			{
				cell_histogram = correctHistogram(table_histogram[c]); //update cell occurrences 
				cell_pmf = correctPMF(cell_histogram,getCellOccurrences(cell_histogram));	// update cell PMF			
				
				//save information in a Table for TrainingData			
				td.putHistogramArrayIntoTable(cell_histogram, c);
				td.putPMFArrayIntoTable(cell_pmf, c);
				
			}			
		
			//add training data to list of adjusted training data's 
			tds_laplace.add(td);
			
		}		
	}
	
	/* 
	 * Get Training Data for this unique Classifier
	 * */ 		    
	public ArrayList<TrainingData> getUpdatedTrainingData ()
	{
		return tds_laplace;
		
	}
		    	
   
	/*
	 * This function takes in the new observation sample, and returns the classification type. 
	 *   */

	public int classifyObservation(ArrayList<Integer> observations)
	{
	
		int bayesian_result = 0;
   
    	float [] classification_result = new float [2];  // return format is [0]= cellID, [1]: probability 
    	
    	float[] sense_results = new float [numberOfCells];         

  
    	int cellNumber;
       	int ap_index;
    	
	
		/*for each training data, and its corresponding observation, apply the sense model 
		 * So find the probability of being in Cell[i], and having that rssi value for that given AP, 
		 * Obtain an array with that probability for each cell
		*/
		
		for(int t=0; t<tds_laplace.size(); t++)
		{
			
			ap_index = NextStrongestAP(observations);
		
			System.out.println("AP name: "+tds_laplace.get(ap_index).getName() + "observation:"+observations.get(ap_index));
	
			//fetch the conditional probability of being in all cells and having that given rssi value for that given AP
			sense_results = senseOneAP(observations.get(ap_index), tds_laplace.get(ap_index).getPMF()); //P(e[i]=r|H)
			posterior = vector_mult(this.prior, sense_results);	
		
	/*		System.out.println("prior !! ");
			display_1D(this.prior);
			
			System.out.println("Sense Model !!");
			display_1D(sense_results);
			
			System.out.println("Posterior !!");
			display_1D(this.posterior);
	*/	
			System.arraycopy(this.posterior, 0, this.prior, 0, this.posterior.length); // update prior after 1 step.    
			
			classification_result=getMaxValueandClassify(posterior);
			
			cellNumber= (int)(classification_result[0] +1);
			
			//update end result only if classification had a valid cell id
			if( cellNumber >= 1)
			{
				bayesian_result = (int)(classification_result[0] +1);
			}
			
		 //   System.out.println("cellnumber:"+cellNumber);
		    ClassificationEstimations.add( (int)(classification_result[0] +1));
	     //	System.out.println("Cell:" + ClassificationEstimations.get(t)); 
		    System.out.println("Cell: "+ (classification_result[0]+1) + "Probability: "+classification_result[1] );
									
		}
				
	    return bayesian_result;
	
	
		
		
	}
	
	
		        
    public Float [] correctHistogram(Float[] histogram)
    {
    	Float [] temp = new Float [histogram.length];
	
    	System.arraycopy(histogram, 0, temp, 0, histogram.length);
   
    	
    	for(int i=0; i<histogram.length; i++)
    	{
    		if(temp[i] == null)
    		{
    			//temp[i]= (0f + alpha)/(N + alpha * beta);
    			temp[i]= 0f + alpha;
    		}
    		else{ 
    			//temp[i]= (temp[i]+ alpha)/(N + alpha * beta);
    			temp[i]= (temp[i]+ alpha);
    		}
    	}
    	
    	return temp;
    }
    
    
    /*This functions corrects the PMF for the Laplace TrainingData
     * @paramter1: Corrected histogram, which added one to each possible rssi, which we cover ( -100 ->0 )"
     * @parameter2: The updated occurrences with this correction*/
    private Float [] correctPMF(Float[]histogram_corrected, int occurrences)
    {
   	
    	Float [] temp = new Float[histogram_corrected.length];
    	
    	System.arraycopy(histogram_corrected, 0, temp, 0, histogram_corrected.length);
    	
    	for(int i=0; i<temp.length; i++)
    	{
    		if(temp[i] != null)
    			temp[i]= temp[i] / occurrences; 
    	}
    	
    	return temp;
    }
    
    
    
    	
	/* This function takes the PMF of a given access point, and adjust it according to the Laplace Filter*/
    public float [][] laplace_correction1(float [][]pmf)
    {
    	float p;   //p1 + p2 + ... + pn = 1
    	float u;   // (a + u*pi)/p  where i= # of distinct values of rssi. and u*pi=1
    	
    	float [][] temp = new float[18][100];


		    	
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
    
    private int getCellOccurrences(Float [] AP_CellDistribution)
    {
    	int count=0; 
    	
    	for(int i=0; i<AP_CellDistribution.length; i++)
    	{
    		if(AP_CellDistribution[i] != null)
    			count += AP_CellDistribution[i].intValue();
    	}
    	
    	return count;
    }
    
	
	
	
	/* This function takes the PMF of a given accesspoint, and adjust it according to the Laplace Filter*/
    public float [][] laplace_correction(float [][]pmf)
    {
    	float p;   //p1 + p2 + ... + pn = 1
    	float u;   // (a + u*pi)/p  where i= # of distinct values of rssi. and u*pi=1
    	
    	float [][] temp = new float[18][100];

		    	
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
    
   
	//puts cell[i] histogram or pmf and puts it in a Table
	public static void putArrayinTable(Float [] cellData, int cellId, Table table){
		
		//put the array element at the correct location in table
		//each array index corresponds to the correct rssi value for the table
		for(int i=0; i<cellData.length; i++)
		{
			table.setValue(cellId, i, cellData[i].floatValue());
		}
		
		
	}
	
	
	
	
	
	
}
