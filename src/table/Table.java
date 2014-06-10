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
		
		//initialize table. necessary to prevent error when doing  floatValue, and a null is present
	/*	for(int i=0; i <table.length; i++)
		{
			for(int t=0; t<table[0].length; t++)
				table[i][t]=0f;
		}
	*/
		
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
	 * if not using initializing of the 2D array at the constructor, return 0 for values which are null
	 * uncomment the method which is desired, and comment the other
	 */
	public float getValue(int cell, int rssi) {
		if(table[cell][rssi] ==null)
		{
			return 0;
		}
		else 
		{
			return this.table[cell][rssi].floatValue();
		}
		
	
	}
	
	public Float getValue2(int cell, int rssi) {
		return this.table[cell][rssi];
	
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

