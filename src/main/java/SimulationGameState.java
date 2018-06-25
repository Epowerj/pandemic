public class SimulationGameState extends GameState {

    // copy constructor
    public SimulationGameState(GameState other) {
        super(other);
    }

    public void copy(GameState other) { // makes copy public on this class
        super.copy(other);
    }

    public int simulateUntilLoss() {
        int turns = 0;
        int player = 0;

        while (!haveLost()) {
            turns++;

            newTurn(players.get(player));

            //switch to the next player
            if (player < players.size()) {
                player++;
            } else {
                player = 0;
            }
        }

        return turns;
    }
}
