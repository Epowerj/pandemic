import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ComputerPlayer {
    private GameState gamestate;
    private Player player;

    private int timeToWin;
    private int timeToLose;

    public ComputerPlayer(GameState gamestatelink, Player tocontrol) {
        gamestate = gamestatelink;
        player = tocontrol;
    }

    //should do move and returns a description of the move
    //TODO currently just prints
    public String doMove() {

        // calculate TTW and TTL
        calculateTime();

        // do all possible moves (abstracted)
        ArrayList<Plan> plans = simulateMoves();

        // pick the best from plans
        //TODO doesn't take into account real TTW and TTL
        Plan currentBest = null;
        for (Plan plan : plans) {
            if (currentBest == null || plan.getDeltaValue() > currentBest.getDeltaValue()) {
                currentBest = plan;
            }
        }

        String toReturn = "The best plan is:\n  " + currentBest.getDescription();

        return toReturn;
    }

    private void calculateTime() {
        calculateTTW();
        calculateTTL();
    }

    private int calculateTTW() {
        //average of predictions
        //average time to have 5 of one color in the hand
        //avergae turns to move and exchange a card
        //last step avg time to get to a research station
        //repeat this 4 times for each cure to get number to win based on actions
        HashMap<String,Double> playerpredictions= new HashMap<>();
        timeToWin = 50;
        HashMap<String,Double> predictions = gamestate.predictPlayer();

        return timeToWin;
    }

    private int calculateTTL() {

        // old
        //int cardsTTL = playerCardsTTL();
        // calculate out of cubes TTL
        // calculate 8 outbreaks TTL
        // return the smallest

        // simulate a bunch of times and average the number
        SimulationGameState sim;
        ArrayList<Integer> toAverage = new ArrayList<>();

        for (int i = 0; i < 2; i++) { // simulate many times
            sim = new SimulationGameState(gamestate);
            toAverage.add(sim.simulateUntilLoss());
        }

        // average
        int simTTL = 0;
        for (int i : toAverage) {
            simTTL += i;
        }
        simTTL = simTTL / toAverage.size();

        timeToLose = simTTL;

        return timeToLose;
    }

    private int playerCardsTTL() {
        return gamestate.getPlayerDeck().deckSize() * 2; // how many actions till we run out of player cards
    }

    //simulate all possible moves
    private ArrayList<Plan> simulateMoves() {
        //possible moves:

        // treat
        ArrayList<Plan> treatPlans = simulateTreat();

        // discover (+ trade cards)
        //TODO ArrayList<Plan> discoverPlans = simulateDiscover();

        //TODO build research station (skipping)

        ArrayList<Plan> plans = new ArrayList<>();
        plans.addAll(treatPlans);
        //plans.addAll(discoverPlans);

        return plans;
    }

    private ArrayList<Plan> simulateTreat() {
        //have to list all the possible POIs and go to them

        HashMap<String, City> cities = gamestate.getCities();
        HashMap<String, Integer> allPOIs = new HashMap<>(); //list of nodes that have cubes

        //build the list of cities that have cubes
        for (Map.Entry<String, City> entry : cities.entrySet()) {
            String cityName = entry.getKey();
            City city = entry.getValue();

            if (city.getCubeCount() > 0) {
                allPOIs.put(cityName, 0);
            }
        }

        ArrayList<Plan> plans = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : allPOIs.entrySet()) {
            String cityName = entry.getKey();
            int TTLDelta = entry.getValue(); // is 0

            //find the turns needed to get there (decrease in TTL)
            ArrayList<String> path = player.goNormal(player.getCurrentCity(), cityName, gamestate);

            //find how it changes the TTL (increase in TTL)
            int cubeCount = cities.get(cityName).getCubeCount(); //TODO cube count is only for the default color

            if (!gamestate.isDiseaseCured(cities.get(cityName).getColor())) { // if that color isn't cured
                //for all possible cubes removed
                for (int i = 0; i < cubeCount; i++) {

                    SimulationGameState sim;
                    ArrayList<Integer> toAverage = new ArrayList<>();

                    for (int h = 0; h < 25; h++) { // simulate many times
                        sim = new SimulationGameState(gamestate);
                        sim.treatDisease(cityName, cubeCount);
                        toAverage.add(sim.simulateUntilLoss());
                    }

                    // average
                    int newTTL = 0;
                    for (int k : toAverage) {
                        newTTL += k;
                    }
                    newTTL = newTTL / toAverage.size();

                    TTLDelta = (newTTL - timeToLose) - path.size();

                    plans.add(new Plan("Treat cubes at " + cityName, 0, TTLDelta, path));
                }
            } else { // otherwise, that color is cured

                // don't need to loop
                SimulationGameState sim;
                ArrayList<Integer> toAverage = new ArrayList<>();

                for (int h = 0; h < 25; h++) { // simulate many times
                    sim = new SimulationGameState(gamestate);
                    sim.treatDisease(cityName, 3);
                    toAverage.add(sim.simulateUntilLoss());
                }

                // average
                int newTTL = 0;
                for (int k : toAverage) {
                    newTTL += k;
                }
                newTTL = newTTL / toAverage.size();

                TTLDelta = (newTTL - timeToLose) - path.size();

                plans.add(new Plan("Treat cubes at " + cityName, 0, TTLDelta, path));
            }
        }

        return plans;
    }

    private ArrayList<Plan> simulateDiscover() {
        ArrayList<Plan> plans = new ArrayList<>();

        // if have 5 cards of the same color, can try going for cure

        // check if player has the right cards for curing
        HashMap<String, Integer> cardCount = player.colorCount();
        for (Map.Entry<String, Integer> entry : cardCount.entrySet()) {
            String color = entry.getKey();
            int count = entry.getValue();

            if (count >= 5) {
                //TODO try making a plan to cure this and add it to plans
                ArrayList<String> path = player.pathToClosestStation(gamestate);
                String destination = path.get(path.size() - 1);

                Plan curePlan = new Plan(("Drive to " + destination + " and cure " + color), -20, -(path.size() + 1), path);
                plans.add(curePlan);
            }
        }

        // can always try trading cards??
        //TODO do trades and add generated plans

        return plans;
    }

    public int getTimeToLose() {
        calculateTTL();
        return timeToLose;
    }

    //TODO simulate an exchange and a meet


}
