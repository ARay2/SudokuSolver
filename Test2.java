import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
//import java.util.concurrent.TimeUnit;

import javax.swing.*;
import javax.swing.border.Border;

public class Test2 implements ActionListener {
	JFrame frame;
	JPanel sudoku, buttons, panel;
	JButton loadButton, solveButton, pauseButton, clearButton;
	Boolean flag = false, count = true;
	int counter =1;
	int[][] matrix = null;
    String[] backup = null;    
	static JTextField[][] textFields= new JTextField[9][9];
	
	public Test2() {
		frame = new JFrame("Sudoku Solver");

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel = new JPanel();
		sudoku = new JPanel();
		getSudoku();
		getButtons();
        panel.setLayout(new BorderLayout());
        panel.add(sudoku, BorderLayout.NORTH);
        panel.add(buttons, BorderLayout.SOUTH);
        frame.add(panel);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.pack();
	}
	void getButtons() {
		buttons = new JPanel();
		buttons.setAlignmentX(Component.BOTTOM_ALIGNMENT);
		loadButton = new JButton("Load");
        loadButton.addActionListener(this);
        buttons.add(loadButton);
        solveButton = new JButton("Solve");
        solveButton.addActionListener(this);
        buttons.add(solveButton);
        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(this);
        buttons.add(pauseButton);
        clearButton = new JButton("Clear");
        clearButton.addActionListener(this);
        buttons.add(clearButton);
	}
	public void actionPerformed(ActionEvent e)
    {
		//using swingWorker for backgrounding thread
        //this prevents GUI from lockup.
		Thread th1 = new Thread(new Runnable() {
			@Override
            public void run() {
				solve(0,0,matrix);                 

            }
        }); 
        if( e.getSource() == loadButton )
        {
            flag = true;
            setCells(parseProblem(backup), flag);
        }
		
            	
        if( e.getSource() == solveButton)
        {
            flag = false;
            matrix = parseProblem(backup);
            th1.start();
            if (solve(0,0,matrix)) // solves in place
			{
				System.out.println("Solved");
			}
			else
				System.out.println("NONE");     	
        }
        if( e.getSource() == clearButton )
    	{
        	th1.suspend();;
    		matrix = null;
    		setCells(matrix, flag);
    	}
        if( e.getSource() == pauseButton )
        {
        	if(counter%2!=0)
        		th1.stop();
        	else
        	{
        		th1.start();
        		solve(0,0,matrix);
        	}
        }
    }
	void getSudoku() {
		sudoku.setLayout(new BorderLayout());
		sudoku.setLayout(new GridLayout(9, 9));
		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
				textFields[r][c] = new JTextField(2);
				Font f = new Font("Dialog", Font.PLAIN, 18);
				textFields[r][c].setFont(f);
				textFields[r][c].setHorizontalAlignment(JTextField.CENTER);
				sudoku.add(textFields[r][c], BorderLayout.NORTH);
			}
		}
	}
	public static void main(String[] args) throws IOException {
		Test2 a = new Test2();

		File inFile = new File(args[0]);
    	//System.out.println(filename);
		String[] argument = null;
		try {
			BufferedReader in = new BufferedReader(new FileReader(inFile));
			StringBuilder sb = new StringBuilder();
			String line = in.readLine();
			while(line != null) {
				for(int i = 0; i<9; i++)
				{
					if(Character.isDigit(line.charAt(i)))
						sb.append(line.charAt(i));
					else
						sb.append("0");
				}
			    line = in.readLine();
			    //System.out.println("Reading from file");
			}
			in.close();
			//System.out.println(sb);
			argument = sb.toString().split("");
			a.backup=argument;
			//System.out.println(argument[1]);
		} catch (FileNotFoundException e) {
			System.out.println("File in catch block");
			e.printStackTrace();
		}
        a.matrix = a.parseProblem(argument);
    }

	boolean solve(int i, int j, int[][] cells) {
		if(cells!=null)
		{
			if (i == 9) {
				i = 0;
				if (++j == 9)
					return true;
			}
			if (cells[i][j] != 0)  // skip filled cells
				return solve(i+1,j,cells);

			for (int val = 1; val <= 9; ++val) {
				if (legal(i,j,val,cells)) {
					cells[i][j] = val;
					textFields[i][j].setText(String.valueOf(val));
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (solve(i+1,j,cells))
						return true;
				}
			}
			cells[i][j] = 0; // reset on backtrack
			return false;
		} 
		else
		{
			System.out.println("Waiting");
			return false;
		}
	}

    boolean legal(int i, int j, int val, int[][] cells) {
        for (int k = 0; k < 9; ++k) 
        {
            if (val == cells[k][j])
                return false;
            if (val == cells[i][k])
                return false;
        }

        int relevanBoxRow = (i / 3)*3;
        int relevanBoxCol = (j / 3)*3;
        for (int k = 0; k < 3; ++k) // box
            for (int m = 0; m < 3; ++m)
                if (val == cells[relevanBoxRow+k][relevanBoxCol+m])
                    return false;
        
        return true; // no violations, so it's legal
    }

    int[][] parseProblem(String[] args) {
        int[][] problem = new int[9][9]; // default 0 vals
        int n = 0; //counter for number of values in file
        for (int i =0; i<9; i++)//row number iteration
        {
        	for (int j = 0; j<9; j++)//column number iteration
        	{
        		int val = Integer.parseInt(args[n]);
        		problem[i][j] = val;
                n++;
                //System.out.print("System is here");
        	}
        }
        return problem;
    }
    void setCells(int[][] matrix, Boolean flag) {
    	if(matrix!=null)
    	{
    		for (int r = 0; r < 9; r++) {
    			for (int c = 0; c < 9; c++) {
    				if ((c == 2|| c == 5)&& count == true)
					{
						Border oldBorder = textFields[r][c].getBorder();
						Border setBorder = BorderFactory.createMatteBorder(0, 0, 0, 2, Color.BLACK);
						Border newBorder = BorderFactory.createCompoundBorder(setBorder, oldBorder);
						textFields[r][c].setBorder(newBorder);
					}
    				if ((r == 2|| r == 5)&& count == true)
					{
						Border oldBorder = textFields[r][c].getBorder();
						Border setBorder = BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK);
						Border newBorder = BorderFactory.createCompoundBorder(setBorder, oldBorder);
						textFields[r][c].setBorder(newBorder);
					}
    				if (matrix[r][c]!=0)
    				{
    					textFields[r][c].setText(String.valueOf(matrix[r][c]));
    					if (flag)
    					{
    						textFields[r][c].setFont(textFields[r][c].getFont().deriveFont(Font.BOLD));
    						textFields[r][c].setBackground(new Color(210, 210, 210));
    						textFields[r][c].setEditable(false);
    					}
    					else
    						textFields[r][c].setEditable(false);
    				}
    				else
    				{
    					textFields[r][c].setText("");
    					textFields[r][c].setEditable(true);
    				}
    			}
    		}
    		count = false;
    	}
    	else
    	{
    		for (int r = 0; r < 9; r++) {
    			for (int c = 0; c < 9; c++) {
    				textFields[r][c].setText("");
    			}
    		}
    	}
    }
}