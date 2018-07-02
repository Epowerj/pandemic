public class SimulationGameState extends GameState {

    // copy constructor
    public SimulationGameState(GameState other) {
        super(other);

        playerdeck.reshuffleExistingDeck(this);
    }

    public void copy(GameState other) { // makes copy public on this class
        super.copy(other);
    }

    public void treatDisease(String city, int amount) {
        City targetCity = getCities().get(city);

        //if disease is cured
        if (isDiseaseCured(targetCity.getColor())) {
            targetCity.removeCubes(targetCity.getCubeCount());
        } else {
            targetCity.removeCubes(amount);
        }
    }

    public int simulateUntilWin(){
        //TODO need to be able to switch colors until gone through them all
        int turns =0;
        return turns;
    }

    // launcher method
    public int simulateUntilLoss() {
        return simulateUntilLoss(getPlayers().get(0));
    }

    public int simulateUntilLoss(Player startingPlayer) {
        int turns = 0;
        int player = getPlayerNumber(startingPlayer);

        while (!haveLost()) {
            turns++;

            newTurn(players.get(player), false);

            //switch to the next player
            if (player < players.size() - 1) {
                player++;
            } else {
                player = 0;
            }
        }

        return turns * 4; //convert into actions

        // |   ||
        // ||  |_
    }

    public int getPlayerNumber(Player player) {
        int currentPlayer;
        for (currentPlayer = 0; currentPlayer < getPlayers().size(); currentPlayer++) {
            if (getPlayers().get(currentPlayer) == player) {
                break;
            }
        }

        return currentPlayer;
    }

}

