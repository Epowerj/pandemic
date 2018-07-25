import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// represents a city/node on the board
public class City {
    private String name;
    private String color; // the color of the city

    // list of cubes that this node has
    // color string -> cube count
    private HashMap<String, Integer> cubes = new HashMap<String, Integer>();

    // list of cities' names that are adjacent to this one
    private ArrayList<String> adjacent = new ArrayList<String>();

    public City(String cty, String clr) {
        name = cty;
        color = clr;

        cubes.put("U", 0);
        cubes.put("B", 0);
        cubes.put("R", 0);
        cubes.put("Y", 0);
    }

    // copy constructor
    public City(City other) {
        name = other.getName();
        color = other.getColor();

        cubes.put("U", other.getCubeCount("U"));
        cubes.put("B", other.getCubeCount("B"));
        cubes.put("R", other.getCubeCount("R"));
        cubes.put("Y", other.getCubeCount("Y"));

        adjacent = other.getAdjacent();
    }

    public void addAdjacent(String adjcity) {
        adjacent.add(adjcity);
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getAdjacent() {
        //return a copy of adjacent
        return new ArrayList<String>(adjacent); //copy constructor
    }

    public String getColor() {
        return color;
    }

    public int getCubeCount(String color) {
        return cubes.get(color);
    }

    public int getCubeCount() {
        return getCubeCount(this.color);
    }

    public void setCubeCount(int count) {
        setCubeCount(count, this.color);
    }

    public HashMap<String, Integer> getCubeList() {
        return cubes;
    }

    // only for setup
    public void setCubeCount(int count, String color) {
        cubes.put(color, count);
    }

    // launcher for the real increment cubes method
    public void incrementCubes(GameState gameState) {
        incrementCubes(this.color, gameState);
    }

    // launcher for the real increment cubes method
    public void incrementCubes(String color, GameState gameState) {
        incrementCubes(color, gameState, false);
    }

    // this is the real increment cubes method
    // handles outbreaks
    public void incrementCubes(String color, GameState gameState, boolean recursing) {
        cubes.put(color, cubes.get(color) + 1); // increment cubes

        //check if there are more than 3 cubes there
        for (Map.Entry<String, Integer> entry : cubes.entrySet()) { //per color

            String col = entry.getKey();
            int value = entry.getValue();

            if (value > 3) { // if there are more than 2 cubes
                cubes.put(col, 3); //put back to 3
                gameState.incrementOutbreaks(); // we have an outbreak

                // save this city so that it doesn't explode again this turn
                // causes infinite recursion otherwise
                gameState.addToExplodedCities(this.getName());

                //infect adjacent cities
                HashMap<String, City> cities = gameState.getCities();
                for (String cityname : adjacent) {
                    //if city hasn't already exploded
                    if (!gameState.isCityExploded(cityname)) {
                        cities.get(cityname).incrementCubes(col, gameState, true);
                    }
                }

                if (!recursing) {
                    //stack should have returned back so it's okay to clear
                    gameState.clearExplodedCities();
                }
            }
        }
    }

    public void addCubes(int count, String color, GameState gameState) {
        for (int i = 0; i < count; i++) {
            incrementCubes(color, gameState);
        }
    }

    // launcher for the real method
    public void addCubes(int count, GameState gameState) {
        addCubes(count, this.color, gameState);
    }

    public void removeCubes(int amount, String color) {
        cubes.put(color, cubes.get(color) - amount);

        if (cubes.get(color) < 0) {
            cubes.put(color, 0);
        }
    }

    public void removeCubes(int amount) {
        removeCubes(amount, this.color);
    }

    public boolean isAdjacent(String icity) {
        return adjacent.contains(icity.toLowerCase());
    }
}
