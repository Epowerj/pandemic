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

    public String doMove() {
        //TODO do move and return string description of move

        // 1 - calculate TTW and TTL
        calculateTime();
        // 2 - do all possible moves (abstracted)
        simulateMoves();
        // 3 - calculate the difference in TTW and TTL for each move
        // 4 - pick the best move and do it

        return "Did nothing";
    }

    private void calculateTime() {
        calculateTTW();
        calculateTTL();
    }

    private int calculateTTW() {
        timeToWin = 50;

        return timeToWin;
    }

    private int calculateTTL() {
        timeToLose = 50;

        return timeToLose;
    }

    //simulate all possible moves
    private void simulateMoves() {
        //possible moves:

        //move
        //TODO would you even need to just move simulateGo(); ??

        // treat
        ArrayList<Plan> treatPlans = simulateTreat();

        // build research station

        // trade cards

        // discover

        ArrayList<Plan> plans = new ArrayList<>();
        plans.addAll(treatPlans);

        //pick the best from plans
    }

    private ArrayList<Plan> simulateTreat() {
        String current = player.getCurrentCity();

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
            ArrayList<String> path = player.goNormal(player.getCurrentCity(), cityName);

            //find how it changes the TTL (increase in TTL)
            int cubeCount = cities.get(cityName).getCubeCount(); //TODO cube count is only for the default color

            //for all possible cubes removed
            for (int i = 0; i < cubeCount; i++) {
                //TODO create a plan object?
                int positiveTTLDelta;

                if (cubeCount >= 3) {
                    positiveTTLDelta = 2 + i * 4; // temporary: doesn't take into account shuffle backs
                } else {
                    positiveTTLDelta = i * 4;
                }

                TTLDelta = positiveTTLDelta - path.size();

                plans.add(new Plan("Treat cubes at " + cityName, 0, TTLDelta));
            }
        }

        return plans;
    }
}
