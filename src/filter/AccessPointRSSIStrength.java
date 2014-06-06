package filter;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;




public class AccessPointRSSIStrength {
	private static float total_avg=(float)0;
	private static float total_std=(float)0;
	private static float threshold_tooStrong=(float)0;
	private static float threshold_tooWeak=(float)0;
    private static ArrayList<String> filtered_ap = new ArrayList<String>();
    private static String filepath="";
	
   
    /**
     *Constructor  
     */
    public AccessPointRSSIStrength(String filepath)
    {
    	this.filepath=filepath; //path of the file to read
    }
    
    
  
   /**
   * This function returns the Access Points that are within the standard diviation
   * */  
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
		 	String selected = filepath+"/selection/selection.txt";
		 	
		 	try{
		 		File file = new File(selected);
		 		Scanner reader = new Scanner(file);
		 		
		 		reader.useDelimiter("\\s*[,\n\r]\\s*");
		 		
		 		String access_point="";
		 		
		 		while(reader.hasNext()){
		 			
		 			
		 		}
		 		
		 		
		 		
			 	Set set = data.entrySet();
			 	Iterator i = (Iterator) set.iterator(); 	
			 	Map.Entry me;	
			  	Object[] objectArrayOfValues = data.values().toArray();
			 	
			 	for(int k=0; k<objectArrayOfValues.length; k++){
			 		temp[k]= (Float)objectArrayOfValues[k];
				 }		
	
			 	/* calculate the mean and std of all the key */
			 	total_avg = get_mean(temp);
			 	total_std = get_standardDeviation(temp,total_avg);
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
		 	catch(Exception e){
		 		System.out.println("\n\nFilter Phase2\n\n"+e.getMessage());
		 	}
			 
			 
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
