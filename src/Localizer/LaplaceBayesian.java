package Localizer;

import histogram.TrainingData;

import java.util.ArrayList;

import table.Table;

public class LaplaceBayesian extends Bayesian{

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
	 private static int beta=101; 
	//static private int numberOfSamples; //N
	
	
	 // Save a next set of training data after doing laplace correction
    ArrayList<TrainingData> tds_laplace = new ArrayList<TrainingData>();
	
	
	
	public LaplaceBayesian(String filepath) {
		super(filepath);
		// TODO Auto-generated constructor stub
	
	
	}

	
	/*Train classifier, to know what PMF Table to use */
	public void trainClassifier(ArrayList<TrainingData> trainingData){
		this.tds=trainingData;
		this.tds_laplace=trainingData;
		
		//apply Laplace correction to the training data
		
	String ap_name=null;
	int cellID;
	Float [][] table_histogram;
	Float []cell_histogram;
	Float [] cell_pmf;
	
	//for each training data, update the cell distribution data
	for(int t=0; t<tds.size(); t++)
	{
		
		ap_name=tds.get(t).getName();
		
		//get histogram  table
		table_histogram=tds.get(t).getHistogram().getTable();
	
		//for each cell correct the Histogram and PMF Table
		for(int c=0; c<table_histogram.length; c++)
		{
			cell_histogram=correctHistogram(table_histogram[c]);
			cell_pmf=correctPMF(table_histogram[c],getCellOccurrences(cell_histogram));
			
			
			
		}
		
		
		
	}
		
		
		
	}
	
	 		    
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
	    	
    
		    
 
	
	/* This function takes the PMF of a given accesspoint, and adjust it according to the Laplace Filter*/
    /*public Float [][] laplace_correction(Float [][]histogram)
    {
    	
    	Float [][] temp = new Float[histogram.length][histogram[0].length];
    	int NumberOfSamples;
    	
    	Float [][] laplaceHistogramTable = new Float [histogram.length][histogram[0].length];
    	Float [][] laplacePMFTable = new Float [histogram.length][histogram[0].length];
    	
		// System.out.println("pmf array length:"+pmf[0].length);
		    	
    	// run through each cell distribution. and adjust the histogram according to the laplace
    	for(int c=0; c<temp.length; c++)
    	{
    		//get cell number of samples, for given AP
    		NumberOfSamples=getCellOccurrences(temp[c]);
    		
    		
    		//update histogram 
    		
    		//update pmf

    		
    		
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
    
    */
    
    static public Float [] correctHistogram(Float[]histogram)
    {
    	Float [] temp = new Float [histogram.length];
    	int N=0; //current occurences
    	
    	
    	System.arraycopy(histogram, 0, temp, 0, histogram.length);
    	
    	//get current occurrences 
    	N= getCellOccurrences(temp);
    	
    	
    	for(int i=0; i<histogram.length; i++)
    	{
    		
    		temp[i]= (temp[i]+ alpha)/(N + alpha * beta);
    	}
    	
    	return temp;
    }
    
    

    static private Float [] correctPMF(Float[]pmf, int occurrences)
    {
    	//Float [] temp = new Float [pmf.length];
    	
    	for(int i=0; i<pmf.length; i++)
    	{
    		pmf[i]= pmf[i] / occurrences; 
    	}
    	
    	return pmf;
    }
    
    
    
    
	
	/* This function takes the PMF of a given accesspoint, and adjust it according to the Laplace Filter*/
    public float [][] laplace_correction1(float [][]pmf)
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
    
    private static int getCellOccurrences(Float [] AP_CellDistribution)
    {
    	int count=0; 
    	
    	for(int i=0; i<AP_CellDistribution.length; i++)
    	{
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
    
   
	//puts cell[i] histogram or pmf and puts it in a Table
	public static void putArrayinTable(Float [] cellData, int cellId, Table table){
		
		//put the array element at the correct location in table
		//each array index corresponds to the correct rssi value for the table
		for(int i=0; i<cellData.length; i++)
		{
			table.setValue(cellId, i, cellData[i]);
		}
		
		
	}
	
	
	
	
	
	
}
