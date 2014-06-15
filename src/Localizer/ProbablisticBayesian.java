package Localizer;

import histogram.TrainingData;

import java.util.ArrayList;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Occurs;

public class ProbablisticBayesian extends Bayesian implements ClassifierAPI{

	//keep track of the statistics of each cell
	private ArrayList<Float> mean_list;
	private ArrayList<Float> std_list;
	private ArrayList<Float> variance_list;
	private ArrayList<Integer> count_list;
	
	
	//Save a next set of training data after doing Gaussian Distribution correction
	public ArrayList<TrainingData> tds_PBayesian = new ArrayList<TrainingData>();
	
	
	
	/* Constructor, 
	 * save the base path to all of the data */
	public ProbablisticBayesian(String filepath){
		super(filepath);
		
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
						
			//for each cell correct the PMF Table
			for(int c=0; c<table_histogram.length; c++)      
			{
			
				
			
				
				cell_pmf = correctPMF(table_histogram[c],getCellOccurrences(table_histogram[c]));	// update cell PMF			
			
				if(c==0){
					System.out.println("Cell id:" + (c+1) );
					for(int i=0; i<cell_pmf.length; i++)
					{
					//	System.out.println("rssi: " + i + "  probability: "+ cell_pmf[i]);
					}
				}
				
				//save information in a Table for TrainingData			
			//		td.putHistogramArrayIntoTable(table_histogram[c], c); //save the original histogram
			//		td.putPMFArrayIntoTable(cell_pmf, c);
					
			}			
		
			//add training data to list of adjusted training data's 
			tds_PBayesian.add(td);
			
		}		
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
		
		for(int t=0; t<tds_PBayesian.size(); t++)
		{
			
			ap_index = NextStrongestAP(observations);
		
			System.out.println("AP name: "+tds_PBayesian.get(ap_index).getName() + "observation:"+observations.get(ap_index));
	
			//fetch the conditional probability of being in all cells and having that given rssi value for that given AP
			sense_results = senseOneAP(observations.get(ap_index), tds_PBayesian.get(ap_index).getPMF()); //P(e[i]=r|H)
			posterior = vector_mult(this.prior, sense_results);	
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
	
	
		
	/* 
	* For a given cell count the amount of samples they are. 
	* rssi values with 0 occurrences is counted as 1 sample, while 
	* rssi values with x occurrences are counted as x samples.
	* */
	private int getCellOccurrences(Float [] AP_CellDistribution)
	{
		int count=0; 
		
		for(int i=0; i<AP_CellDistribution.length; i++)
		{
			//add 1 to count when array has null, and dont read else error will occur
			if(AP_CellDistribution[i] == null)
			{
				count ++;
			}
			else{
				count += AP_CellDistribution[i].intValue(); //add to count the amount of times rssi value occurs
			}
			
		}
		
		return count;
	}
	
	
	/*
	* Return the list of training data for the Bayesian
	* */
	public ArrayList<TrainingData> getUpdatedTrainingData ()
	{
	return tds_PBayesian;
	
	}
		
	
	/* 
	 * Correct the PMF table according to a normal gaussian distribution
	 * */
	private Float [] correctPMF(Float[]histogram_corrected, int occurrences)
	{
		
		Float [] temp = new Float[histogram_corrected.length];
		float mean = 0;
		float std = 0;
		float [] probResult = new float [histogram_corrected.length];
		
		
		System.arraycopy(histogram_corrected, 0, temp, 0, histogram_corrected.length);
		
		//calculate mean 
		mean = calculateMean(temp, occurrences);
		System.out.println("Mean: " + mean );
		
		
		//calculate std 
		std = calculateSTD( temp, mean, occurrences );
		System.out.println("STD: " + std );
		
		//Correct probability per rssi value
		
		
		for(int i=0; i<temp.length; i++)
		{
			
			probResult[i]=getProbabilityNormalDistribution( (-i), mean, std);
		//	temp[i] = getProbabilityNormalDistribution(temp[i].intValue(), mean, std); //unnecessary to have histogram as float
				
		//	if(temp[i] != null)
			//	temp[i]= temp[i] / occurrences; 
		}
		
		for(int i=probResult.length-1; i >=0; i--)
    	{
    		System.out.println( probResult[i]);
    	}

		
		
		
		return temp;
	}
	
	
	
	
	
	/*
	 * Calculate the mean from a set data
	 * formula (1/N) * sigma(x[i])
	 * Where n is the number of samples including counting rssi value with 0 occurrence as a count
	 * */
	public float calculateMean(Float [] cellDistribution, int occurrences){
		float mean =0; 
		float sum=0;
		float rssi_sum =0; //total sum of given rssi, including occurrences. 
		
		for(int i=0; i<cellDistribution.length; i++)
		{
			
			if(cellDistribution[i] != null){
				// add the sum of that rssi value, with the amount of times appeard
				//correct also for the fact rssi values are negative, but we took absolute value for index id
				rssi_sum = (-i) * cellDistribution[i]; 
				sum  += rssi_sum;
			}
		}
		
		mean = sum/ occurrences ;
		
		return mean;
	}
	
	
	/* 
	 * get the probability of a having a certain rssi value,
	 * under the assumption of having a gaussian distribution with u=mean and sigma=std
	 * */
	public float getProbabilityNormalDistribution(int rssi, float mean, float std)
	{
		double gaussian_probability =0;
		double temp1, temp2;
		
		temp1 = (1/(std*Math.sqrt(2*Math.PI) ));
		temp2 = Math.pow(Math.E , - Math.pow(( rssi-mean ),2) / (2*Math.pow(std,2)) ) ;
		gaussian_probability= temp1 * temp2; 
		
		
		//gaussian_probability=((1/(std*Math.sqrt(2*Math.PI) )) * Math.pow(Math.E , - Math.pow(( rssi-mean ),2) / (2*Math.pow(std,2)) );
		
		
		return (float)gaussian_probability;
		
	}
	
	
	/*
	 * Calculate the standard deviation from a set data
	 * */
	public float calculateSTD(Float [] cellDistribution, float mean, int occurrences){
		float std=0;
		float variance=0;
		
	    variance = calculateVariance(cellDistribution, mean, occurrences);
		
		std = (float) Math.sqrt((double)variance);
	    
	    
		return std;
	}
	
	
	/*
	 * Calculate the variance from a set data
	 * */
	public float calculateVariance(Float [] dataset,float mean, int occurrences){
		float variance=0; //(1/N)*  sigma [(xi-u)^2 ]
	
		for(int i=0; i<dataset.length; i++)
		{
			
			variance += Math.pow( (-i) - (double)(mean), 2);  
			
		/*	if(dataset[i] == null)
			{
				variance += Math.pow( 0 - (double)(mean), 2);
	
			}
			else 
			{
				variance += Math.pow( dataset[i].doubleValue() - (double)(mean), 2);
	
			}
	*/
		}	
		variance = variance/ occurrences;
			
		return variance;
	}
	
	
	/*
	 * Return the mean of a set data
	 * */
	public float getMean(){
		return 0f;
	}
	
	/*
	 * Return the standard deviation of a set data
	 * */
	public float getSTD(){
		return 0f;
	}
	
	
		public int calculateCount(){
			
			return 0;
		}
	



}


