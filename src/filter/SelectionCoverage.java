/* *
 *Description: This class is responsible for fetching the list of Access-Points and selecting those which has a certain percentage of coverage.
 *The coverage percentage is user defined at run time
 */

package filter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class SelectionCoverage {

	private int numOfCells;
	private double coverage_percentage; 
	private String filepath;
	private int CellsRequired;
	private ArrayList<String> selectedAccessPoints = new ArrayList<String>();
	
	
	//default is 52%
	public SelectionCoverage(){
		this.coverage_percentage=52; 
		this.numOfCells=17;
	}
	
	public SelectionCoverage(int numOfCells, int coverage_percentage, String filepath){
		this.numOfCells=numOfCells;
		this.coverage_percentage=coverage_percentage;
		this.filepath=filepath;
		
	}
	
	/* 
	 * Determine which Access-Points cover the amount of cells,
	 * specified by the coverage percentage.
	 * */
	public void generateSelection(){
			//paths to fetch the occurrences 
		String folder_name= "1_RawUnselected_AP/occurrence/";
		//String folder_name="2_Filter/selection/";
		//String readfile = filepath+folder_name+"selectionAvg.txt";
		String filename="occurrence.txt";
		
		//fetch file with occurrences 
		
		
		try{
			File file = new File(filepath+folder_name+filename);
			//File file = new File(readfile);
			
			String AP_name=null;
			int occurrence=0;
			int required_occurrence=0;
			
			Scanner reader = new Scanner(file);
			reader.useDelimiter("\\s*[,\n\r]\\s*");
			
			//read each line word for word. each line has 2 tokens
			
			//find out how much cells at least need to be covered
			required_occurrence = getNumCellsRequirement();
			
			while(reader.hasNext())
			{
				AP_name = reader.next();
				occurrence = reader.nextInt();
				
				//capture the Access-Points which pass the coverage
				if(occurrence >= required_occurrence)
				{
					selectedAccessPoints.add(AP_name);
				}
				reader.nextLine();
				
			}
			
			
			reader.close();
		}	
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		
	}
	
	/* 
	 * Save the Access-points which passed the coverage filter
	 * */
	public void writeSelectionToFile(){
		
		String folder_name="2_Filter/selection/";
		String filename="selectionCvg.txt";
		//String filename="selection.txt";
		
		try{
			
		
			File file = new File(filepath+folder_name);
			
			if(!file.exists()){
				if(file.mkdirs())
					System.out.println("Directory created!");
				else
					System.out.println("Failed to create directory!");
			}
			
			
			PrintWriter writer = new PrintWriter(file.getPath() +"/"+ filename);
	
			for(int i=0; i<selectedAccessPoints.size(); i++)
			{
				writer.println(selectedAccessPoints.get(i));
			}
			writer.close();
			
		}catch(FileNotFoundException fnfe) {
			System.out.println("\n\nFile not found: \n\n"+ fnfe.getMessage());
		}
		catch(SecurityException se) {
			System.out.println("\n\nSecurity: \n\n"+ se.getMessage());
		}
	}
	
	
	
	
	/* 
	 * Return the coverage percetage
	 * */
	public double getCoveragePercentage(){
		return this.coverage_percentage/100;
	}
	
	/* 
	 * Return the number of cells that must be covered by a given Access-Point
	 * */
	public int getNumCellsRequirement(){
		int cellsRequired=0;
	
		//always round up when decimals number appear
		cellsRequired=(int)Math.ceil((this.coverage_percentage * this.numOfCells )/100 );
		
		
		return cellsRequired;
	}
	
	
	
}
