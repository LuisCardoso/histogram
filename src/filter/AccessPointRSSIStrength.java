package filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Iterator;




public class AccessPointRSSIStrength {
	private static float total_avg=(float)0;
	private static float total_std=(float)0;
	private static float threshold_tooStrong=(float)0;
	private static float threshold_tooWeak=(float)0;
    private static ArrayList<String> filtered_ap = new ArrayList<String>();
		
	
	
	
	
	public ArrayList<String> get_filteredAP()
	{
		return filtered_ap;
	}
	
	
	
	
	/*get average of an array */
	public static float get_mean(float [] data){
		float average=(float)0;
		float sum=(float)0;
		
		for (int i=0; i<data.length; i++)
		{
			sum += data[i];
		}
		
		average = sum/data.length;
		total_avg=average;
		return average;
		
	}
	
	
	 public static void	filter_rssi_too_strong_Tree(TreeMap<String,Float> data){
			
		 	float [] temp = new float[data.size()];	 	
		 	
		 	Set set = data.entrySet();
		 	Iterator i = (Iterator) set.iterator(); 	
		 	Map.Entry me;	
		  	Object[] objectArrayOfValues = data.values().toArray();
		 	
		 	for(int k=0; k<objectArrayOfValues.length; k++){ // !!! Does not need. Pass objectArrayOfValues to get_mean !!!
		 		temp[k]= (Float)objectArrayOfValues[k]; 
			 }		

		 	/* calculate the mean and std of all the key */
		 	total_avg = get_mean(temp);
		 	total_std = get_standardDeviation(temp,total_avg);  // !!! total_avg is global, no need to pass it to a function !!!
		 	threshold_tooStrong = total_avg+total_std;
			threshold_tooWeak = total_avg-total_std;
			   
		 	/* filter out which APs are within the std */
		    i = (Iterator) set.iterator(); 	
		 
		    
		 	while(i.hasNext())
		 	{
		 	    me =(Map.Entry)i.next();
		 	   if((Float)me.getValue() <threshold_tooStrong && (Float)me.getValue()>threshold_tooWeak )	
		 	    {
		 		  filtered_ap.add(String.valueOf(me.getKey())); 					
		 	    }
			    	   
		 	}
		 	
			 return;
				
		}

	
	
	
	
	
	/* get stadard divation of an array */
	static public float get_standardDeviation(float []data, float data_mean){
		float std=(float)0;
		double temp= (double)0;
	
	
		/* get variance */
		for(int i=0; i<data.length; i++)
		{
			temp+=Math.pow((data[i]-data_mean),2);
		}
		
		temp = Math.sqrt(temp/data.length);
		total_std = (float) temp;
				
	    std=(float)temp;
		
		return std;
	}

	
	
	
	

}
