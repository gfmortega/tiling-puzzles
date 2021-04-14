import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.TreeSet;
public class MainFrame extends JFrame
{
	public int width;
	private int height;
	private int grid_size;
	
	private Tile grid_information[][];
	private MyComponent GridPainter;
	private MouseControl mouseController;
	private MoveControl moveController;
	
	private int currX;
	private int currY;
	private int currD;
	
	private int successfulClicks;
	private int missingSquares;

	private JButton undoButton;
	private JButton redoButton;
	private JButton clearButton;
	private JButton newGameButton;
	private JButton chessToggleButton;
	private JButton cycleToggleButton;

	private Cycle cycle;
	
	public MainFrame()
	{
		JOptionPane.showMessageDialog(null,"Welcome to Domino Tiling!  The goal of this game is to tile the given board, using only dominos.\nLeft-click to place a domino on the highlighted cells.\nRight-click or Ctrl+click to rotate the direction of your domino.\nHave fun!");
		GridPainter = new MyComponent();
		mouseController = new MouseControl(this);
		moveController = new MoveControl();
		
		this.add(GridPainter);
		setUpButtons();

		if(!newGame())
			return;
		
		this.getContentPane().setPreferredSize(new Dimension(width,height));
		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	private boolean gameIsWon()
	{
		return missingSquares + 2*successfulClicks == grid_size*grid_size;
	}
	private void setUpButtons()
	{
		undoButton = new JButton("Undo");
		redoButton = new JButton("Redo");
		clearButton = new JButton("Clear");
		newGameButton = new JButton("New Game");
		chessToggleButton = new JButton("Chess Pattern");
		cycleToggleButton = new JButton("Domino Cycle");
		
		ActionListener undoListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				if(moveController.undoStack.empty()||gameIsWon())
					return;
				undoMove(moveController.undoMove());
			}
		};
		undoButton.addActionListener(undoListener);
		
		ActionListener redoListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				if(moveController.redoStack.empty()||gameIsWon())
					return;
				redoMove(moveController.redoMove());
			}
		};
		redoButton.addActionListener(redoListener);
		
		ActionListener clearListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				if(gameIsWon())
					return;
				if(JOptionPane.showConfirmDialog(null,"Are you sure you want to clear the board?")==JOptionPane.YES_OPTION)
				{
					while(!moveController.undoStack.empty())
						undoMove(moveController.undoMove());
					moveController.clearMoves();
				}
			}
		};
		clearButton.addActionListener(clearListener);
		
		ActionListener newGameListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				if(JOptionPane.showConfirmDialog(null,"Are you sure you want to start a new game?")==JOptionPane.YES_OPTION)
					newGame();
				repaint();
			}
		};
		newGameButton.addActionListener(newGameListener);

		ActionListener chessToggleListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				chessToggle();
				repaint();
			}
		};
		chessToggleButton.addActionListener(chessToggleListener);

		ActionListener cycleToggleListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				cycleToggle();
				repaint();
			}
		};
		cycleToggleButton.addActionListener(cycleToggleListener);
		
		Container buttonPane = new Container();
		buttonPane.setLayout(new GridLayout(3,2));
		buttonPane.add(undoButton);
		buttonPane.add(redoButton);
		buttonPane.add(clearButton);
		buttonPane.add(newGameButton);
		buttonPane.add(chessToggleButton);
		buttonPane.add(cycleToggleButton);

		this.getContentPane().add(buttonPane,BorderLayout.SOUTH);
	}
	private void chessToggle()
	{
		for(int i = 0; i < grid_size; i++)
			for(int j = 0; j < grid_size; j++)
				grid_information[i+2][j+2].chessToggle();
	}
	private void undoMove(Move m)
	{
		grid_information[m.x][m.y].unfill();
		grid_information[m.x+rX(m.d)][m.y+rY(m.d)].unfill();
		successfulClicks--;
		repaint();
	}
	private void redoMove(Move m)
	{
		int temp = currD;
		currD = m.d;
		clickTile(m.x,m.y,true);
		currD = temp;
		repaint();
	}
	private boolean newGame()
	{
		/***
			This function is way too bloated;
			refactor into more functions sometime
		***/
		Object[] options = {"Normal Chessboard",
                    "One Square Missing",
                    "Adjacent Corners Missing",
                    "Opposite Corners Missing",
                	"Two Squares Missing (Different Colors)",
                	"Two Squares Missing (Any)",
                	"Six Squares Missing (Special)",
                	"Many Squares Missing (Equal Black and White)"};

        JComboBox optionList = new JComboBox(options);
                optionList.setSelectedIndex(0);

		String s = (String)JOptionPane.showInputDialog(
			this,
		    "What type of chessboard do you want to tile?",
		    "Select Screen",
		    JOptionPane.PLAIN_MESSAGE,
		    null,
		    options,
		    -1);
		if(s==null)
			return false;
		
		grid_size = 8;
		GameData.tileSize = GameData.preferredSize/grid_size + (GameData.preferredSize%grid_size==0 ? 0 : 1);
		width = grid_size*GameData.tileSize;
		height = grid_size*GameData.tileSize + 75;
				
		grid_information = new Tile[grid_size+4][grid_size+4];
		for(int i = 0; i <= grid_size+3; i++)
			for(int j = 0; j <= grid_size+3; j++)
				grid_information[i][j] = new Tile(i,j);
		for(int i = 1; i <= grid_size+1; i++)
		{
			grid_information[i][1].taint();
			grid_information[i][grid_size+2].taint();
		}
		for(int j = 1; j <= grid_size+1; j++)
		{
			grid_information[1][j].taint();
			grid_information[grid_size+2][j].taint();
		}


		ArrayList<Tuple> tainted = new ArrayList<Tuple>();

		Random rand = new Random();
		if(s.equals(options[0]))
		{
			missingSquares = 0;
		}
		else if(s.equals(options[1]))
		{
			missingSquares = 1;
			tainted.add(randomTuple(rand));
		}
		else if(s.equals(options[2]))
		{
			missingSquares = 2;
			tainted.add(new Tuple(grid_size-1,grid_size-1));
			tainted.add(new Tuple(0,grid_size-1));
		}
		else if(s.equals(options[3]))
		{
			missingSquares = 2;
			tainted.add(new Tuple(0,0));
			tainted.add(new Tuple(grid_size-1,grid_size-1));
		}
		else if(s.equals(options[4]) || s.equals(options[5]))
		{
			missingSquares = 2;
			Tuple a = randomTuple(rand);
			Tuple b = randomTuple(rand);
			while(a.equals(b) || (s.equals(options[4]) && a.color()==b.color()))
				b = randomTuple(rand);
			tainted.add(a);
			tainted.add(b);
		}
		else if(s.equals(options[6]))
		{
			missingSquares = 6;
			tainted.add(new Tuple(3, 7));
			tainted.add(new Tuple(4, 7));
			tainted.add(new Tuple(4, 6));
			tainted.add(new Tuple(5, 6));
			tainted.add(new Tuple(7, 4));
			tainted.add(new Tuple(7, 3));
		}
		else
		{
			missingSquares = 2*(rand.nextInt(4)+2);
			int col = 0;
			while(tainted.size() < missingSquares)
			{
				Tuple a = randomTuple(rand);
				if(!tainted.contains(a) && a.color()==col)
				{
					tainted.add(a);
					col ^= 1;
				}
			}
		}

		for(int i = 0; i < tainted.size(); i++)
			taint(tainted.get(i));
		
		clearHover();
		moveController.clearMoves();
		
		successfulClicks = 0;
		this.setTitle(s);
		
		GameData.shuffleColors();
		// for(int i = 0; i < grid_size; i++)
		// 	grid_information[i+2][0+2].fillIn(1+i);
		// repaint();

		cycle = new Cycle(GameData.tileSize, tainted);

		return true;
	}
	private Tuple randomTuple(Random rand)
	{
		int seed_x = rand.nextInt(grid_size);
		int seed_y = rand.nextInt(grid_size);
		return new Tuple(seed_x, seed_y);
	}
	private void taint(Tuple t)
	{
		grid_information[t.i+2][t.j+2].taint();
	}
	public void clearHover()
	{
		/*
		switch(currD)
		{
			case 0:
				currX = 0;
				currY = grid_size+1;
				break;
			case 1:
				currX = 0;
				currY = 0;
				break;
			case 2:
				currX = grid_size+1;
				currY = 0;
				break;
			case 3:
				currX = grid_size+1;
				currY = grid_size+1;
				break;
		}
		*/
		currX = 1;
		currY = 1;
	}
	public int getX(int x)
	{
		return x/GameData.tileSize+1+1;
	}
	public int getY(int y)
	{
		return y/GameData.tileSize+1+1;
	}
	public void cycleToggle()
	{
		cycle.toggleVisible();
	}
	public int rX(int d)
	{
		if(d==0||d==2)
			return 0;
		if(d==1)
			return 1;
		if(d==3)
			return -1;
		return 0;
	}
	public int rY(int d)
	{
		if(d==1||d==3)
			return 0;
		if(d==0)
			return -1;
		if(d==2)
			return 1;
		return 0;
	}
	public void rotate()
	{
		toggleHighlights();
		currD = (currD+1)%4;
		toggleHighlights();
		repaint();
	}
	public void clickTile(int i, int j, boolean virtual)
	{
		if(gameIsWon())
			return;
		//System.out.println(i+" "+j);
		if(grid_information[i][j].isFilled || grid_information[i+rX(currD)][j+rY(currD)].isFilled)
			return;
			
		int colorID = 1;
		for(int a = 0; a < 2; a++)
		{
			int ii = 0, jj = 0;
			if(a==0){ ii = i; jj = j; }
			else if(a==1){ ii = i + rX(currD); jj = j + rY(currD); }
			
			for(int d = 0; d <= 3; d++)
			{
				if(grid_information[ii+rX(d)][jj+rY(d)].getID()==colorID)
				{
					// wow this is so dumb
					// why did i insist on mexing this way
					// well whatever, im too lazy to improve it
					colorID++;
					a = -1;
					break;
				}
			}
		}
		grid_information[i][j].fillIn(colorID);
		grid_information[i+rX(currD)][j+rY(currD)].fillIn(colorID);
		
		successfulClicks++;
		if(!virtual)
			moveController.performMove(new Move(i,j,currD));
		repaint();
		
		if(gameIsWon())
		{
			this.setTitle("Successfully domino-tiled");
			toggleHighlights();
			clearHover();
			JOptionPane.showMessageDialog(null,"Congratulations, you have domino-tiled this board!");
		}
	}
	public void toggleHighlights()
	{
		grid_information[currX][currY].toggleHighlight();
		grid_information[currX+rX(currD)][currY+rY(currD)].toggleHighlight();
	}
	public void updateCurrent(int x, int y)
	{
		if(gameIsWon())
			return;
		toggleHighlights();
		int i = getX(x), j = getY(y);
		currX = i;
		currY = j;
		toggleHighlights();
		repaint();
	}
	private class MyComponent extends JComponent
	{
		@Override
		protected void paintComponent(Graphics g)
		{
			Graphics2D g2d = (Graphics2D)g;
			for(int i = 1; i <= grid_size; i++)
				for(int j = 1; j <= grid_size; j++)
					grid_information[i+1][j+1].draw(g2d);
			cycle.draw(g2d);
		}
	}
}