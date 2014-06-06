package Localizer;

import java.util.TreeMap;

public class Bayesian {
     
	static int numberOfCells= 17;
	static int numberOfClasses=17;
	static int numberOfObservations=4;
	static int RSSI_Range= 101; // 0 to -100
	
	static int callcount=0;
	
	/* TrainingData */
	/*static float [][] pmf_AP1 = new float [numberOfCells] [RSSI_Range];
	static float [][] pmf_AP2 = new float [numberOfCells] [RSSI_Range];
	static float [][] pmf_AP3 = new float [numberOfCells] [RSSI_Range];
	static float [][] pmf_AP4 = new float [numberOfCells] [RSSI_Range];
	*/
	public static TreeMap<Integer,Float> pmf_AP1 = new TreeMap<Integer,Float>();
	public static TreeMap<Integer,Float> pmf_AP2 = new TreeMap<Integer,Float>();
	public static TreeMap<Integer,Float> pmf_AP3 = new TreeMap<Integer,Float>();
	public static TreeMap<Integer,Float> pmf_AP4 = new TreeMap<Integer,Float>();
	
	
	static float [][] PM = new float [numberOfCells] [numberOfObservations];
	
	

	
	
   public Bayesian(TreeMap<Integer,Float>AP1, TreeMap<Integer,Float>AP2, TreeMap<Integer,Float>AP3, TreeMap<Integer,Float>AP4)	
   {
	 
   }
	
   
   	 /* receives the PMF of four Access Points */
      public static String bayesian(TreeMap<Integer,Float>AP1, TreeMap<Integer,Float>AP2, TreeMap<Integer,Float>AP3, TreeMap<Integer,Float>AP4 ){
    	 
	    	String bayesian_result=null;
	    	float [] temp = new float [2];
	    	
	    	float [] posterior = new float [numberOfCells];
	    	float[] prior =  new float [numberOfCells];
	    	float sense_results[]=new float [numberOfCells];
	    	
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
		
			
			/* sense model for a specific AccessPoint
			 * 
			 * input: fetch new observation, e[i] rssi value
			 *        Probability Model for a specific AP, Histogram of e[i]
			 * output: conditional probability of being in all cells and having the rssi for that particular AP rssi,  P(ei=... |H)
			 *  function layout: senseOneAP(AP[i] BSSID , AP[i] rssi value, AP[i] histogram )
			 * */
			
		//	sense_results = senseOneAP(rssi_observation, PM); //P(e[i]=r|H)
			
			rssi_observation=0;
	//		sense_results = senseOneAP(rssi_observation, Outlook_pmf); //P(e[i]=r|H)
			
			posterior = vector_mult(prior, sense_results);
			
		
			/* update prior after 1 step.    */
			System.arraycopy(posterior, 0, prior, 0, posterior.length);
		
			rssi_observation=2;
		//	sense_results = senseOneAP(rssi_observation, Temp_pmf); //P(e[i]=r|H)
			posterior = vector_mult(prior, sense_results);
			
			/* update prior after 1 step. */
			System.arraycopy(posterior, 0, prior, 0, posterior.length);
		
			
			
			rssi_observation=0;
			//sense_results = senseOneAP(rssi_observation, Hum_pmf); //P(e[i]=r|H)
			posterior = vector_mult(prior, sense_results);
			
			/* update prior after 2 step.    */
			System.arraycopy(posterior, 0, prior, 0, posterior.length);
		
			
			
			
			rssi_observation=1;
			//sense_results = senseOneAP(rssi_observation, Wind_pmf); //P(e[i]=r|H)
			posterior = vector_mult(prior, sense_results);
			
			
			
			/* update prior after 3 step.    */
			System.arraycopy(posterior, 0, prior, 0, posterior.length);
		
			
					
			/* calculate the posterior for one step.  
			 * P(H|e[i]= P(e[i]|H) * P(H) */

			/* print posterior after one step*/
	//		System.out.println("Posterior probability after step: "+ "1");
			
			for(int i=0; i<numberOfClasses; i++)
			{
				System.out.println("Posterior " +i + ": " + posterior[i] );
						
			}
			
			temp=getMaxValueandClassify(posterior);
			
			System.out.println("Classification: " +temp[0] + " -- value " + temp[1]  );
			
			/* update prior after 1 step.    */
	//		System.arraycopy(posterior, 0, prior, 0, posterior.length);
	
			
			/* get P(E|H)*/
			//perception = sense(prior,perception_model);
			
			
			/*calculate posterior */
		/*	for(int i=0; i<posterior.length; i++)
			{
				posterior[i] = perception[i]*prior[i]; 
			}
			*/
			/* update prior */
			//System.arraycopy(posterior, 0, prior, 0, posterior.length);
			
		    return bayesian_result;
    }

    
    /*
  * This functions receives measured rssi from a given accesspoint (ei), and the distribution of a specific AP over all cells
  * returns the P(ei|H), so the probability being in each cell, and having the given rssi for that specific AP
  * */
    
    public static float [] senseOneAP(float ap_observation, float [][] pmf_accesspoint)
    {
    	float [] sense_result= new float [numberOfCells]; //buffer to hold P(ei|H)
    	boolean rssi_found=false;
    	 
    	  	
    	/* find the column of the rssi value */
    	for(int r=0; r<pmf_accesspoint[0].length; r++)
    	{
    		/* rssi value in PM found*/
    		if(pmf_accesspoint[0][r] == ap_observation){
    			rssi_found = true;
    			
    			/* fetch the probability for each cell having that rssi value for that AP.  P(e1|H) */
    			for(int c=0; c<numberOfClasses;  c++)
    			{
    				sense_result[c]= pmf_accesspoint[c+1][r]; //for the PM skip first row, since its the label ID
    			
    			}
    			break;
    		} 
    		
    	}
    	if(rssi_found==false){
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
    	  
    
    

}
