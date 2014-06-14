package filter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
	
	//list of access point filtered by their rssi strength. 
	//Too strong and too weak not accepted
    public static ArrayList<String> filtered_ap = new ArrayList<String>();
	
    //path to where the access points attained from filtering by occurrences (phase 1 filtering)
    private static String filepath="";
	
    public AccessPointRSSIStrength(String filepath)  {
       	//String folder_name="2_Filter/selection/";
    	//this.filepath= filepath+folder_name;
    	this.filepath=filepath;
    }
    
    
	
	/* This function fetched the file with the selected AP.
	 * Takes the names and average rssi  */
	public static TreeMap <String, Float> fetch_AP_and_RSSIavg()	{
	
		String AP_name;
		float average;
		TreeMap<String,Float> filedata = new TreeMap<String,Float>();
		
		String folder_name="2_Filter/selection/";
		
		//String readfile = filepath+"selectionAvg.txt";
		String readfile = filepath+folder_name+"selectionAvg.txt";
		
		
		try{
			File file = new File(readfile);
			
			Scanner reader = new Scanner(file);
			reader.useDelimiter("\\s*[,\n\r]\\s*");
			
			
			while(reader.hasNext())
			{
				AP_name = reader.next();
				average = reader.nextFloat();
			
				//System.out.println("APname: " + AP_name + " Average: " +average );	
				/*add data to a Treemap */
				filedata.put(AP_name, average);
				reader.nextLine();
				
			}
			
			
			reader.close();
		}	
		catch(Exception e){
			System.out.println(e.getMessage());
		}
			
		return filedata;
	}
	
		
	
	public static void save_filtered_AP( ){
		try {
			String filename = "selectionNormalRSSI.txt";
			String folder_name="2_Filter/selection/";
	    		
			//File file = new File(filepath+"/selection");
			File file = new File(filepath+folder_name);
			
			
			// If the directory does not exist, then make one
			if(!file.exists()) {
				if(file.mkdirs())
					System.out.println("Directory created!");
				else
					System.out.println("Failed to create directory!");
			}
			
			
			PrintWriter writer = new PrintWriter(file.getPath() +"/"+ filename);
			
			
			//Set <String> setOfKeys =  avg.keySet();
			//Iterator <String> iterator = setOfKeys.iterator();
			
			String access_point_name = "";
			
			for(int i=0; i<filtered_ap.size(); i++)
			{
				access_point_name = filtered_ap.get(i);
				// write the values to the file
				writer.append(
						access_point_name+ "\n"
						);

				
			}	
			writer.close();
			
		} // try
		catch(FileNotFoundException fnfe) {
			System.out.println("\n\nFile not found: \n\n"+ fnfe.getMessage());
		}
		catch(SecurityException se) {
			System.out.println("\n\nSecurity: \n\n"+ se.getMessage());
		}
		
		
		
	}
	
	
	
	
	 public static void	filter_rssi_too_strong_Tree(TreeMap<String,Float> data){
	
	 	float [] temp = new float[data.size()];	 	
	 	
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
	
	
	
	 public static void filter_rssi_too_strong(){
		
		float [] datatest = new float [6]; 
	    
		datatest[0]= (float)1; 
	    datatest[1]= (float)4; 
	    datatest[2]= (float)2; 
	    datatest[3]= (float)4; 
	    datatest[4]= (float)6; 
	    datatest[5]= (float)9; 
	    
	    
	    float std = get_standardDeviation(datatest, get_mean(datatest));
	    threshold_tooStrong = total_avg+total_std;
	    threshold_tooWeak = total_avg-total_std;
	   
	    
	    /* for all of the previous samples, spit out too strong or too week rssi */
	    for(int j=0; j<datatest.length; j++)
	    {
	    	/* keep only rssi that are within the standard diviation range*/
	    	if(datatest[j] <threshold_tooStrong && datatest[j]>threshold_tooWeak )
	    	{
	    		filtered_ap.add(String.valueOf(datatest[j]));
	    		
	    	}
	    }
	    
	}
	
	

	/*get average of an array */
	static public float get_mean(float [] data){
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


	
	public void display_normalAP()
	{
		System.out.println("filtered Access-Points");
		
		for(int i=0; i<filtered_ap.size(); i++)	
		{
			System.out.println("[" + (i+1) + "]: " +filtered_ap.get(i));//
		}
		
	}
	
	public ArrayList<String> get_list_of_normalAP()
	{
		return filtered_ap;
	}
	
	
	
	
	
	
	
	
	
	

}
