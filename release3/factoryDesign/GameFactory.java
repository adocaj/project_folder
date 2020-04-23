public class GameFactory {

    public Game getGameType(ObjectChoice oChoice){

        Game gameObject = null;
        switch (oChoice) {
            case boardObject:
                gameObject = new Board(Game.size);
                break;
            case buttonObject:
                gameObject = new Button();
                break;
            case stateObject:
                gameObject = new State(Game.size);
                break;
            case mSearchObject:
                gameObject = new MonteCarloSearch(Game.size);   
                break;
            case userInteractionObject:
                gameObject = new UserInteraction();
                break;         
            default:
                break;
        }
        return gameObject;
    }
}