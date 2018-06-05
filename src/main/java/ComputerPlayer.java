public class ComputerPlayer {
    private GameState gamestate;
    private Player player;

    public ComputerPlayer(GameState gamestatelink, Player tocontrol) {
        gamestate = gamestatelink;
        player = tocontrol;
    }

    public String doMove() {
        //TODO do move and return string description of move

        return "Did nothing";
    }
}
