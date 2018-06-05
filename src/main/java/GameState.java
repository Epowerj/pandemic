import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class GameState {
    private static HashMap<String, City> nodes = new HashMap<String, City>();
    private static ArrayList<String> stations = new ArrayList<>();
    private static Deck playerdeck = new Deck();
    private static boolean blueCured = false;
    private static boolean blackCured = false;
    private static boolean redCured = false;
    private static boolean yellowCured = false;
    private static boolean blueEradicated = false;
    private static boolean blackEradicated = false;
    private static boolean redEradicated = false;
    private static boolean yellowEradicated = false;
    ArrayList<Player> players = new ArrayList<>();
    private Deck infectiondeck = new Deck();
    private int outbreak = 0;
    private int infectionrateindex = 0;
    private int[] infectionrates = new int[]{2, 2, 2, 3, 3, 4, 4};

    //constructor
    public GameState(String info_file) {
        parseInfo(info_file);
    }

    public static void placeResearchStation(String targetCity) {
        if (!stations.contains(targetCity)) { //if there isn't a station already there
            stations.add(targetCity);
        }
    }

    public static boolean cityHasResearchStation(String target) {
        return stations.contains(target);
    }

    public static HashMap<String, City> getCities() {
        return nodes;
    }

    public static void discardPlayerCard(PlayerCard toDiscard) {
        playerdeck.pushToDiscard(toDiscard);
    }

    public static boolean isDiseaseCured(String color) {
        if (color.equals("B")) {
            return blueCured;
        } else if (color.equals("R")) {
            return redCured;
        } else if (color.equals("Y")) {
            return yellowCured;
        } else if (color.equals("U")) {
            return blackCured;
        } else {
            return false;
        }
    }

    public static boolean isDiseaseEradicated(String color) {
        if (color.equals("B")) {
            return blueEradicated;
        } else if (color.equals("R")) {
            return redEradicated;
        } else if (color.equals("Y")) {
            return yellowEradicated;
        } else if (color.equals("U")) {
            return blackEradicated;
        } else {
            return false;
        }
    }

    public static void setCured(String color) {
        if (color.equals("B")) {
            blueCured = true;
        } else if (color.equals("R")) {
            redCured = true;
        } else if (color.equals("Y")) {
            yellowCured = true;
        } else if (color.equals("U")) {
            blackCured = true;
        }
    }

    public static Deck getPlayerDeck() {
        return playerdeck;
    }

    public static ArrayList<String> getStations() {
        return stations;
    }

    //add city to the list
    private void addNode(City node) {
        nodes.put(node.getName(), node);
    }

    //read all the cities and other board info from text file
    private void parseInfo(String filename) {

        List<String> lines = null;

        try {
            Scanner input = new Scanner(new FileInputStream(new File(filename)));
            lines = Files.readAllLines(Paths.get(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //save all the cities
        //and add their cards
        for (int i = 0; i < lines.size(); i++) {
            //if this line isn't empty
            if (lines.get(i).length() > 2) {
                List<String> words = new ArrayList<String>(Arrays.asList(lines.get(i).split(" ")));

                String color = words.get(0);
                String cityName = words.get(1).toLowerCase();

                City newCity = new City(cityName, color);
                addNode(newCity);

                //add cards for this city
                infectiondeck.push(new InfectionCard(cityName, color));
                playerdeck.push(new PlayerCard(cityName, color));
            }
        }

        //now connect all the cities
        for (int i = 0; i < lines.size(); i++) {
            //if this line isn't empty
            if (lines.get(i).length() > 2) {
                List<String> words = new ArrayList<String>(Arrays.asList(lines.get(i).split(" ")));

                String cityName = words.get(1).toLowerCase();
                City toUpdate = nodes.get(cityName);

                //for each adjacent city
                for (int j = 2; j < words.size(); j++) {
                    String adjCityName = words.get(j).toLowerCase();

                    //make sure that we didn't miss this adjacent city when parsing
                    assert (nodes.containsKey(adjCityName));

                    toUpdate.addAdjacent(adjCityName);
                }

                //DEBUG-START
                System.out.print("The city " + toUpdate.getName() + " is adjacent to: ");
                for (String adj : toUpdate.getAdjacent()) {
                    System.out.print(adj + ", ");
                }
                System.out.print("\n");
                //DEBUG-END
            }
        }

        infectiondeck.shuffle();
        playerdeck.shuffle();
    }

    public void gameSetup() {
        //create players
        addPlayer(Player.Role.DISPATCHER);
        addPlayer(Player.Role.MEDIC);
        //addPlayer(Player.Role.PLANNER);
        //addPlayer(Player.Role.SCIENTIST);

        dealCards();

        setupInfectedCities(); //distribute cubes

        //shuffle in epidemic cards AFTER dealing cards to players
        infectiondeck.push(new EpidemicCard());
        infectiondeck.push(new EpidemicCard());
        infectiondeck.push(new EpidemicCard());
        infectiondeck.push(new EpidemicCard());
        infectiondeck.push(new EpidemicCard());
        infectiondeck.push(new EpidemicCard());
        infectiondeck.shuffle();

        stations.add("atlanta"); //add research station
    }

    public void addPlayer(Player.Role role) {
        Player newplayer = new Player(role);

        players.add(newplayer);
    }

    public void dealCards() {
        int cardstodeal = 0;
        int playercount = players.size();
        if (playercount == 2) {
            cardstodeal = 4;
        } else if (playercount == 3) {
            cardstodeal = 3;

        } else if (playercount == 4) {
            cardstodeal = 2;
        } else {
            System.out.println("Bad number of players");
            assert (false);
        }

        for (Player player : players) { //for each player
            for (int i = 0; i < cardstodeal; i++) { //loop cardstodeal amount of times
                player.drawCard(playerdeck);
            }
        }
    }

    public void setupInfectedCities() {
        for (int i = 0; i < 3; i++) {
            InfectionCard card = (InfectionCard) infectiondeck.draw();
            City city = nodes.get(card.getCity());
            city.setCubeCount(3);
        }
        for (int i = 0; i < 3; i++) {
            InfectionCard card = (InfectionCard) infectiondeck.draw();
            City city = nodes.get(card.getCity());
            city.setCubeCount(2);
        }
        for (int i = 0; i < 3; i++) {
            InfectionCard card = (InfectionCard) infectiondeck.draw();
            City city = nodes.get(card.getCity());
            city.setCubeCount(1);
        }
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    //everything that needs to be done at the end of each turn
    //draw infection cards, put new cubes, handle epidemic cards
    public void newTurn() {


        int amountCards = infectionrates[infectionrateindex];

        for (int i = 0; i < amountCards; i++) {
            Card card = infectiondeck.draw();

            if (card.getCardType() == Card.CardType.EPIDEMIC) {

                //increase the infection rate
                if (infectionrateindex < 6) {
                    infectionrateindex++;
                }

                //add 3 cubes
                InfectionCard cardToInfect = (InfectionCard) infectiondeck.getBottomNormalCard();
                nodes.get(cardToInfect.getCity()).addCubes(3);
                infectiondeck.pushToDiscard(cardToInfect);

                infectiondeck.shuffeBack();

            } else { //if normal infection card
                InfectionCard infcard = (InfectionCard) card;

                if (!isDiseaseEradicated(infcard.getColor())) {
                    City city = nodes.get(infcard.getCity());

                    city.incrementCubes(); //put new cube
                }
            }
        }
    }

    public int getOutbreak() {
        return outbreak;
    }

    public int getInfectionrateindex() {
        return infectionrateindex;
    }

}
