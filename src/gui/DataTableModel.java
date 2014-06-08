package gui;

import javax.swing.table.AbstractTableModel;


/*
 * This class is required by DataTable.
 * This class configures DataTable.
 */
public class DataTableModel extends AbstractTableModel {

	/**
	 * This field is added automatically by the IDE
	 * Somehow the IDE gives an warning when this variable is missing 
	 */
	private static final long serialVersionUID = 1L;
	
	// The columnsNames are filled in, in the constructor
	// columnsNames are the columns of the table
	private String[] columnNames = new String[101]; //TODO
	
	// data to be shown
	private Object[][] data; //TODO
	
	
	/*
	 * Constructor
	 */
	public DataTableModel() {
		
		// Fill in the columnNames with the range from 0 to 100
		for(int i = 0; i <= columnNames.length; i++) {
			
			// The columnsNames are equal to the negative of index i
			// Except when index is 0 (zero), since 0 is neither positive nor negative  
			if(i != 0) {
				columnNames[i] = "-"+i;
			}
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 100;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return 17;
	}

	@Override
	public Object getValueAt(int row, int col) {
		// TODO Auto-generated method stub
		return data[row][col];
	}
}
