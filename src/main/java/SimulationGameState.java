public class SimulationGameState extends GameState {

    // copy constructor
    public SimulationGameState(GameState other) {
        super(other);
    }

    public void copy(GameState other) { // makes copy public on this class
        super.copy(other);
    }

    public void treatDisease(String city, int amount) {
        City targetCity = GameState.getCities().get(city);

        //if disease is cured
        if (isDiseaseCured(targetCity.getColor())) {
            targetCity.removeCubes(targetCity.getCubeCount());
        } else {
            targetCity.removeCubes(amount);
        }
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
