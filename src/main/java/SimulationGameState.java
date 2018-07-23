
// a subclass of GameState that is used to simulate the game to calculate TTL
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

    // launcher function for the real simulate
    // this is for simulating pure TTL (rather than calculating move costs)
    public int simulateUntilLoss() {
        return simulateUntilLoss(0, 0, 0, null);
    }

    // does the simulation until the game is over
    // startingPlayer is the player number of the player that does the move
    // actionsTillMove is how many actions it will take for the player to reach the destination before the actual move
    // actionsLeftInTurn is how many actions are left in the current turn when the simulation is called
    // move is a function that does the actual move (lambda expression)
    public int simulateUntilLoss(int startingPlayer, int actionsTillMove, int actionsLeftInTurn, Runnable move) {
        int currentPlayer = startingPlayer;

        // save the amount of actions the simulation has done so far
        // do the actions left in the turn first though
        int actions = actionsLeftInTurn;
        actionsTillMove -= actionsLeftInTurn; // the amount of actions left until we do the simulation's move

        boolean haveDoneMove = false;

        // loop until we lose
        while (!haveLost()) {

            newTurn(players.get(currentPlayer), false); // simulate a new turn
            actions += 4; // a turn is 4 actions

            // if the player that is doing the move is the current player
            if (currentPlayer == startingPlayer) {
                actionsTillMove -= 4; // we've gotten closer to when we can do the simulation's move
            }

            // if we're in position to do the move and we've not done it before, do it
            if (actionsTillMove <= 0 && !haveDoneMove) {
                haveDoneMove = true;

                // do the move
                if (move != null) {
                    move.run(); // execute the move
                }
            }

            //switch to the next player
            if (currentPlayer < players.size() - 1) {
                currentPlayer++;
            } else {
                currentPlayer = 0;
            }
        }

        return actions;

        // |   ||
        // ||  |_
    }

}
