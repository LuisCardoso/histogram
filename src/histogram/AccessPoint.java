package histogram;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class AccessPoint {
	private int sample_id = 0;
	private String ssid = "";
	private String bssid = "";
	
	// rssi value is always <= 0
	private int level = 1;
	private int frequency = -1;
	private String capabilities = "";
	private int describeContents = -1;
	
	// Total amount of counted level (rssi) values
	private int total_level_frequency = 0;
	
	private TreeMap<Integer, Integer> level_frequency = new TreeMap<Integer, Integer>();
//	private TreeMap<Integer, Float> level_pdf = new TreeMap<Integer, Float>();
	
	
	public AccessPoint(String ssid, String bssid, int level) {
		this.ssid = ssid;
		this.bssid = bssid;
		this.level = level;
		
		increaseFrequency(level);
	}
	
	
	public AccessPoint(int sample_id, String ssid, String bssid, int level, int frequency, 
			String capabilities, int describeContents) {
		this.sample_id = sample_id;
		this.ssid = ssid;
		this.bssid = bssid;
		this.level = level;
		this.frequency = frequency;
		this.capabilities = capabilities;
		this.describeContents = describeContents;
	}
	
	
	/*
	 * This function returns the SSID of this accesspoint
	 */
	public String getSSID() {
		return ssid;
	}
	
	
	/*
	 * This function returns the BSSID of this accesspoint
	 */
	public String getBSSID() {
		return bssid;
	}
	
	
	/*
	 * This function returns the <key, value> pairs in an array
	 * The key is place on even indexes whilst the value is placed on the odd indexes
	 */
	public int[] getLevelFrequencyAsArray() {
		
		Set <Integer> set = level_frequency.keySet();
		
		Iterator <Integer> iterator = set.iterator();
		
		int [] freq = new int[set.size()*2];
		int i = 0;
		
		while(iterator.hasNext()) {
			freq[i] = iterator.next(); // save level (rssi)
			freq[i+1] = level_frequency.get(freq[i]).intValue(); // save corresponding frequency of occurrence
			
			i += 2;
		}
				
		return freq;
	}
	
	
	/*
	 * This method increases the number of occurrences of a given level (rssi value)
	 */
	public void increaseFrequency(int level) {
		
		// First check if the level, which is the key of the treemap, already exists in the treemap
		if(level_frequency.containsKey(Integer.valueOf(level))) {
			
			// If the level already exists in the treemap, we then get its corresponding value
			Integer v = level_frequency.get(Integer.valueOf(level));
			
			// Since the value is returned as a object, we must first transform it to a primitive value,
			// such that it can be manipulated with operators such as addition, subtraction, etc...
			int i = v.intValue();
			
			// Then, we increase this value by one
			i++;
			
						
			// At last, we want to save this new value back in the treemap with its corresponding level, i.e. key
			// Since, the function put() only accepts objects and not primitive values we must transform the key and the value to 
			// their object representation with Integer.valueof()
			level_frequency.put(Integer.valueOf(level), Integer.valueOf(i));
		}
		else {
			// Else if the given level does not exist yet in the treemap.
			// Then a new entry is created with the given level as the key an a 
			// corresponding frequency value of 1. (Since it is the first time that this level appears)
			level_frequency.put(Integer.valueOf(level), Integer.valueOf(1));
		}
		
		// The total number of occurred levels is increase by one
		total_level_frequency++;
	}
	
	
	/*
	 * This function returns the total amount of all occurred levels (rssi)
	 */
	public int getTotalLevelFrequency() {
		return total_level_frequency;
	}
}
