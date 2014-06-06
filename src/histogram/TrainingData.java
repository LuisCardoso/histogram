package histogram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import table.Table;

public class TrainingData {
	// access-point name
	String access_point_name;
	String filepath;
	Table pmfTable;
	Table histogramTable;
	
//	private ArrayList<Table> tables = new ArrayList<Table>();
	
	
	/*
	 * Constructor
	 * @param name: access-point name
	 * @param filepath: path to the main directory
	 */
	public TrainingData(String name, String filepath) {
		this.access_point_name = name;
		this.filepath = filepath;
	}
	
	
	/*
	 * This function returns the name of this trainingdata
	 */
	public String getName() {
		return access_point_name;
	}
	
	
	/*
	 * This function returns the pmf table
	 */
	public Table getPMF() {
		return pmfTable;
	}
	
	
	/*
	 * This function returns the histogram table
	 */
	public Table getHistogram() {
		return histogramTable;
	}
	
	
	/*
	 * This function creates a pmf table for each access-point
	 */
	public void createPMFTable() {
		// Fetch the path
//		String path = Environment.getExternalStorageDirectory().toString()+"/Download/histogram/";
		
		String folder_name = "1_RawUnselected_AP/histogram/";
		
		String path = filepath+folder_name;
		
		// print on the screen
//		Log.d("Files", "Path: " + path);
		
		File dir = new File(path);        
		File dirs[] = dir.listFiles();
		
		File subdir = null;
		File file[] = null;
		
		// cell name
		String cell = "";
		String tokens[] = null;
		
		// table name
//		String accesspoint = "";
		
//		Table table = null;
		
//		Log.d("Files", "Size: "+ dirs.length);
		
		for (int i = 0; i < dirs.length; i++){
			
//		    Log.d("Dir", "DirName:" + dirs[i].getName());
		    
		    // subdirectoryprivate ArrayList<Table> tables = new ArrayList<Table>();
		    subdir = new File(dirs[i].getPath());
		    
		    // list of files in the subdirectory
		    file = subdir.listFiles();
//		    
//		    Log.d("Dir", "file[i].getPath():" + file[i].getPath());
//		    Log.d("Dir", "File[i].getName():" + file[i].getName());
//		    Log.d("Dir", "File[i].getParent():" + file[i].getParent());
//		    
//		    Log.d("File", "\t FileName:" +subdir.getName());
		    
		    // Iterate over each file
		    for(File f : file) {
//		    	Log.d("File", "\t FileName:" +f.getName());
		    	
		    	// retrieving the cell name (directory name) from the parent path
		    	// by removing (trimming) the path of the directory
		    	tokens = f.getParent().split("/"); // ????????????????????? path changed ???????????????????????????
		    	
		    	 cell = tokens[tokens.length-1].substring(1);
		    	 
		    	// retrieving the name of the access-point from the filename
//		    	accesspoint = f.getName().substring(0, f.getName().length()-4);
//		    	Log.d("Accesspoint", accesspoint);
		    	
		    	// Fetch the table with the cell name
//		    	table = getTable(accesspoint);
		    	
		    	// Creates a new table if it does not exist yet
		    	if(pmfTable == null) {
		    		pmfTable = new Table(access_point_name);
		    	}
		    	
		    	// Read file
		    	putPMFIntoTable(f, pmfTable, cell);
		    }
		}
	}
	
	
	/*
	 * Returns a table with the name cell
	 */
//	private Table getTable(String cell) {
//		Table table = null;
//		
//		for(int i = 0; i < tables.size(); i++) {
//			
//			if(tables.get(i).getName().equalsIgnoreCase(cell)) {
//				table = tables.get(i);
//				break;
//			}
//		}
//		
//		return table;
//	}


	/*
	 * This function converts the histogram to pmf table
	 */
	private void putPMFIntoTable(File f, Table table, String cell) {

//		Log.d("ReadFile", "Filename:"+f.getName());
		//Read text from file
		StringBuilder text = new StringBuilder();

		try {
		    BufferedReader br = new BufferedReader(new FileReader(f));
		    String line;
		    String [] tokens;

		    while ((line = br.readLine()) != null) {
//		        text.append(line);
//		        text.append('\n');
		    	tokens = line.split(",");
		    	
		    	
		    	
		    	table.setValue(
		    			Integer.parseInt(cell.substring(1))-1, 
		    			Math.abs(Integer.parseInt(tokens[0])), 
		    			Float.parseFloat(tokens[2]));
		    	
		    	table.printTable();
		    }
		    
//		    Log.d("ReadFile", text.toString());
		}
		catch (IOException e) {
		    System.out.println("Error: " +e.getMessage());
		}
	}
	
	
	/*
	 * This function creates a histogram for each access-point
	 */
	public void createHistogramTable() {
		// Fetch the path
//		String path = Environment.getExternalStorageDirectory().toString()+"/Download/histogram/";
		
		String folder_name = "1_RawUnselected_AP/histogram/";
		
		String path = filepath+folder_name;
		
		// print on the screen
//		Log.d("Files", "Path: " + path);
		
		File dir = new File(path);        
		File dirs[] = dir.listFiles();
		
		File subdir = null;
		File file[] = null;
		
		// cell name
		String cell = "";
		String tokens[] = null;
		
		// table name
//		String accesspoint = "";
		
//		Table table = null;
		
//		Log.d("Files", "Size: "+ dirs.length);
		
		for (int i = 0; i < dirs.length; i++){
			
//		    Log.d("Dir", "DirName:" + dirs[i].getName());
		    
		    // subdirectoryprivate ArrayList<Table> tables = new ArrayList<Table>();
		    subdir = new File(dirs[i].getPath());
		    
		    // list of files in the subdirectory
		    file = subdir.listFiles();
//		    
//		    Log.d("Dir", "file[i].getPath():" + file[i].getPath());
//		    Log.d("Dir", "File[i].getName():" + file[i].getName());
//		    Log.d("Dir", "File[i].getParent():" + file[i].getParent());
//		    
//		    Log.d("File", "\t FileName:" +subdir.getName());
		    
		    // Iterate over each file
		    for(File f : file) {
//		    	Log.d("File", "\t FileName:" +f.getName());
		    	
		    	// retrieving the cell name (directory name) from the parent path
		    	// by removing (trimming) the path of the directory
		    	tokens = f.getParent().split("/"); // ????????????????????? path changed ???????????????????????????
		    	
		    	 cell = tokens[tokens.length-1].substring(1);
		    	
		    	// retrieving the name of the access-point from the filename
//		    	accesspoint = f.getName().substring(0, f.getName().length()-4);
//		    	Log.d("Accesspoint", accesspoint);
		    	
		    	// Fetch the table with the cell name
//		    	table = getTable(accesspoint);
		    	
		    	// Creates a new table if it does not exist yet
		    	if(histogramTable == null) {
		    		histogramTable = new Table(access_point_name);
		    	}
		    	
		    	// Read file
		    	putHistogramIntoTable(f, histogramTable, cell);
		    }
		}
	}


	private void putHistogramIntoTable(File f, Table table,	String cell) {
		
		try {
		    BufferedReader br = new BufferedReader(new FileReader(f));
		    String line;
		    String [] tokens;

		    while ((line = br.readLine()) != null) {
//		        text.append(line);
//		        text.append('\n');
		    	tokens = line.split(",");
		    	
//		    	Log.d("Token0", tokens[0]);
//		    	Log.d("Token1", tokens[1]);
//		    	Log.d("Token2", tokens[2]);
		    	
//		    	Log.d("Cell", ""+Integer.parseInt(cell.substring(1)));
//		    	Log.d("Cell-1", ""+(Integer.parseInt(cell.substring(1))-1));
		    	
		    	table.setValue(
		    			Integer.parseInt(cell.substring(1))-1, 
		    			Math.abs(Integer.parseInt(tokens[0])), 
		    			Float.parseFloat(tokens[1]));
		    	
		    	table.printTable();
		    }
		    
//		    Log.d("ReadFile", text.toString());
		}
		catch (IOException e) {
		    System.out.println("Error: " +e.getMessage());
		}
	}
}
