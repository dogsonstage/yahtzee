/***
 * Created on 4/13/24 by @author Madison S.
 */
package main;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/***
 * GUI to display current game scorecard.
 */
class ScoreCardWindow {
	
	protected JTable table;
	protected JFrame frame;
	protected String[][] data;
	protected String[] columns;
	
	protected ScoreCardWindow() {
		
		frame = new JFrame();
		
		frame.setTitle("Score Card");
		
		String[][] tempData = { {"ONES", "", ""}, 
							{"TWOS", "", ""},
							{"THREES", "", ""},
							{"FOURS", "", ""},
							{"FIVES", "", ""},
							{"SIXES", "", ""},
							{"TOTAL SCORE", "", ""},
							{"BONUS", "", ""},
							{"", "", ""},
							{"3 OF A KIND", "", ""},
							{"4 OF A KIND", "", ""},
							{"FULL HOUSE", "", ""},
							{"SMALL STRAIGHT", "", ""},
							{"LARGE STRAIGHT", "", ""},
							{"CHANCE", "", ""},
							{"YAHTZEE", "", ""},
							{"YAHTZEE BONUS", "", ""},
							{"GRAND TOTAL", "", ""}};
		
		String[] tempColumns = {"", "PLAYER 1", "PLAYER 2"};
		
		//Place values in data and columns
		data = tempData.clone();
		columns = tempColumns.clone();

		
		
		//Make scorecard window
		table = new JTable(data, columns);
		table.setEnabled(false);
		JScrollPane sp=new JScrollPane(table);
		frame.add(sp);
		frame.setSize(350,350);
		frame.setVisible(true);
		
		//Make cols 1 and 2 center alignment
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
	}
}
