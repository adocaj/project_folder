
/**
 * This program allows the user to play the game of Tic Tac Toe with a computer opponent which
 * uses monte carlo techniques and upper confidence bounds to determine its moves. RunGame is the 
 * driver behind the whole program.
 * @version: 04/24/2020
 * @author: Andris Docaj
 */

public class RunGame {
    public static void main(String[] args) {
        GameFactory gFactory = new GameFactory();
        UserInteraction userInteraction;
        userInteraction = (UserInteraction)gFactory.getGameType(ObjectChoice.userInteractionObject);
        userInteraction.board.setUserInteraction(userInteraction);
        userInteraction.buildFrame();
    }

}