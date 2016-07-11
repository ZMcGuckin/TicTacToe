import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Simple TicTacToe game created using Java Swing Graphics.
 *
 * @author Zach McGuckin
 *
 */
@SuppressWarnings("serial")
public class TicTacToeMain extends JFrame 
{
   // Named-constants for the game board
   public final int ROWS = 3;  // ROWS by COLS cells
   public final int COLS = 3;
   
   public int mouseX2;  // Coordinates
   public int mouseY2;

   // Named-constants of the various dimensions used for graphics drawing
   public final int CELL_SIZE = 200; // cell width and height (square)
   public final int CANVAS_WIDTH = CELL_SIZE * COLS;  // the drawing canvas
   public final int CANVAS_HEIGHT = CELL_SIZE * ROWS;
   public final int GRID_WIDTH = 8;                   // Grid-line's width
   public final int GRID_WIDTH_HALF = GRID_WIDTH / 2; // Grid-line's half-width
   // Symbols (cross/nought) are displayed inside a cell, with padding from border
   public final int CELL_PADDING = CELL_SIZE / 6;
   public final int SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2; // width/height
   public final int SYMBOL_STROKE_WIDTH = 8; // stroke width

   // Use an enumeration (inner class) to represent the various states of the game
   public enum GameState 
   {
      PLAYING, DRAW, CROSS_WON, NOUGHT_WON
   }
   private GameState currentState;  // the current game state

   // Use an enumeration (inner class) to represent the seeds and cell contents
   public enum Seed 
   {
      EMPTY, CROSS, NOUGHT
   }
   private Seed currentPlayer;  // the current player

   private Seed[][] board; // Game board of ROWS-by-COLS cells
   private Seed[][] winBoard; // Array used to store the winning combination later
   private DrawCanvas canvas; // Drawing canvas (JPanel) for the game board
   private JLabel statusBar;  // Status Bar

   /** Constructor to setup the game and the GUI components */
   public TicTacToeMain() {
      canvas = new DrawCanvas();  // Construct a drawing canvas (a JPanel)
      canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

      // The canvas (JPanel) fires a MouseEvent upon mouse-click
      canvas.addMouseMotionListener(new MouseAdapter()
      {
    	 @Override
    	 public void mouseMoved(MouseEvent e){
    		 TicTacToeMain.this.mouseX2 = e.getX();
    		 TicTacToeMain.this.mouseY2 = e.getY();
    		 repaint();
    	 }
      });
      // The canvas (JPanel) fires a MouseEvent upon mouse-click
      canvas.addMouseListener(new MouseAdapter()
      {
         @Override
         public void mouseReleased(MouseEvent e)  // mouse-clicked handler
         {
            int mouseX = e.getX();
            int mouseY = e.getY();
            // Get the row and column clicked
            int rowSelected = mouseY / CELL_SIZE;
            int colSelected = mouseX / CELL_SIZE;

            if (currentState == GameState.PLAYING) 
            {
               if (rowSelected >= 0 && rowSelected < ROWS && colSelected >= 0
                     && colSelected < COLS && board[rowSelected][colSelected] == Seed.EMPTY) 
               {
                  board[rowSelected][colSelected] = currentPlayer; // Make a move
                  updateGame(currentPlayer, rowSelected, colSelected); // update state
                  // Switch player
                  currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
               }
            } 
            else        // game over
               initGame(); // restart the game
            // Refresh the drawing canvas
            repaint();  // Call-back paintComponent().
         }
      });

      // Setup the status bar (JLabel) to display status message
      statusBar = new JLabel("  ");
      statusBar.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
      statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));

      Container cp = getContentPane();
      cp.setLayout(new BorderLayout());
      cp.add(canvas, BorderLayout.CENTER);
      cp.add(statusBar, BorderLayout.PAGE_END); // same as SOUTH

      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      pack();  // pack all the components in this JFrame
      setTitle("Tic Tac Toe");
      setVisible(true);  // show this JFrame

      board = new Seed[ROWS][COLS]; // allocate array
      winBoard = new Seed[ROWS][COLS]; // allocate array
      initGame(); // initialize the game board contents and game variables
   }

   /** Initialize the game-board contents and the status */
   public void initGame() 
   {
      for (int row = 0; row < ROWS; ++row) 
      {
         for (int col = 0; col < COLS; ++col) 
         {
            board[row][col] = Seed.EMPTY; // all cells empty
            winBoard[row][col] = Seed.EMPTY;
         }
      }
      currentState = GameState.PLAYING; // ready to play
      currentPlayer = Seed.CROSS;       // cross plays first
   }

   /* Update the currentState after the player with "theSeed" has placed on
       (rowSelected, colSelected). */
   public void updateGame(Seed theSeed, int rowSelected, int colSelected) 
   {
      if (hasWon(theSeed, rowSelected, colSelected)) 
         currentState = (theSeed == Seed.CROSS) ? GameState.CROSS_WON : GameState.NOUGHT_WON;
      else if (isDraw()) 
         currentState = GameState.DRAW;
      // Otherwise, no change to current state (still GameState.PLAYING).
   }

   /** Return true if it is a draw (i.e., no more empty cell) */
   public boolean isDraw() 
   {
      for (int row = 0; row < ROWS; ++row) 
      {
         for (int col = 0; col < COLS; ++col) 
         {
            if (board[row][col] == Seed.EMPTY) 
            {
               return false; // an empty cell found, not draw, exit
            }
         }
      }
      return true;  // no more empty cell, it's a draw
   }

   /** Return true if the player with "theSeed" has won after placing at
       (rowSelected, colSelected) */
   public boolean hasWon(Seed theSeed, int rowSelected, int colSelected) 
   {
	  // 3-in-the-row 
      if(board[rowSelected][0] == theSeed && board[rowSelected][1] == theSeed && board[rowSelected][2] == theSeed)
      {
    	  winBoard[rowSelected][0] = theSeed;
    	  winBoard[rowSelected][1] = theSeed;
    	  winBoard[rowSelected][2] = theSeed;
    	  return true;
      }
      //3 in-the-column
      else if(board[0][colSelected] == theSeed && board[1][colSelected] == theSeed && board[2][colSelected] == theSeed)
      {
    	  winBoard[0][colSelected] = theSeed;
    	  winBoard[1][colSelected] = theSeed;
    	  winBoard[2][colSelected] = theSeed;
    	  return true;
      }
      // 3-in-the-diagonal
      else if(rowSelected == colSelected && board[0][0] == theSeed && board[1][1] == theSeed && board[2][2] == theSeed)
      {
    	  winBoard[0][0] = theSeed;
    	  winBoard[1][1] = theSeed;
    	  winBoard[2][2] = theSeed;
    	  return true;
      }
      // 3-in-the-opposite-diagonal
      else if(rowSelected + colSelected == 2 && board[0][2] == theSeed && board[1][1] == theSeed && board[2][0] == theSeed)
      {
    	  winBoard[2][0] = theSeed;
    	  winBoard[1][1] = theSeed;
    	  winBoard[0][2] = theSeed;
    	  return true;
      }
      return false;
   }

   /**
    *  Inner class DrawCanvas (extends JPanel) used for custom graphics drawing.
    */
   class DrawCanvas extends JPanel 
   {
      @Override
      public void paintComponent(Graphics g) // invoke via repaint()
      {  
         super.paintComponent(g);    // fill background
         setBackground(Color.WHITE); // set its background color

         // Draw the grid-lines
         g.setColor(Color.LIGHT_GRAY);
         for (int row = 1; row < ROWS; ++row) 
         {
            g.fillRoundRect(0, CELL_SIZE * row - GRID_WIDTH_HALF,
                  CANVAS_WIDTH-1, GRID_WIDTH, GRID_WIDTH, GRID_WIDTH);
         }
         for (int col = 1; col < COLS; ++col) 
         {
            g.fillRoundRect(CELL_SIZE * col - GRID_WIDTH_HALF, 0,
                  GRID_WIDTH, CANVAS_HEIGHT-1, GRID_WIDTH, GRID_WIDTH);
         }

         // Draw the Seeds of all the cells if they are not empty
         // Use Graphics2D which allows us to set the pen's stroke
         Graphics2D g2d = (Graphics2D)g;
         g2d.setStroke(new BasicStroke(SYMBOL_STROKE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));  // Graphics2D only
         g2d.setColor(Color.RED);
         //Tracer to know where they are placing their piece
         if(currentState == GameState.PLAYING){
        	 if(currentPlayer == Seed.NOUGHT){
        		 g2d.setColor(Color.BLUE);
        		 int centerX = mouseX2 - (SYMBOL_SIZE/2);
        		 int centerY = mouseY2 - (SYMBOL_SIZE/2);
        		 g2d.drawOval(centerX, centerY, SYMBOL_SIZE, SYMBOL_SIZE);
        	 } else{
        		 g2d.setColor(Color.RED);
        		 int centerX = mouseX2 - (SYMBOL_SIZE/2);
        		 int centerY = mouseY2 - (SYMBOL_SIZE/2);
        		 g2d.drawLine(centerX, centerY, centerX + SYMBOL_SIZE, centerY + SYMBOL_SIZE);
        		 g2d.drawLine(centerX, centerY + SYMBOL_SIZE, centerX + SYMBOL_SIZE, centerY);
        	 }
         }
         for (int row = 0; row < ROWS; ++row) 
         {
            for (int col = 0; col < COLS; ++col) 
            {
               int x1 = col * CELL_SIZE + CELL_PADDING;
               int y1 = row * CELL_SIZE + CELL_PADDING;
               if (board[row][col] == Seed.CROSS) 
               {
                      g2d.setColor(Color.RED);
                      int x2 = (col + 1) * CELL_SIZE - CELL_PADDING;
                      int y2 = (row + 1) * CELL_SIZE - CELL_PADDING;
                      g2d.drawLine(x1, y1, x2, y2);
                      g2d.drawLine(x2, y1, x1, y2);
               }  
               else if (board[row][col] == Seed.NOUGHT) 
               {
                  g2d.setColor(Color.BLUE);
                  g2d.drawOval(x1, y1, SYMBOL_SIZE, SYMBOL_SIZE);
               }
               if(winBoard[row][col] == Seed.CROSS) 
               {
                  g2d.setColor(Color.GREEN);
                  int x2 = (col + 1) * CELL_SIZE - CELL_PADDING;
                  int y2 = (row + 1) * CELL_SIZE - CELL_PADDING;
                  g2d.drawLine(x1, y1, x2, y2);
                  g2d.drawLine(x2, y1, x1, y2);
               } 
               else if (winBoard[row][col] == Seed.NOUGHT) 
               {
                  g2d.setColor(Color.GREEN);
                  g2d.drawOval(x1, y1, SYMBOL_SIZE, SYMBOL_SIZE);
               }
            }
         }

         // Print status-bar message
         if (currentState == GameState.PLAYING) 
         {
            statusBar.setForeground(Color.BLACK);
            
            if (currentPlayer == Seed.CROSS)
               statusBar.setText("X's Turn");
            else 
               statusBar.setText("O's Turn");
         } 
         else if (currentState == GameState.DRAW) 
         {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click to play again.");
         } 
         else if (currentState == GameState.CROSS_WON) 
         {
            statusBar.setForeground(Color.GREEN);
            statusBar.setText("'X' Won! Click to play again.");
         } 
         else if (currentState == GameState.NOUGHT_WON) 
         {
            statusBar.setForeground(Color.GREEN);
            statusBar.setText("'O' Won! Click to play again.");
         }
      }
   }

   /** The entry main() method */
   public static void main(String[] args) 
   {
      // Run GUI codes in the Event-Dispatching thread for thread safety
      SwingUtilities.invokeLater(new Runnable() 
      {
         @Override
         public void run() 
         {
            new TicTacToeMain(); // Let the constructor do the job
         }
      });
   }
}