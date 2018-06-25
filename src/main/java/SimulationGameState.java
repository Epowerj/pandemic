public class SimulationGameState extends GameState {

    // copy constructor
    public SimulationGameState(GameState other) {
        super(other);
    }

    public void copy(GameState other) { // makes copy public on this class
        super.copy(other);
    }
}
