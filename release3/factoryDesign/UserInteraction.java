//**************************************************************/
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JSplitPane;
//**************************************************************/

/**
 * This program allows the user to play the game of Tic Tac Toe with a computer opponent which
 * uses monte carlo techniques and upper confidence bounds to determine its moves.
 * @version: 02/12/2020
 * @author: Andris Docaj
 */

//**************************************************************/

public class UserInteraction implements Game{

    //*Fields
    //**********************************************************/
    Board board;
    GameFactory gameFactory;
    JPanel panel; // facilitates the transportation of main panel
    UserInteraction tacToe;// saves game info, to be used by board 
    JFrame gameFrame; // main game frame
    private int size; // board size attribute 
    private boolean compPlayStatus; // should the computer engine be activated or not
    private boolean compMovesFirst; // should the computer move first
    //**********************************************************/

    //* Constructor that sets the default game values
    UserInteraction(){  
        gameFactory = new GameFactory(); 
        board =(Board) gameFactory.getGameType(ObjectChoice.boardObject);
        compPlayStatus = true; // computer plays by default
        board.setCompPlayStatus(compPlayStatus); // this info is passed to the board
        compMovesFirst = true; // computer is set to move first by default
        board.setCompMovesFirst(compMovesFirst);
        JFrame.setDefaultLookAndFeelDecorated(true); // a frame decoration option
        gameFrame = new JFrame("Tic Tac Toe Menu"); // the frame label
        gameFrame.setResizable(false);
    }
    //**********************************************************/


    //* Methods
    //**********************************************************/

    /*
     * Sets the UserInteraction object to the parameter value. Used to save 
     * information, which the garbage collector might disregard.
     * The information is used by the Board procedures.
     */
    public void setUserInteraction(UserInteraction tacToe){
        this.tacToe = tacToe;
    }
    //**********************************************************/

    /*
     * Used to pass the main panel information to the buildFrame method.
     */
    public void setPanel(JPanel panel){
        this.panel = panel;
    }
    //**********************************************************/

    /*
     * Create the game panel, and pass that information to the buildFrame method.
     */
    public void createPanel(){

        int fontSize = 14; // the font size of the panel       

        // a comboBox which decides who moves first, player or computer
        final DefaultComboBoxModel<String> moveOrder = new DefaultComboBoxModel<>();
        moveOrder.addElement("Computer");
        moveOrder.addElement("Player");
      
        // a label of the interactive options frame
        JLabel gameOptions = new JLabel("Game Options.", JLabel.CENTER);
        gameOptions.setFont(new Font("Curier", Font.BOLD, fontSize));

        //**********************************************************/

        // a panel that holds the label info for computer play options
        JPanel labelBPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel labelB = new JLabel("Play with: ");
        labelB.setFont(new Font("Curier", Font.BOLD, fontSize));
        labelBPanel.add(labelB);

        // a panel that holds the info for the computer move order
        JPanel labelDPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel labelD = new JLabel("Move first: ");
        labelD.setFont(new Font("Curier", Font.BOLD, fontSize));
        labelDPanel.add(labelD);

        //**********************************************************/

        //**********************************************************/

        // panel that holds the computer play order option
        JPanel cBoxDPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JComboBox<String> comboBoxD = new JComboBox<>(moveOrder);
        comboBoxD.setFont(new Font("Courier", Font.BOLD, fontSize));
        cBoxDPanel.add(comboBoxD);

        //**********************************************************/

        //**********************************************************/
        // each label panel, and options panel is combined into one panel
       
        JSplitPane jspD = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, labelDPanel, cBoxDPanel);
        jspD.setResizeWeight(0.47);
        //**********************************************************/

        //**********************************************************/
        // each options panel is equipped with an action listener to pick up 
        // user preferences
        //**********************************************************/
      

                
        comboBoxD.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    JComboBox<?> cb = (JComboBox<?>) event.getSource();
                    if (cb.getSelectedItem().equals("Computer first.")) {
                        compMovesFirst = true;
                        board.setCompMovesFirst(compMovesFirst);
                    } else {
                        compMovesFirst = false;
                        board.setCompMovesFirst(compMovesFirst);
                    }
                }
            });
        //**********************************************************/
        
        //**********************************************************/
        // all the separate panels are combined into a main panel which conforms
        // to a grid layout of 5 rows
        //**********************************************************/
        //JPanel mainPanel = new JPanel(new GridLayout(3, 0));
        JPanel mainPanel = new JPanel(new GridLayout(2, 0));
        mainPanel.add(gameOptions);
        //mainPanel.add(jspA);

        mainPanel.add(jspD);
        setPanel(mainPanel); // the main panel is passed to the panel field
        //**********************************************************/

    }

    /*
     * buildFrame builds the main frame which interacts with the user, and acquires
     * the user's preferred choices.
     */
    public void buildFrame(){

        // decorate frame, label it, and close it, when the exit option is selected
        JFrame.setDefaultLookAndFeelDecorated(true);
        gameFrame = new JFrame("Tic Tac Toe Menu");
        gameFrame.setResizable(false);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // set the main panel to the panel created in createPanel
        JPanel mainPanel = new JPanel();
        this.createPanel();
        mainPanel = this.panel;
        mainPanel.setPreferredSize(new Dimension(300, 250)); // and assign it a size

        // the frame will show up in the center of the screen
        gameFrame.setLocationRelativeTo(null);
        gameFrame.getContentPane().add(mainPanel); // add the main panel to the game frame

        //**********************************************************/
        // create a button that applies the options of the user
        JButton button = new JButton("Apply");
        button.setFont(new Font("Courier", Font.BOLD, 16));
        gameFrame.add(button, BorderLayout.SOUTH);
        
        // add action listener to the square
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                board.setButtons(); // first create the buttons
                board.setCompPlayStatus(compPlayStatus); // then apply the user choices
                board.setCompMovesFirst(compMovesFirst);
                gameFrame.dispose(); // dispose the game frame
                if (compPlayStatus) {
                    if (compMovesFirst) { // if the computer moves first
                        board.makeCompFirstMove(); // make the computer move
                    } else {
                        board.compMovesSecond(); // else inform the computer that it moves second
                    }
                } 
            }
        });
        //**********************************************************/ 
        
        gameFrame.pack(); // pack all the options, main panel and button, into the frame
        gameFrame.setVisible(true); // make the frame visible
    }
}