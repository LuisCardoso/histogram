package table;


public class Table {
	//name is equal to access point
	private String name;
	
	// pmf table: row (cell) ranges from 1 to 17 and column (rssi) ranges from 0 to 100
	private float [][] pmf = new float[17][100];
	
	
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
	public void setPMF(int cell, int rssi, float pmf) {
		this.pmf[cell][rssi] = pmf;
	}
	
	
	/*
	 * Returns the pmf of this table at a given row (cell) and column (rssi)
	 */
	public float getPMF(int cell, int rssi) {
		return this.pmf[cell][rssi];
	}
	
	
	/*
	 * This method prints the table
	 */
	public void printTable() {
	//	Log.d("TableName", name);
		System.out.println("TableName"+ name);
		for(int i = 0; i < pmf.length; i++) {
			for(int j = 0; j < pmf[i].length; j++) {
				if(pmf[i][j] > 0) {
	//				Log.d("TableValue", ""+"i:" +i+1+ " j:"+ j +" = "+ pmf[i][j]);
					System.out.println("TableValue"+ ""+"i:" +i+1+ " j:"+ j +" = "+ pmf[i][j]);
				}
			}
		}
	}
}

