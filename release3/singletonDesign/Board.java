//**********************************************************/
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionEvent; // these two are needed to sniff mouse clicks
import java.awt.event.ActionListener;
import java.awt.*; // needed for GridLayout, and for ActionListener
import java.util.*;
//**********************************************************/


public class Board implements ActionListener {

    //* Fields
    //**********************************************************/
    JFrame frame; // create the tic tac toe frame, where buttons will reside
    // a frame has buttons and a size    
    private int frameSize;
    private int alternatingNum = 0; // keeps track which marker the computer places on board
    JButton[][] frameButtons; // a collection of buttons in a 2D format
    ArrayList<JButton> fButtonsList; // an array list of the board buttons
    State state; // keeps track of the game state
    MonteCarloSearch mSearch; // enables monte carlo search
    private boolean compMovesFirst; // determines if the computer moves first
    private boolean compPlayStatus; // determines if the computer is allowed to play
    String marker; // the game marker, an "X" or an "O"
    TicTacToe tacToe; // take the tic tac toe object created in the class of the same name
    //**********************************************************/

    //* Board constructor
    Board(int frameSize) {
        this.frameSize = frameSize;
        this.frameButtons = new JButton[frameSize][frameSize]; // the 2D buttons are created
        state = new State(frameSize); // a state of the specific size is created
        JFrame.setDefaultLookAndFeelDecorated(true); // the frame with the squares is decorated
        frame = new JFrame("Tic Tac Toe"); // create a frame for the grid, with a label
        frame.setResizable(false);
        fButtonsList = new ArrayList<>();// create an array list for the buttons
        mSearch = new MonteCarloSearch(frameSize); // create a monte carlo search object
    }
    //**********************************************************/
    
    //* Methods
    
    /*
     * Set the tic tac toe reference to that of the object created in the Tic Tac Toe class
     */
    public void setTicTacToe(TicTacToe tacToe){
        this.tacToe = tacToe;
    }
    //**********************************************************/

    /*
     * Set the computer plays or not option.
     */
    public void setCompPlayStatus(boolean compPlayStatus){
        this.compPlayStatus = compPlayStatus;
    }
    //**********************************************************/

    /*
     * Set the computer moves first option as obtained from the Tic Tac Toe class
     */
    public void setCompMovesFirst(boolean compMovesFirst){
        this.compMovesFirst = compMovesFirst;
    }
    //**********************************************************/

    public void setFrameVisibility(boolean value){
        frame.setVisible(value);
    }

    /*
     * Create a collection of buttons, and them to the frame which will record the game
     */
    public void setButtons() {

        frame.setLayout(new GridLayout(frameSize, frameSize)); // make the grid be 3x3, a user choice can change this
        
        for (int i = 0; i < frameSize; i++) {
            for (int j = 0; j < frameSize; j++) {
                frameButtons[i][j] = new JButton(); // point the frameButtons to button objects
                frameButtons[i][j].setFont(new Font(Font.DIALOG, Font.PLAIN, 60)); // set the font of the squares
                frameButtons[i][j].setText(ButtonState.Empty); // initially the squares are set to an empty string
                frameButtons[i][j].addActionListener(this); // enables association of clicks to the class object, in our case TicTacToe
                frame.add(frameButtons[i][j]); // add the frameButtons to the frame
                fButtonsList.add(frameButtons[i][j]); // add the frameButtons to the array list
            }
        }
        
        frame.pack(); // this option seemed to speed up the gui display
        frame.setSize(600 * (frameSize / 3), 600 * (frameSize / 3));// set the frame size
        frame.setLocationRelativeTo(null); // centralize the square board location
        
        frame.setVisible(true); // make the frame visible
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // if we close the frame, the program ends
        
        state.createButtons(); // create the state buttons to queue terminal choices
        mSearch.setState(state); // transport the state information to the monte carlo search
    }
    //**********************************************************/

    /*
     * Adjust the markers accordingly if the computer is set to move second
     */
    public void compMovesSecond(){
        if (compPlayStatus && !compMovesFirst) {
            mSearch.setCompMarker(ButtonState.O);
            mSearch.setPlayerMarker(ButtonState.X);
        }
    }
    //**********************************************************/

    /*
     * Handle the process of making the first move, if the computer is
     * set to move first.
     */
    public void makeCompFirstMove(){
        // if the computerStatus is true, and compMovesFirst is true
        if (compPlayStatus && compMovesFirst) {

            mSearch.setCompMarker(ButtonState.X); // the computer will play as "X"
            mSearch.setPlayerMarker(ButtonState.O);

            // pass to monte carlo the alternating num value so markers are handled accordingly
            mSearch.setAltNum(alternatingNum); 
            mSearch.selectBestIndex();

            // if an immediate terminal move exists
            if (mSearch.getImmediateStatus()) {
                int row = mSearch.getImmRow(); // obtain its location
                int col = mSearch.getImmCol();
                marker = mSearch.getComputerMarker(); // and marker symbol
                applyComputerChoice(row, col, marker); // apply the terminal move
                mSearch.setImmediateExists(false); // set the value to false, so that other games start fresh
            } else {
                int bestIndex = mSearch.getIndexBest(); // if no immediate terminal move, obtain the 
                int row = state.buttonList.get(bestIndex).getRow(); // location given by the monte carlo 
                int col = state.buttonList.get(bestIndex).getCol(); // search results
                marker = mSearch.getComputerMarker(); // set the marker accordingly
                applyComputerChoice(row, col, marker); // and apply the choice
            }
        }
    }
    //**********************************************************/

    /*
     * Detect the user clicks, and record them
     */
    @Override
    public void actionPerformed(ActionEvent event) {   
        
        
        JButton buttonClicked = (JButton) event.getSource(); // get source of clicks, an object will be returned, so casting is needed
        int playAgain = 10; // determine if the user wants to play again
        int i = buttonClicked.getLocation().y / buttonClicked.getBounds().height; // get the location of the clicked button
        int j = buttonClicked.getLocation().x / buttonClicked.getBounds().width;
        
        if(!buttonClicked.getText().equals(ButtonState.Empty)) // make sure clicked buttons don't change if clicked again
            return;

        // clicking alternating buttons, produces alternating X and O
        marker = this.alternatingNum % 2 == 0 ? ButtonState.X : ButtonState.O;
        buttonClicked.setText(marker); // set the marker symbol accordingly
        alternatingNum++; // update the alternating number  

        state.setMoveNumber(alternatingNum); // pass the alternating number
        state.recordPlayData(i, j, marker); // and the marker values and symbol to the state
        state.setClickedButton(i, j, marker); // record info on which buttons are clicked, and unavailable     
                
        
        if (state.isPresentMoveTerminal()) { // if the present move is a game ending move

            if (!state.isDraw) { // prompt the user with a message which includes who won
                playAgain = JOptionPane.showConfirmDialog(null, state.winMarker + " wins! Do you want to play again?", marker + "won!", JOptionPane.YES_NO_OPTION);
            } else { // otherwise the message informs the user of a drawn game
                playAgain = JOptionPane.showConfirmDialog(null, " The game is a draw! Do you want to play again?", "Draw!", JOptionPane.YES_NO_OPTION);
            }

            if(playAgain == JOptionPane.YES_OPTION) { // if the user wants to play again
                
                clearButtons(); // clear the board buttons for the new game
                state.clearPlayData(); // clear the recorded play data
                state.clearClickedInfo(); // and the data on which buttons were clicked
                state.clearWinScores();
                frame.dispose();  // dispose the buttons frame
                tacToe.buildFrameNew(); // and build a new game frame for the user to choose from                
                
            } else {
                System.exit(0); // otherwise we exit
            }
        } else { // if the present move is not terminal

            if (compPlayStatus) { // and the computer play option is available
                mSearch.setAltNum(alternatingNum); // transfer the proper alternating number to the monte carlo search
                mSearch.selectBestIndex();
                if (mSearch.getImmediateStatus()) { // if there are immediate terminal moves
                    int row = mSearch.getImmRow(); // get their location
                    int col = mSearch.getImmCol();
                    marker = mSearch.getComputerMarker(); // and marker symbol
                    applyComputerChoice(row, col, marker); // and apply them
                    mSearch.setImmediateExists(false); // reset for a new game
                } else { // if no terminal moves exist
                    int bestIndex = mSearch.getIndexBest(); // find the best option offered by a monte carlo search
                    int row = state.buttonList.get(bestIndex).getRow();
                    int col = state.buttonList.get(bestIndex).getCol();
                    marker = mSearch.getComputerMarker(); 
                    applyComputerChoice(row, col, marker); // and apply the choice
                }
            }            
        }        
    }
    //**********************************************************/

    /*
     * Apply the computer move on the board
     */
    public void applyComputerChoice(int row, int col, String marker) {
        int playAgain = 10;
        alternatingNum++; // update the alternating number
        state.setMoveNumber(alternatingNum); // and inform the state of the change
        state.recordPlayData(row, col, marker); // record the move on the state
        state.setClickedButton(row, col, marker); // record the clicked button info on the state

        mSearch.setAltNum(alternatingNum); // pass the updated alternating number to the monte carlo search
        frameButtons[row][col].setText(marker); // make the move visible on the board

        mSearch.setCompClickedButton(row, col); // inform monte carlo that the button is clicked

        if (state.isPresentMoveTerminal()) { // if the compute made move is terminal

            if (!state.isDraw) { // inform the user who won, and if they want to play again
                playAgain = JOptionPane.showConfirmDialog(null, state.winMarker + " wins! Do you want to play again?", marker + "won!", JOptionPane.YES_NO_OPTION);
            } else { // otherwise the game was a draw, and ask the user if they want to play again
                playAgain = JOptionPane.showConfirmDialog(null, " The game is a draw! Do you want to play again?", "Draw!", JOptionPane.YES_NO_OPTION);
            }

            if(playAgain == JOptionPane.YES_OPTION) { // if the user wants to play again
                clearButtons(); // clear the board buttons
                state.clearPlayData(); // clear the play data
                state.clearClickedInfo(); // and which buttons were clicked info
                state.clearWinScores();
                frame.dispose();   // dispose the buttons frame                     
                tacToe.buildFrameNew(); // and build a new interactive frame          
            } else {
                System.exit(0); // otherwise we exit
            }                  
        } 
    }
    //**********************************************************/

    /*
     * Set all the button markers to an empty state
     */ 
    public void clearButtons(){
        for (int i = 0; i < fButtonsList.size(); i++) {
            fButtonsList.get(i).setText(ButtonState.Empty);
        }
        alternatingNum = 0; // set the alternating number to zero
    }
    //**********************************************************/

}