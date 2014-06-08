package gui;

import histogram.TrainingData;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import table.Table;

/*
 * This class shows the training data in a table.
 */

public class DataTable {
	
	ArrayList<TrainingData> trainingData = new ArrayList<TrainingData>();	
	private Float[][] pmfTable;
	private Float[][] histogramTable;
	private String[] columnNames = new String[101];
	
	
	/*
	 * Constructor
	 * 
	 * @param tds: training data to be shown in a table
	 */
	public DataTable(ArrayList<TrainingData> tds) {
		trainingData = tds;
		

		// Fill in the columnNames with the range from 0 to 100
		for(int i = 0; i < columnNames.length; i++) {

			// The columnsNames are equal to the negative of index i
			// Except when index is 0 (zero), since 0 is neither positive nor negative  
			if(i != 0) {
				columnNames[i] = "-"+i;
			}
			else {
				columnNames[i] = ""+i;
			}
		}
	}
	
	
	/*
	 * This function shows the pmf data in a table
	 */
	public void showTables() {
		Table pmfTable = null;
		Table histogramTable = null;
		String tableName = null;
		
		for(int i = 0; i < trainingData.size(); i++) {
			
			// fetch table name, which equals access-point name
			tableName = trainingData.get(i).getName();
			
			// fetch pmf table
			pmfTable = trainingData.get(i).getPMF();
			
			// fetch histogram table
			histogramTable = trainingData.get(i).getHistogram();
			
			// assign the tables to the 
			this.pmfTable = pmfTable.getTable();
			this.histogramTable = histogramTable.getTable();
			
			createTables(tableName);
		}
	}
	
	
	/*
	 * This function shows the histogram data in a table
	 */
	private void createHistogramTable(String tableName) {
		JTable table = new JTable(histogramTable, columnNames);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setPreferredScrollableViewportSize(new Dimension(700, 300));
		table.setFillsViewportHeight(true);
		
		// Put table in scrollpane, so that becomes scrollable 
		JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		JFrame frame = new JFrame("Histogram: "+tableName);
		frame.setLayout(new BorderLayout());
		frame.add(scrollPane, BorderLayout.CENTER);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}


	/*
	 * This function shows the pmf data in a table
	 */
	private void createPMFTable(String tableName) {
		JTable table = new JTable(pmfTable, columnNames);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setPreferredScrollableViewportSize(new Dimension(700, 300));
		table.setFillsViewportHeight(true);
		
		// Put table in scrollpane, so that becomes scrollable 
		JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		
		JFrame frame = new JFrame("PMF: "+tableName);
		frame.setLayout(new BorderLayout());
		frame.add(scrollPane, BorderLayout.CENTER);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	
	/*
	 * This method creates both tables pmf table as well as histogram table
	 */
	private void createTables(String tableName) {
		createPMFTable(tableName);
		createHistogramTable(tableName);
	}


	/*
	 * This function shows the histogram data in a table
	 */
	public void showHistogramTable() {
		
	}
}
