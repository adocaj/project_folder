//**********************************************************/
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Comparator;
import java.util.PriorityQueue;
//**********************************************************/


public class State implements Game{

    //* Fields
    //**********************************************************/
    
    //******************* */
    int size;
    int totalMoveNumber; // keep track of the total move number
    boolean isDraw; // true if the state is a draw
    String winMarker; // the marker symbol of the winning party

    int[] rowParity; // keep track how many markers of same kind occupy a row
    int[] colParity; // a column
    int[] diagParity; // or a diagonal

    HashMap<Integer, String> rowMap; // records symbols of same kind for a row, 
    HashMap<Integer, String> colMap; // column
    HashMap<Integer, String> dMap; // and diagonal
    //*************************************** */
    int[] rowFillFactor; // determines how many markers ("X" or "O") occupy a row 
    int[] colFillFactor; // column
    int[] diagFillFactor; // and diagonal

    Comparator<Button> queueValSorter; // sort queues according to queue value in increasing order
    ArrayList<PriorityQueue<Button>> rowQueueArr; // array list of buttons, queued according to 
    ArrayList<PriorityQueue<Button>> colQueueArr; // their queue values
    ArrayList<PriorityQueue<Button>> diagQueueArr;
    Button[][] button; 
    ArrayList<Button> buttonList;

    private int terminal_i; // terminal row, immediate move finishes game
    private int terminal_j; // terminal column
    ArrayList<Button> immedTerminalMovList; // a list of available game ending moves

    boolean rowTerminalNext; // row of next move that results in a terminal state
    boolean colTerminalNext; // column of next terminal move
    boolean eqDiagTerminalNext; // equal index diagonal of a terminal next move
    boolean unEqDiagTerminalNext; // un-equal diagonal for a terminal next move

    boolean rowColTerminalNow; // is a row or column terminal now
    boolean eqDiagTerminalNow; // is the equal diagonal terminal now
    boolean unEqDiagTerminalNow; // unequal diagonal terminability check

    private int clickedRow; // keep track of the clicked button and
    private int clickedCol;
    private String clickedMarker; // its marker

    //****************************************************** */
    // state storage quantities
    //****************************************************** */
    int storedTotalMoveNum;

    int[] storedRowParity;
    int[] storedColParity;
    int[] storedDiagParity;

    HashMap<Integer, String> storedRowMap; // an X row, or an O row
    HashMap<Integer, String> storedColMap;
    HashMap<Integer, String> storedDMap;
    //*************************************** */
    int[] storedRowFillFactor;
    int[] storedColFillFactor;
    int[] storedDiagFillFactor;
    //******************************************************* */
    int[] storedQueueValsArr;
    int[] storedVisitedInfoArr;
    int[] storedClickedInfoArr;


    //**********************************************************/
    //* State constructor
    State(int size){
        this.size = size;
        //**************************************** */
        this.totalMoveNumber = 0;
        this.winMarker = Game.Empty;
        isDraw = false;

        rowParity = new int[size];
        colParity = new int[size];
        diagParity = new int[2];// 0 equal, 1 unequal

        rowMap = new HashMap<>();
        colMap = new HashMap<>();
        dMap = new HashMap<>();
        //************************************************* */
        rowFillFactor = new int[size];
        colFillFactor = new int[size];
        diagFillFactor = new int[2];
        //************************************************* */
        queueValSorter = Comparator.comparing(Button::getQueueVal);

        rowQueueArr = new ArrayList<>(); 
        colQueueArr = new ArrayList<>();
        diagQueueArr = new ArrayList<>();

        button = new Button[size][size];
        buttonList = new ArrayList<>();

        immedTerminalMovList = new ArrayList<>();
        
        rowTerminalNext = false;
        colTerminalNext = false;
        eqDiagTerminalNext = false;
        unEqDiagTerminalNext = false;

        rowColTerminalNow = false;
        eqDiagTerminalNow = false;
        unEqDiagTerminalNow = false;

        
        // state storage quantities
        //*************************************** */
        storedTotalMoveNum = totalMoveNumber; // needed for drawing conditions

        storedRowParity = new int[size];
        storedColParity = new int[size];
        storedDiagParity = new int[2];

        storedRowMap = new HashMap<>();
        storedColMap = new HashMap<>();
        storedDMap = new HashMap<>();

        storedRowFillFactor = new int[size];
        storedColFillFactor = new int[size];
        storedDiagFillFactor = new int[2];

        storedQueueValsArr = new int[size * size];
        storedVisitedInfoArr = new int[size * size];
    }
    //**********************************************************/


    //*Methods
    //**********************************************************/

    // Original State activity
    
    //********************************************************************************** */

    /*
     * Set move number, used in Board and Monte Carlo search 
     */
    public void setMoveNumber(int totalMoveNumber) {
        this.totalMoveNumber = totalMoveNumber;
    }
    //**********************************************************/

    /*
     * Store info about rows and columns. If a terminal condition exists
     * make such information available.
     */
    public void checkRowCol(HashMap<Integer, String> hMap, int[] parityArray, int i, String marker){
        
        if (hMap.isEmpty()) { // if rowMap or colMap is empty, put the new marker info in it
            hMap.put(i, marker);
            ++parityArray[i]; // update the parity array
        } else {
            if (!hMap.containsKey(i)) {// if the key is not in the map
                hMap.put(i, marker); // add it to the map
                ++parityArray[i];
            } else if (hMap.get(i).equals(marker)) { // otherwise add keys of already existing markers
                ++parityArray[i];
                if (parityArray[i] == size) { // if the parity array is equal to size a win has occurred
                    winMarker = marker;
                    rowColTerminalNow = true; // make such information available
                    return;
                }
            } 
        }
        
    }
    //**********************************************************/

    /*
     * store information about diagonals. If a terminal state gets reached
     * make such information available.
     */
    public void checkDiagonal(int i, int j, String marker){
        
        if(i == j) {
            if (!dMap.containsKey(0)) { // if the equal diagonal map is empty, put the new element in it
                dMap.put(0, marker);
                ++diagParity[0]; // update the equal diagonal parity array
            } else {
                if (dMap.get(0).equals(marker)) {
                    ++diagParity[0];
                    if (diagParity[0] == size) { // if a terminal state is reached
                        winMarker = marker;     // make such information known
                        eqDiagTerminalNow = true;
                        return;
                    }
                }
                
            }
        } 
        if (i+j == size -1) { // if a row/col falls into the unequal diagonal
            if (!dMap.containsKey(1)) { // and the map of such diagonal is empty
                dMap.put(1, marker); // put the new element into the map
                ++diagParity[1]; // update the unequal diagonal parity array
            } else {
                if (dMap.get(1).equals(marker)) {
                    ++diagParity[1];
                    if (diagParity[1] == size) { // if a terminal state exists 
                        winMarker = marker;
                        unEqDiagTerminalNow = true; // make such information available
                        return;
                    }
                }
            }
        }
    }
    //**********************************************************/

    /*
     * Create buttons which will mirror the jButtons of the Board class
     */
    public void createButtons(){        
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                button[i][j] = new Button(i, j, Game.Empty);
                buttonList.add(button[i][j]);
            }
        }
    }
    //**********************************************************/

    /*
     * Create a row queue so that the rows with lowest queue value are
     * obtained in constant time.
     */
    public void createRowQueue(){
        for (int i = 0; i < size; i++) {
            rowQueueArr.add(new PriorityQueue<>(size, queueValSorter));
            for (int j = 0; j < size; j++) {
                rowQueueArr.get(i).add(button[i][j]);
            }
        }
    }
    //**********************************************************/

    /*
     * Create a column queue
     */
    public void createColQueue(){
        for (int j = 0; j < size; j++) {
            colQueueArr.add(new PriorityQueue<>(size, queueValSorter));
            for (int i = 0; i < size; i++) {
                colQueueArr.get(j).add(button[i][j]);
            }
        }
    }
    //**********************************************************/

    /*
     * Create a queue for the diagonals
     */
    public void createDiagQueue(){
        for (int i = 0; i < 2; i++) {
            diagQueueArr.add(new PriorityQueue<>(size, queueValSorter)); // 0 is equal diag, 1 is unequal
        }
        // equal diagonal
        for (int i = 0; i < size; i++) {
            diagQueueArr.get(0).add(button[i][i]);
        }
        // unequal diagonal
        for (int i = 0; i < size; i++) {
            diagQueueArr.get(1).add(button[i][size - 1 - i]);
        }
    }
    //**********************************************************/

    /*
     * Make sure the queue is rearranged after the queue value changes. It doesn't happen automatically.
     */
    public void heapifyRowQueue(int i) {
        if (!rowQueueArr.get(i).isEmpty()) {
            rowQueueArr.get(i).add(rowQueueArr.get(i).remove());
        }      
    }
    //**********************************************************/

    /*
     * Make sure the queue is rearranged after the column queue values are changed.
     */
    public void heapifyColQueue(int j) {
        if (!colQueueArr.get(j).isEmpty()) {
            colQueueArr.get(j).add(colQueueArr.get(j).remove());
        }
    }
    //**********************************************************/

    /*
     * Rearrange the diagonals as their queue values are changed
     */
    public void heapifyDiagQueue(int i, int j){
        if (i == j) {
            if (!diagQueueArr.get(0).isEmpty()) {
                diagQueueArr.get(0).add(diagQueueArr.get(0).remove());
            }
        }
        if (i+j == size - 1) {
            if (!diagQueueArr.get(1).isEmpty()) {
                diagQueueArr.get(1).add(diagQueueArr.get(1).remove());
            }
        }
    }
    //**********************************************************/

    /*
     * Record the info associated with the clicked buttons
     */
    public void setClickedButton(int row, int col, String marker){
        clickedRow = row;
        clickedCol = col;
        clickedMarker = marker;
        button[clickedRow][clickedCol].setClickedStatus(true);
    }
    //**********************************************************/

    /*
     * Return the clicked row
     */
    public int getClickedRow(){
        return clickedRow;
    }
    //**********************************************************/

    /*
     * Return the clicked column
     */
    public int getClickedCol(){
        return clickedCol;
    }
    //**********************************************************/

    /*
     * Return the clicked marker
     */
    public String getClickedMarker(){
        return clickedMarker;
    }

    /*
     * Record information on how many markers each row, column or diagonal
     * has.
     */
    public void setFillFactor(int i, int j){
        ++rowFillFactor[i];
        ++colFillFactor[j];
        if (i == j) {
            ++diagFillFactor[0];
        }
        if (i + j == size - 1) {
            ++diagFillFactor[1];
        }  

        // an occupied button gets a queue value of 1, if unoccupied the queue value is zero
        button[i][j].setQueueVal(1);
        if (rowQueueArr.isEmpty()) {
            createRowQueue();
        }
        heapifyRowQueue(i); // rearrange the queue so that the button with smallest queue 
        if (colQueueArr.isEmpty()) { // value is always at the beginning of the queue
            createColQueue();
        }
        heapifyColQueue(j);
        if (diagQueueArr.isEmpty()) {
            createDiagQueue();
        }
        heapifyDiagQueue(i, j);
    }
    //**********************************************************/

    /*
     * Get terminal move row
     */
    public int getTerminalRow(){
        return terminal_i;
    }
    //**********************************************************/

    /*
     * Get terminal move column
     */
    public int getTerminalCol(){
        return terminal_j;
    }
    //**********************************************************/

    /*
     * Check if immediate terminal moves exist and add them to 
     * an immediate terminal move list.
     */
    public void immedTerminalMoves(int i, int j){
        if (rowFillFactor[i] == size - 1 && rowParity[i] == size - 1) { // if fill factor and parity array reach size - 1
            immedTerminalMovList.add(rowQueueArr.get(i).peek());// a terminal move exists, at the beginning of the queue
        }
        if (colFillFactor[j] == size - 1 && colParity[j] == size - 1) {
            immedTerminalMovList.add(colQueueArr.get(j).peek());
        }
        if (diagFillFactor[0] ==  size - 1 && diagParity[0] == size - 1) {
            immedTerminalMovList.add(diagQueueArr.get(0).peek());
        }
        if (diagFillFactor[1] == size - 1 && diagParity[1] == size - 1) {
            immedTerminalMovList.add(diagQueueArr.get(1).peek());
        }
    }
    //**********************************************************/

    /*
     * Return the button of the available game ending move
     */
    public Button availableTerminalMove(){
        if (!immedTerminalMovList.isEmpty()) { // if a terminal move exists
            for (Button button : immedTerminalMovList) {
                if (!button.visited()) {// and it's not already made
                    return button; // return it
                }
            }
        }
        return null;
    }
    //**********************************************************/

    /*
     * Return true if the next move is a game ending move 
     */
    public boolean isNextMoveTerminal(int i, int j){
        if (rowFillFactor[i] == size - 1 && rowParity[i] == size - 1) {
            terminal_i = rowQueueArr.get(i).peek().getRow();
            terminal_j = rowQueueArr.get(i).peek().getCol();
            rowTerminalNext = true;
            return true;
        }
        if (colFillFactor[j] == size - 1 && colParity[j] == size - 1) {
            terminal_i = colQueueArr.get(j).peek().getRow();
            terminal_j = colQueueArr.get(j).peek().getCol();
            colTerminalNext = true;
            return true;
        }
        if (diagFillFactor[0] ==  size - 1 && diagParity[0] == size - 1) {
            terminal_i = diagQueueArr.get(0).peek().getRow();
            terminal_j = diagQueueArr.get(0).peek().getCol();
            eqDiagTerminalNext = true;
            return true;
        }
        if (diagFillFactor[1] == size - 1 && diagParity[1] == size - 1) {
            terminal_i = diagQueueArr.get(1).peek().getRow();
            terminal_j = diagQueueArr.get(1).peek().getCol();
            unEqDiagTerminalNext = true;
            return true;
        }
        return false;
    }
    //**********************************************************/

    /*
     * Return true if the present move is a game ending move
     */
    public boolean isPresentMoveTerminal() {

        //*********************************************************************************** */
        if (rowColTerminalNow) {
            return true;
        } 
        if (eqDiagTerminalNow) {
            return true;
        } 
        if (unEqDiagTerminalNow) {
            return true;  
        } 
        //*********************************************************************** */
        if (totalMoveNumber == size * size) {
            isDraw = true;
            return true;
        }
        return false;
    }
    //**********************************************************/

    /*
     * Record play data, the markers and the locations they occupy.
     */
    public void recordPlayData(int i, int j, String marker){
        button[i][j].setVisitStatus(true);
        button[i][j].setMarker(marker);
        checkRowCol(rowMap, rowParity, i, marker);
        checkRowCol(colMap, colParity, j, marker);
        checkDiagonal(i, j, marker);
        setFillFactor(i, j);
    }

    //*********************************************************************************************/
    //*********************************************************************************************/
    // store all current data

    /*
     * Store the total move number
     */
    public void storeTotalMoveNum(){
        storedTotalMoveNum = totalMoveNumber;
    }
    //**********************************************************/

    /*
     * Store the parity array values
     */
    public void storeParityVals(){
        for (int i = 0; i < size; i++) {
            storedRowParity[i] = rowParity[i];
            storedColParity[i] = colParity[i];
        }
        for (int i = 0; i < 2; i++) {
            storedDiagParity[i] = diagParity[i];
        }
    }
    //**********************************************************/

    /*
     * Store the map information
     */
    public void storeMapVals(){
        for (Map.Entry<Integer,String> entry : rowMap.entrySet()) {
            storedRowMap.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Integer,String> entry : colMap.entrySet()) {
            storedColMap.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Integer,String> entry : dMap.entrySet()) {
            storedDMap.put(entry.getKey(), entry.getValue());
        }
    }
    //**********************************************************/

    /*
     * Store the fill factor values.
     */
    public void storeFillFactorVals(){
        for (int i = 0; i < size; i++) {
            storedRowFillFactor[i] = rowFillFactor[i];
            storedColFillFactor[i] = colFillFactor[i];
        }
        for (int i = 0; i < 2; i++) {
            storedDiagFillFactor[i] = diagFillFactor[i];
        }
    }
    //**********************************************************/

    /*
     * Store the queue values.
     */
    public void storeQueueVals(){
        int index = 0;
        for (Button button : buttonList) {
            storedQueueValsArr[index] = button.getQueueVal();
            index++;
        }
    }
    //**********************************************************/

    /*
     * Store the visited information.
     */
    public void storeVisitedInfo(){
        int index = 0;
        for (Button buttons : buttonList) {
            if (buttons.visited()) {
                storedVisitedInfoArr[index] = 1;
            }
            index++;
        }
    }
    //**********************************************************/

    /*
     * Store the present state as it exists on the board.
     */
    public void storePresentState(){
        storeTotalMoveNum();
        storeParityVals();
        storeMapVals();
        storeFillFactorVals();
        storeQueueVals();
        storeVisitedInfo();
    }    
    //************************************************************************************** */

    /*
     * Clear all play data
     */
    public void clearPlayData(){         
        
        rowMap.clear();
        colMap.clear();
        dMap.clear();

        Arrays.fill(rowParity, 0);
        Arrays.fill(colParity, 0);
        Arrays.fill(diagParity, 0);

        isDraw = false;
        winMarker = Game.Empty;
        totalMoveNumber = 0;

        immedTerminalMovList.clear();

        Arrays.fill(rowFillFactor, 0);
        Arrays.fill(colFillFactor, 0);
        Arrays.fill(diagFillFactor, 0);

        rowTerminalNext = false;
        colTerminalNext = false;
        eqDiagTerminalNext = false;
        unEqDiagTerminalNext = false;

        rowColTerminalNow = false;
        eqDiagTerminalNow = false;
        unEqDiagTerminalNow = false;

        for (Button buttons : buttonList) {
            buttons.setQueueVal(0);
            buttons.setVisitStatus(false);
            buttons.setMarker(Game.Empty);
        }

    }

    //********************************************************************************************/
    //*********************************************************************************************/ 
    
    // restore current state

    /*
     * Restore the total move number.
     */
    public void restoreTotalMoveNum(){
        totalMoveNumber = storedTotalMoveNum;
    }
    //**********************************************************/

    /*
     * Restore the parity array values
     */
    public void restoreParityValues(){
        for (int i = 0; i < size; i++) {
            rowParity[i] = storedRowParity[i];
            colParity[i] = storedColParity[i];
        }
        for (int i = 0; i < 2; i++) {
            diagParity[i] = storedDiagParity[i];
        }
    }
    //**********************************************************/

    /*
     * Restore the map values.
     */
    public void restoreMapValues(){
        for (Map.Entry<Integer,String> entry : storedRowMap.entrySet()) {
            rowMap.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Integer,String> entry : storedColMap.entrySet()) {
            colMap.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Integer,String> entry : storedDMap.entrySet()) {
            dMap.put(entry.getKey(), entry.getValue());
        }
    }
    //**********************************************************/

    /*
     * Restore the fill factor values
     */
    public void restoreFilledFactorVals(){
        for (int i = 0; i < size; i++) {
            rowFillFactor[i] = storedRowFillFactor[i];
            colFillFactor[i] = storedColFillFactor[i];
        }
        for (int i = 0; i < 2; i++) {
            diagFillFactor[i] = storedDiagFillFactor[i];
        }
    }
    //**********************************************************/

    /*
     * Restore the queue values.
     */
    public void restoreQueueVals(){
        for (int i = 0; i < storedQueueValsArr.length; i++) {
            int row = buttonList.get(i).getRow();
            int col = buttonList.get(i).getCol();
            int qVal = storedQueueValsArr[i];
            button[row][col].setQueueVal(qVal);
        
            heapifyRowQueue(row); // heapify so that the buttons are 
                                  // in their proper positions  
            heapifyColQueue(col);
            
            heapifyDiagQueue(row, col);
        }
    }
    //**********************************************************/

    /*
     * Restore visit status info.
     */
    public void restoreVisitStatus(){
        for (int i = 0; i < storedVisitedInfoArr.length; i++) {
            if (storedVisitedInfoArr[i] == 1) {
                buttonList.get(i).setVisitStatus(true);
            }
        }
    }
    //**********************************************************/

    /*
     * Restore the original state
     */
    public void restoreOriginalState(){
        restoreTotalMoveNum();
        restoreParityValues();
        restoreMapValues();
        restoreFilledFactorVals();
        restoreQueueVals();
        restoreVisitStatus();
    }

    //****************************************************************************** */

    /*
     * Clear all stored data
     */
    public void clearStoredData(){

        storedTotalMoveNum = 0;

        Arrays.fill(storedRowParity, 0);
        Arrays.fill(storedColParity, 0);
        Arrays.fill(storedDiagParity, 0);

        storedRowMap.clear();
        storedColMap.clear();
        storedDMap.clear();

        Arrays.fill(storedRowFillFactor, 0);
        Arrays.fill(storedColFillFactor, 0);
        Arrays.fill(storedDiagFillFactor, 0);

        Arrays.fill(storedQueueValsArr, 0);
        Arrays.fill(storedVisitedInfoArr, 0);
    }
    //**********************************************************/

    /*
     * Clear clicked button info.
     */
    public void clearClickedInfo(){
        for (Button button : buttonList) {
            if (button.clicked()) {
                button.setClickedStatus(false);
            }
        }
    }
    //**********************************************************/

    /*
     * Clear win/loss scores of the buttons.
     */
    public void clearWinScores(){
        for (Button button : buttonList) {
            button.setWinScore(0);
            button.clearUCTScore(0);
            button.setVisitNumber(0);
        }
    }
}