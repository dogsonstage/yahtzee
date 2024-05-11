/***
 * Created on 4/16/24mby @author Madison S.
 */
package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;

class GameWindow implements ActionListener{
	
	JFrame frame;
	JScrollPane outputScrollPane;
	JScrollPane scoreCardPane;
	JTable scoreCardTable;
	JPanel inOutPanel;
	JTextField input;
	JTextArea output;
	String inputString;
	
	
	
	protected GameWindow() {
		
		//Make frame
		frame = new JFrame("Test Game Window");
		frame.setLayout(new BorderLayout(10, 0));
		
		//Make panels
		inOutPanel = new JPanel();
		
		//Set up input and output
		input = new JTextField();
		output = new JTextArea();
		output.setEditable(false);
		outputScrollPane = new JScrollPane(output);
		input.addActionListener(this);
		inOutPanel.setLayout(new BorderLayout());
		inOutPanel.add(input, BorderLayout.SOUTH);
		inOutPanel.add(new JScrollPane(output), BorderLayout.CENTER);
		
		//Set up scoreCard table
		String[][] data = { {"ONES", "", ""}, 
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
		String[] cols = {"", "PLAYER 1", "PLAYER 2"};
		scoreCardTable = new JTable(data, cols);
		scoreCardTable.setEnabled(false);
		scoreCardTable.getTableHeader().setReorderingAllowed(false);
		//Make cols 1 and 2 center alignment
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		scoreCardTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		scoreCardTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
		
		//Set up scoreCard pane
		scoreCardPane = new JScrollPane(scoreCardTable);
		
		//Set up frame
		frame.add(scoreCardPane, BorderLayout.WEST);
		frame.add(inOutPanel, BorderLayout.CENTER);
		frame.setSize(1200, 360);
		frame.setVisible(true);
		//frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}



	@Override
	public void actionPerformed(ActionEvent e) {
		//Save input, add to output, reset input field
		JTextField source = (JTextField)e.getSource();
		inputString = source.getText();
        output.setText(output.getText() + inputString + "\n");
        source.setText("");
        
		
	}
	
}

class YahtzeeOutputStream extends OutputStream{
	
	private JTextArea textArea;
	
	protected YahtzeeOutputStream(JTextArea textArea) {
		this.textArea = textArea;
	}

	@Override
	public void write(int b) throws IOException {
		// redirects data to the text area
        textArea.append(String.valueOf((char)b));
        // scrolls the text area to the end of data
        textArea.setCaretPosition(textArea.getDocument().getLength());
		
	}
	
}
