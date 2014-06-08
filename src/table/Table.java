package table;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class Table {
	//name is equal to access point
	private String name;
	
	// pmf table: row (cell) ranges from 1 to 17 and column (rssi) ranges from 0 to 100
	private Float [][] table = new Float[17][101];
	
	
	/*
	 * Constructor
	 */
	public Table(String name) {
		this.name = name;
	}
	
	
	/*
	 * Returns the name of the table
	 */
	public String getName() {
		return name;
	}
	
	
	/*
	 * Set the pmf of this table at a given row (cell) and column (rssi)
	 */
	public void setValue(int cell, int rssi, float pmf) {
		this.table[cell][rssi] = new Float(pmf);
	}
	
	
	/*
	 * Returns the pmf of this table at a given row (cell) and column (rssi)
	 */
	public float getValue(int cell, int rssi) {
		return this.table[cell][rssi].floatValue();
	}
	
	
	/*
	 * Returns the table
	 */
	public Float[][] getTable() {
		return table;
	}
		
	
	/*
	 * This method prints the table
	 */
	public void printTable() {
	//	Log.d("TableName", name);
		System.out.println("TableName: "+ name);
		for(int i = 0; i < table.length; i++) {
			for(int j = 0; j < table[i].length; j++) {
				if((table[i][j] != null) && ((table[i][j]).floatValue() > 0)) {
	//				Log.d("TableValue", ""+"i:" +i+1+ " j:"+ j +" = "+ pmf[i][j]);
					System.out.println("TableValue [i:"+ (i+1)+ "]  " + "[j:"+ j +"] = "+ table[i][j]);
				}
			}
		}
	}
}

