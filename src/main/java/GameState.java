import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class GameState {
    private static HashMap<String, City> nodes = new HashMap<String, City>();
    private static ArrayList<String> stations = new ArrayList<>();
    private static ArrayList<InfectionCard> infectiondiscard = new ArrayList<>();
    private static Deck playerdeck = new Deck();
    private static boolean blueCured = false;
    private static boolean blackCured = false;
    private static boolean redCured = false;
    private static boolean yellowCured = false;
    private static boolean blueEradicated = false;
    private static boolean blackEradicated = false;
    private static boolean redEradicated = false;
    private static boolean yellowEradicated = false;
    private static Deck infectiondeck = new Deck();
    private static int outbreak = 0;
    private static ArrayList<String> explodedCites = new ArrayList<>();
    private int infectionrateindex = 0;
    private int[] infectionrates = new int[]{2, 2, 2, 3, 3, 4, 4};
    ArrayList<Player> players = new ArrayList<>();

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

    public static Deck getInfectionDeck() {
        return infectiondeck;
    }

    public static ArrayList<String> getStations() {
        return stations;
    }

    public static void incrementOutbreaks() {
        outbreak++;
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
        playerdeck.push(new EpidemicCard());
        playerdeck.push(new EpidemicCard());
        playerdeck.push(new EpidemicCard());
        playerdeck.push(new EpidemicCard());
        playerdeck.push(new EpidemicCard());
        playerdeck.push(new EpidemicCard());
        playerdeck.shuffle();

        stations.add("atlanta"); //add research station
    }

    public void addPlayer(Player.Role role) {
        Player newplayer = new Player(role);

        players.add(newplayer);
    }

    public static void addToExplodedCities(String city) {
        explodedCites.add(city);
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

    public static boolean isCityExploded(String city) {
        for (int i = 0; i < explodedCites.size(); i++) {
            if (explodedCites.get(i).equals(city.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    public int getOutbreak() {
        return outbreak;
    }

    public int sizingPlayer(){
       return playerdeck.deckSize();
    }

    public int sizingInfection(){
        return infectiondeck.deckSize();
    }

    public int infectionDiscard(){
        return infectiondeck.discardSize();
    }


    public static void clearExplodedCities() {
        explodedCites.clear();
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
                PlayerCard newCard = (PlayerCard) playerdeck.pop(); //at this point there should be no epidemic cards
                player.addCardToHand(newCard);
            }
        }

        /*for (Player player : players) { //for each player
            for (int i = 0; i < 6; i++) { //loop cardstodeal amount of times
                PlayerCard newCard = (PlayerCard) playerdeck.getCardColor("B"); //at this point there should be no epidemic cards
                player.addCardToHand(newCard);
            }
        }*/
    }

    //everything that needs to be done at the end of each turn
    //draw infection cards, put new cubes, handle epidemic cards
    public void newTurn(Player currentPlayer) {

        //draw player cards and do epidemics
        for (int i = 0; i < 2; i++) { //loop until 2 cards are added to player hand

            Card drawnCard = playerdeck.pop();

            if (drawnCard.getCardType() == Card.CardType.EPIDEMIC) {
                System.out.println("Drew an Epidemic card!!");

                //increase the infection rate
                if (infectionrateindex < 6) {
                    infectionrateindex++;
                }

                //add 3 cubes
                InfectionCard cardToInfect = (InfectionCard) infectiondeck.getBottomNormalCard();
                nodes.get(cardToInfect.getCity()).addCubes(3);
                infectiondeck.pushToDiscard(cardToInfect);

                System.out.println("Added cubes to " + cardToInfect.getCity());

                infectiondeck.shuffeBack();
            } else {// otherwise it must be a normal player card
                currentPlayer.addCardToHand((PlayerCard) drawnCard);

                System.out.println("Player added " + drawnCard.getCardInfoString() + " to their hand");
            }
        }

        //draw infection cards and update cubes
        int amountCards = infectionrates[infectionrateindex]; //get the infection rate

        for (int i = 0; i < amountCards; i++) { //depends on the infection rate
            Card card = infectiondeck.draw();
            System.out.println("Drew an infection card");

            InfectionCard infcard = (InfectionCard) card;

            if (!isDiseaseEradicated(infcard.getColor())) {
                City city = nodes.get(infcard.getCity());

                System.out.println("Added cubes to " + city.getName());
                city.incrementCubes(); //put new cube
            }
        }
    }

    public int getInfectionRate() {
        return infectionrates[infectionrateindex];
    }

    public boolean isInDeck(String target){
         target = target.toLowerCase();
            for (InfectionCard card: infectiondiscard) {
                if (card.getCity().toLowerCase().equals(target)) {
                    return true;
                }
            }
            return false;
    }

    public void congested(){


    }
}


