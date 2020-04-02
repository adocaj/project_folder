public class Button {

    //* Fields
    //**********************************************************/
    // a Button has a marker value
    private String marker;
    // a Button has a location
    private int row, col;

    // a Button has visited information
    private boolean visited;
    private boolean clickedStatus;

    private int queueVal; // a queue value for easy access

    private int winScore; // a win score
    private int visitNumber; // a visit number value
    private double uctScore; // and a loss score
    //**********************************************************/

    //* An empty button constructor.
    Button(){
        this.row = 0;
        this.col = 0;
        this.marker = ButtonState.Empty;
        this.visited = false;
        this.winScore = 0;
        this.uctScore = 0;
        this.visitNumber = 0;
        this.queueVal = 0;
        this.clickedStatus = false;
    }

    //* A parametrized button constructor.
    Button(int i, int j, String marker) {
        this.row = i;
        this.col = j;
        this.marker = marker;
        this.visited = false;
        this.winScore = 0;
        this.uctScore = 0;
        this.visitNumber = 0;
        this.queueVal = 0;
        this.clickedStatus = false;
    }
    //**********************************************************/

    //* Methods
    
    //**********************************************************/

    /* 
     * Set queue value.
     */
    public void setQueueVal(int queueVal){
        this.queueVal = queueVal;
    }
    //**********************************************************/

    /* 
     * Get queue value.
     */
    public int getQueueVal(){
        return this.queueVal;
    }
    //**********************************************************/

    /* 
     * Set marker symbol.
     */
    public void setMarker(String marker) {
        this.marker = marker;
    }
    //**********************************************************/

    /* 
     * Get marker symbol.
     */
    public String getMarker() {
        return this.marker;
    }
    //**********************************************************/

    /* 
     * Get row, returns an integer representing the row value.
     */
    public int getRow() {
        return this.row;
    }
    //**********************************************************/

    /* 
     * Get Column value.
     */
    public int getCol(){
        return this.col;
    }
    //**********************************************************/

    /* 
     * Set visit status. True if visited, false otherwise.
     */
    public void setVisitStatus(boolean visited) {
        this.visited = visited;
    }
    //**********************************************************/

    /* 
     * Get visited status info.
     */
    public boolean visited() {
        return this.visited;
    }
    //**********************************************************/

    /* 
     * Set clicked status, true if button is clicked, false otherwise.
     */
    public void setClickedStatus(boolean clickedStatus){
        this.clickedStatus = clickedStatus;
    }
    //**********************************************************/

    /* 
     * Get clicked status. 
     */
    public boolean clicked(){
        return this.clickedStatus;
    }
    //**********************************************************/

    /* 
     * Get win score value.
     */
    public int getWinScore(){
        return this.winScore;
    }
    //**********************************************************/

    /* 
     * Set win score value. 
     */
    public void setWinScore(int winScore){
        this.winScore = winScore;
    }
    //**********************************************************/

    /* 
     * Get UCT score value.
     */
    public double getUCTScore(){
        return this.uctScore;
    }
    //**********************************************************/

    /* 
     * Clear UCT score value.
     */
    public void clearUCTScore(double uctScore){
        this.uctScore = uctScore;
    }
    //**********************************************************/

    /* 
     * Set UCT score value.
     */
    public void setUCTScore(double nodeWinNum, double nodeVisitNum, double totalSimulationNum){
        this.uctScore = (nodeWinNum / nodeVisitNum) + Math.sqrt(0.00017 * Math.log(totalSimulationNum) / nodeVisitNum) ;
    }
    //**********************************************************/


    /* 
     * Get visited number value.
     */
    public int getVisitedNumber() {
        return this.visitNumber;
    }
    //**********************************************************/

    /* 
     * Set visited number value.
     */
    public void setVisitNumber(int visitNumber) {
        this.visitNumber = visitNumber;
    }
    //**********************************************************/
}