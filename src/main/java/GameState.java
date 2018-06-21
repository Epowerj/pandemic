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
    private static InfectionDeck infectiondeck = new InfectionDeck();
    private static int outbreak = 0;
    private static ArrayList<String> explodedCites = new ArrayList<>();
    ArrayList<Player> players = new ArrayList<>();
    private int infectionrateindex = 0;
    private int[] infectionrates = new int[]{2, 2, 2, 3, 3, 4, 4};
    public final int epidemicDifficulty = 4;
    private boolean haveLost = false;
    private int epochSize;
    private int epochOverflow;

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

    public static void addToExplodedCities(String city) {
        explodedCites.add(city);
    }

    public static boolean isCityExploded(String city) {
        for (int i = 0; i < explodedCites.size(); i++) {
            if (explodedCites.get(i).equals(city.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    public static void clearExplodedCities() {
        explodedCites.clear();
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
        addPlayer(Player.Role.PLANNER);
        //addPlayer(Player.Role.MEDIC);
        //addPlayer(Player.Role.SCIENTIST);

        dealCards(); // deal cards to players

        setupInfectedCities(); // distribute cubes

        // shuffle in epidemic cards AFTER dealing cards to players
        playerdeck.shuffle();
        epochSize = playerdeck.deckSize() / epidemicDifficulty;
        epochOverflow = playerdeck.deckSize() - epochSize * (epidemicDifficulty - 1);
        // add the first epidemic card
        Random r = new Random();
        int random = r.nextInt(epochOverflow); // random number from 0 to epochOverflow
        playerdeck.insert(new EpidemicCard(), random); // insert into player deck
        // for each epidemic except the first one
        for (int i = 0; i < epidemicDifficulty - 1; i++) {
            random = r.nextInt(epochSize); // random number from 0 to epochSize
            random += i * epochSize + epochOverflow; // epoch offset
            playerdeck.insert(new EpidemicCard(), random); // insert into player deck
        }

        stations.add("atlanta"); // add research station
    }

    public void addPlayer(Player.Role role) {
        Player newplayer = new Player(role);

        players.add(newplayer);
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

    public int getOutbreak() {
        return outbreak;
    }

    public int getPlayerDeckSize() {
        return playerdeck.deckSize();
    }

    public int getInfectionSize() {
        return infectiondeck.deckSize();
    }

    public int getPlayerDiscard(){ return playerdeck.discardSize(); }

    //public int getInfectionDiscardSize() { return infectiondeck.discardSize(); }

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

        if (playerdeck.deckSize() < 2) { //if we can't draw cards, we've lost
            haveLost = true;
        }

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

                infectiondeck.shuffleBack();
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

        updateEradicated();
    }

    private void updateEradicated() {
        HashMap<String, Integer> cubeCounts = getCubeCounts();
        for (Map.Entry<String, Integer> entry : cubeCounts.entrySet()) {
            String color = entry.getKey();
            int count = entry.getValue();

            if (count == 0) {
                if (color.equals("B")) {
                    blueEradicated = true;
                }

                if (color.equals("U")) {
                    blackEradicated = true;
                }

                if (color.equals("R")) {
                    redEradicated = true;
                }

                if (color.equals("Y")) {
                    yellowEradicated = true;
                }
            }
        }
    }

    public boolean haveLost() {
        haveLost = (outbreak >= 8); // if 8 or more outbreaks

        if (!haveLost) { //if we've already lost, don't do anything

            HashMap<String, Integer> cCount = new HashMap<>();

            HashMap<String, Integer> cubeCounts = getCubeCounts();
            for (Map.Entry<String, Integer> entry : cubeCounts.entrySet()) {
                String color = entry.getKey();
                int count = entry.getValue();

                if (count >= 24) {
                    haveLost = true;
                }
            }
        }

        return haveLost;
    }

    public HashMap<String, Integer> getCubeCounts() {
        HashMap<String, Integer> result = new HashMap<>();
        result.put("U", 0);
        result.put("Y", 0);
        result.put("B", 0);
        result.put("R", 0);

        for (Map.Entry<String, City> entry : getCities().entrySet()) {
            String name = entry.getKey();
            City city = entry.getValue();

            HashMap<String, Integer> colors = city.getCubeList();

            for (Map.Entry<String, Integer> entry2 : colors.entrySet()) {
                String color = entry2.getKey();
                int count = entry2.getValue();

                result.put(color, result.get(color) + count);
            }
        }

        return result;
    }

    public int getInfectionRate() {
        return infectionrates[infectionrateindex];
    }

    public boolean isInInfectionDeck(String target) {
        return infectiondeck.isInDeck(target);
    }

    public boolean isInfectionDiscard(String target) {
        return infectiondeck.isInDiscard(target);
    }

    public InfectionDeck getInfectiondeck() {
        return infectiondeck;
    }

    public HashMap<String,Integer> getPlayerColorCount(){
        ArrayList<Card> playerdiscard = playerdeck.getDiscard();
        HashMap<String,Integer> colorcount = new HashMap<>();
        colorcount.put("U", 0);
        colorcount.put("B", 0);
        colorcount.put("R", 0);
        colorcount.put("Y", 0);

        for (Card c : playerdiscard){
            if (c.cardtype == Card.CardType.PLAYER) {
                PlayerCard pcard = (PlayerCard) c;
                if (pcard.getColor().equals("U")) {
                    colorcount.put("U", colorcount.get("U") + 1);
                } else if (pcard.getColor().equals("B")) {
                    colorcount.put("B", colorcount.get("B") + 1);
                } else if (pcard.getColor().equals("R")) {
                    colorcount.put("R", colorcount.get("R") + 1);
                } else {
                    colorcount.put("Y", colorcount.get("Y") + 1);
                }
            }
        }
        return colorcount;
    }
}


