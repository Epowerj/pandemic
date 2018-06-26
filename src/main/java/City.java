import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class City {
    private String name;
    private String color;

    private HashMap<String, Integer> cubes = new HashMap<String, Integer>();

    private ArrayList<String> adjacent = new ArrayList<String>();

    public City(String cty, String clr) {
        name = cty;
        color = clr;

        cubes.put("U", 0);
        cubes.put("B", 0);
        cubes.put("R", 0);
        cubes.put("Y", 0);
    }

    //copy constructor
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

    //only for setup
    public void setCubeCount(int count, String color) {
        cubes.put(color, count);
    }

    public void incrementCubes(GameState gameState) {
        incrementCubes(this.color, gameState);
    }

    // launcher for the real increment cubes method
    public void incrementCubes(String color, GameState gameState) {
        incrementCubes(color, gameState, false);
    }

    public void incrementCubes(String color, GameState gameState, boolean recursing) {
        cubes.put(color, cubes.get(color) + 1);

        //check if there are more than 3 cubes there
        for (Map.Entry<String, Integer> entry : cubes.entrySet()) { //per color

            String col = entry.getKey();
            int value = entry.getValue();

            if (value > 3) {
                cubes.put(col, 3); //put back to 3
                gameState.incrementOutbreaks();

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

    public void addCubes(int count, GameState gameState) {
        addCubes(count, this.color, gameState);
    }

    public void removeCubes(int amount, String color) {
        cubes.put(color, cubes.get(color) - 1);

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
