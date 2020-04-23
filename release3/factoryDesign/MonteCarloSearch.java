
public class MonteCarloSearch implements Game{

    //* Fields
    //**********************************************************/
    State state; // mSearch needs to know the state of the board
    GameFactory gameFactory;
    private int numSimulations; // how many simulations per move to make
    private int altNum; // and the alternating number
    private int IndexBest; // the index of best possible move will be passed on to the board
    private int randomPick; // the index of a random pick will be used in the simulation

    private int simRow; // a row from a simulation
    private int simCol; // a column from a simulation
    private String simMarker; // a simulation marker

    private boolean immediateExists; // true if an immediate terminal move exists
    private int immRow; // the row of the immediate terminal move
    private int immCol; // the column of the immediate terminal move

    private int compClickedRow; // if the computer selects a square, that square is unavailable
    private int compClickedCol; // for the monte carlo analysis

    private int rewardVal; // reward for winning, given to winning squares
    private String computerMarker; // the marker symbol of the computer
    private String playerMarker; // the marker symbol of the player
    //**********************************************************/

    //* Monte carlo search constructor
    MonteCarloSearch(int size){
        //state = new State(size); // create a new state object
        gameFactory = new GameFactory();
        state = (State) gameFactory.getGameType(ObjectChoice.stateObject);
        numSimulations = 1500;  // set the simulation number to 1500 as default 
        IndexBest = 0;          // the best index set to 0, but will change
        immediateExists = false; // immediate exists is initially false
        rewardVal = 1; // the reward is +1 for winning
    }
    //**********************************************************/

    //* Methods

    /*
     * Set the computer marker symbol as decided by the Board class
     */
    public void setCompMarker(String computerMarker){
        this.computerMarker = computerMarker;
    }
    //**********************************************************/

    /*
     * Set player marker symbol
     */
    public void setPlayerMarker(String playerMarker){
        this.playerMarker = playerMarker;
    }
    //**********************************************************/

    /*
     * Set the truth status of the immediate value variable
     */
    public void setImmediateExists(boolean immediateExists){
        this.immediateExists = immediateExists;
    }
    //**********************************************************/

    /*
     * Get the immediate value status
     */
    public boolean getImmediateStatus(){
        return immediateExists;
    }
    //**********************************************************/

    /*
     * Get the row of the immediately terminal move
     */
    public int getImmRow(){
        return immRow;
    }
    //**********************************************************/

    /*
     * Get the column of the immediately terminal move
     */
    public int getImmCol(){
        return immCol;
    }
    //**********************************************************/

    /*
     * Keep track of which buttons the computer has clicked
     */
    public void setCompClickedButton(int row, int col){
        compClickedRow = row;
        compClickedCol = col;
    }
    //**********************************************************/

    /*
     * Set state to that existing in the Board class
     */
    public void setState(State state) {
        this.state = state;
    }
    //**********************************************************/

    /*
     * Set the alternating number as determined by the Board class
     */
    public void setAltNum(int altNum){
        this.altNum = altNum;
    }
    //**********************************************************/

    /*
     * Get the computer marker symbol
     */
    public String getComputerMarker(){
        return computerMarker;
    }
    //**********************************************************/
    
    /*
     * Generate a random index which is not already occupied
     */
    public int generateUniqueRandom(){
        if (!state.isPresentMoveTerminal()) {
            int randomPick = (int)(Math.random()*state.buttonList.size());
            // loop exits if randomPick is not visited
            while (state.buttonList.get(randomPick).visited() && 
            state.buttonList.get(randomPick).clicked()) {
                randomPick = (int)(Math.random()*state.buttonList.size());
            }               
            
            return randomPick; 
        }
        return -1;
    }
    //**********************************************************/
    
    /*
     * Run a pure monte carlo search which emphasizes terminal moves
     */
    public void simulate(){
        // first we store the present state info
        state.storePresentState();
        for (int i = 0; i < numSimulations; i++) { // do 1000 simulations
            
            simRow = state.getClickedRow(); // commence simulation from the clicked button
            simCol = state.getClickedCol(); 
            simMarker = state.getClickedMarker();
            int tempAltNum = altNum; // store the real alternating number into a temp variable

            while (!state.isPresentMoveTerminal()) { // while the state is not terminal

                //**********************************************************/
                if (state.availableTerminalMove() != null) { // if an immediate terminal move exists

                    simRow = state.availableTerminalMove().getRow(); // pick it
                    simCol = state.availableTerminalMove().getCol();  
                    simMarker = tempAltNum % 2 == 0 ? Game.X : Game.O;
                    state.recordPlayData(simRow, simCol, simMarker); // and record its info
                    tempAltNum++;
                    state.setMoveNumber(tempAltNum);

                } else if (state.isNextMoveTerminal(simRow, simCol)) { // if a terminal move exists on the next move
                    state.immedTerminalMoves(simRow, simCol); // pick it                
                    simRow = state.getTerminalRow();
                    simCol = state.getTerminalCol();
                    simMarker = tempAltNum % 2 == 0 ? Game.X : Game.O;
                    state.recordPlayData(simRow, simCol, simMarker); // and record its info
                    tempAltNum++;
                    state.setMoveNumber(tempAltNum);                    
                } else{ // otherwise
                    randomPick = generateUniqueRandom(); // generate a unique index
                    simRow = state.buttonList.get(randomPick).getRow(); // obtain the index's location
                    simCol = state.buttonList.get(randomPick).getCol();
                    state.immedTerminalMoves(simRow, simCol);
                    simMarker = tempAltNum % 2 == 0 ? Game.X : Game.O;
                    state.recordPlayData(simRow, simCol, simMarker); // and apply the virtual move
                    tempAltNum++;
                    state.setMoveNumber(tempAltNum); 
                }           
            }
                //**********************************************************/
            
            //**********************************************************/
            if (!state.isDraw) { // if the game ends in a win
                if (state.winMarker.equals(computerMarker)) { // and if the computer is the victor
                    updateComputerScore(); // update the win score
                } 
                else {
                    updatePlayerScore();// update the win score if the player wins, a defensive manoeuver
                }
            } else {
                updateVisitNumber(); // if draw, update the visit number of the squares
            }
            //**********************************************************/
            state.clearPlayData(); // clear the play data
            state.restoreOriginalState(); // and restore the original state
        }
        state.clearStoredData(); // clear stored data, so we can analyse the next actual move
    }
    //**********************************************************/

    /*
     * Determines the best possible move for the computer, and makes the information 
     * available to the board class.
     */
    public void selectBestIndex() {  

        if (checkVisitStatus()) {
            simulate();
        }

        state.immedTerminalMoves(compClickedRow, compClickedCol); // determine the immediate terminal moves for the clicked button 

        if (state.availableTerminalMove() != null) { // if an immediate terminal move exists
            immediateExists = true; // inform the board class of such information
            immRow = state.availableTerminalMove().getRow();  // and provide the location of 
            immCol = state.availableTerminalMove().getCol();  // the terminal move

        } else if (state.isNextMoveTerminal(state.getClickedRow(), state.getClickedCol())) {  
            immediateExists = true;  // if the next move is a terminal move, inform the Board class              
            immRow = state.getTerminalRow(); // and provide the information of the next terminal move
            immCol = state.getTerminalCol();
            
        } else{ // otherwise             
                 
            findMaxUCTScore();   // find the button with the maximum UCT value
        } 
    }
    //**********************************************************/

    /*
     * Updates the visit number of the squares in case of draw, and establishes the UCT value of each button.
     */
    public void updateVisitNumber() {

        for (Button button : state.buttonList) {
            if (button.visited() && !button.clicked()) { // if the button is visited but not yet clicked
                int visitNum = button.getVisitedNumber();
                visitNum += 1; // update the visit number
                button.setVisitNumber(visitNum);
                button.setUCTScore((double) button.getWinScore(), (double) visitNum, (double) numSimulations);
            }
        }        
    }
    //**********************************************************/

    /*
     * Rewards winning, establishes the UCT value of buttons.
     */
    public void updateComputerScore() {

        for (Button button : state.buttonList) {
            if (button.visited() && !button.clicked()) { // if the button is visited but not yet clicked
                // if the button marker is same as computer marker
                if (button.getMarker().equals(computerMarker)) {
                    int winScore = button.getWinScore();
                    int visitNum = button.getVisitedNumber();
                    winScore += rewardVal; // reward the computer marked squares 
                    visitNum += 1; // update the visit number
                    button.setWinScore(winScore); // update the win/loss scores of the buttons
                    button.setVisitNumber(visitNum);
                    button.setUCTScore((double) winScore, (double) visitNum, (double) numSimulations);
                } 
            }
        }        
    }
    //**********************************************************/

    /*
     * Rewards winning squares in case of a computer loss, and establishes the UCT value of buttons.
     * Used to prevent a player from winning.
     */
    public void updatePlayerScore() {

        for (Button button : state.buttonList) {
            if (button.visited() && !button.clicked()) { // if the button is visited but not yet clicked
                // if the button marker is same as the player marker
                if (button.getMarker().equals(playerMarker)) {
                    int winScore = button.getWinScore();
                    int visitNum = button.getVisitedNumber();
                    winScore += rewardVal; // reward the player marked squares 
                    visitNum += 1; // update the visit number
                    button.setWinScore(winScore); // update the win/loss scores of the buttons
                    button.setVisitNumber(visitNum);
                    button.setUCTScore((double) winScore, (double) visitNum, (double) numSimulations);
                } 
            }
        }        
    }
    //**********************************************************/

   
    //**********************************************************/
    
    /*
     * Return true if a button exists which is not visited by the simulation, and not clicked.
     */
    public boolean checkVisitStatus(){
        for (Button button : state.buttonList) {
            if (!button.visited() &&  !button.clicked()) { // if button is not visited and not clicked
                return true; // return true
            }
        }
        return false; // else return false
    }
    //**********************************************************/

    /*
     * Find the  square with the maximum UCT score, and record its index.
     */
    public void findMaxUCTScore(){
        double max = 0;
        int index = 0;
        for (Button button : state.buttonList) {
            if (!button.visited() && max < button.getUCTScore()) {
                max = button.getUCTScore();
                IndexBest = index; // pick the square with largest UCT score
            }
            index++;
        }
    }
    //**********************************************************/

    
    //**********************************************************/

    /*
     * Get the best index, for the next computer move and provide it to the board class. 
     */
    public int getIndexBest(){
        return IndexBest;
    }
    //**********************************************************/
}