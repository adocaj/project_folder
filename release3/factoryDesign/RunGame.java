
public class RunGame {
    public static void main(String[] args) {
        GameFactory gFactory = new GameFactory();
        UserInteraction userInteraction;
        userInteraction = (UserInteraction)gFactory.getGameType(ObjectChoice.userInteractionObject);
        userInteraction.board.setUserInteraction(userInteraction);
        userInteraction.buildFrame();
    }

}