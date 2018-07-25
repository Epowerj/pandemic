import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class GameState {
    public final int epidemicDifficulty = 4; // amount of epidemic cards in the deck
    protected Deck playerdeck = new Deck();
    protected InfectionDeck infectiondeck = new InfectionDeck();
    protected final int[] infectionrates = new int[]{2, 2, 2, 3, 3, 4, 4}; // list of infection rates
    protected HashMap<String, City> nodes = new HashMap<String, City>(); // list of cities
    private boolean blackCured = false;
    private boolean redCured = false;
    private boolean yellowCured = false;
    private boolean blueEradicated = false;
    private boolean blackEradicated = false;
    private boolean redEradicated = false;
    private boolean yellowEradicated = false;
    protected ArrayList<Player> players = new ArrayList<>(); // list of players
    protected int infectionrateindex = 0; // index for the infection rates
    protected boolean haveLost = false; // is true if we have a game over
    private ArrayList<String> stations = new ArrayList<>(); // list of research stations
    private boolean blueCured = false; // booleans for whether a certain color is cured and eradicated
    private int outbreak = 0; // outbreak count
    private ArrayList<String> explodedCities = new ArrayList<>(); // used for spreading cubes
    private int epochSize; // amount of cards in each epoch of the player deck (not counting epidemics?)
    private int epochOverflow; // amount of cards in the last epoch (only different in certain cases)
    private HashMap<Integer, ComputerPlayer> aiList = new HashMap<>(); // list of computer players

    // constructor
    public GameState(String info_file) {
        parseInfo(info_file);
    }

    // copy constructor
    public GameState(GameState other) {
        copy(other);
    }

    // used for doing outbreaks (in the city class)
    public void addToExplodedCities(String city) {
        explodedCities.add(city);
    }

    public void placeResearchStation(String targetCity) {
        if (!stations.contains(targetCity)) { //if there isn't a station already there
            stations.add(targetCity);
        }
    }

    public boolean cityHasResearchStation(String target) {
        return stations.contains(target);
    }

    public HashMap<String, City> getCities() {
        return nodes;
    }

    public void discardPlayerCard(PlayerCard toDiscard) {
        playerdeck.pushToDiscard(toDiscard);
    }

    public boolean isDiseaseCured(String color) {
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

    public boolean isDiseaseEradicated(String color) {
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

    // color is a color char
    public void setCured(String color) {
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

    public Deck getPlayerDeck() {
        return playerdeck;
    }

    public Deck getInfectionDeck() {
        return infectiondeck;
    }

    public ArrayList<String> getStations() {
        return stations;
    }

    public void incrementOutbreaks() {
        outbreak++;
    }

    // used by the city class for doing outbreaks
    public boolean isCityExploded(String city) {
        for (int i = 0; i < explodedCities.size(); i++) {
            if (explodedCities.get(i).equals(city.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    public void clearExplodedCities() {
        explodedCities.clear();
    }

    // used in the copy constructor
    // sometimes we want to copy without having to create a new gamestate object (java is slow at creating objects)
    protected void copy(GameState other) {
        //copy nodes
        for (Map.Entry<String, City> entry : other.getCities().entrySet()) {
            String cityName = entry.getKey();
            City city = entry.getValue();

            City copyCity = new City(city);

            nodes.put(cityName, copyCity);
        }

        //copy research stations
        ArrayList<String> otherStations = other.getResearchStations();
        for (int i = 0; i < otherStations.size(); i++) {
            stations.add(other.getResearchStations().get(i));
        }

        playerdeck = new Deck(other.getPlayerDeck());
        infectiondeck = new InfectionDeck(other.getInfectiondeck());

        blueCured = other.isDiseaseCured("B");
        blackCured = other.isDiseaseCured("U");
        redCured = other.isDiseaseCured("R");
        yellowCured = other.isDiseaseCured("Y");

        blueEradicated = other.isDiseaseEradicated("B");
        blackEradicated = other.isDiseaseEradicated("U");
        redEradicated = other.isDiseaseEradicated("R");
        yellowEradicated = other.isDiseaseEradicated("Y");

        outbreak = other.getOutbreak();
        //exploded cities is empty
        //epidemicDifficulty is a final value

        // copy players
        for (int i = 0; i < other.getPlayers().size(); i++) {
            players.add(i, new Player(other.getPlayers().get(i)));
        }

        infectionrateindex = other.getInfectionrateindex();
        //infection rates are final
        haveLost = other.haveLost;
        epochSize = other.getEpochSize();
        epochOverflow = other.getEpochOverflow();
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

    // sets up the game
    public void gameSetup() {
        //create players
        addPlayer(Player.Role.DISPATCHER);
        addPlayer(Player.Role.PLANNER);
        //addPlayer(Player.Role.MEDIC);
        //addPlayer(Player.Role.SCIENTIST);

        dealCards(); // deal cards to players

        setupInfectedCities(); // distribute cubes

        // shuffle in epidemic cards AFTER dealing cards to players
        // this takes into account cases where the deck isn't divisible
        playerdeck.shuffle();
        epochSize = playerdeck.deckSize() / epidemicDifficulty;
        // the last epoch may be a different size if the player deck isn't divisible by epidemic difficulty
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

        stations.add("atlanta"); // add default research station
        stations.add("saopaulo"); // extra research stations for testing
        stations.add("baghdad");
        stations.add("bangkok");
    }

    public void addPlayer(Player.Role role) {
        Player newplayer = new Player(role);

        players.add(newplayer);
    }

    // puts cubes on the board at the beginning of the game
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

    public int getInfectionSize() {
        return infectiondeck.deckSize();
    }

    public int getEpidemicDifficulty() {
        return epidemicDifficulty;
    }

    public int getEpochSize() {
        return epochSize;
    }

    public int getEpochOverflow() {
        return epochOverflow;
    }

    // deal cards to players at the beginning of the game
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
                PlayerCard newCard = (PlayerCard) playerdeck.pop(); // assumes that this is done before epidemics are added
                player.addCardToHand(newCard);
            }
        }
    }

    // launcher function for the real newTurn
    public void newTurn(Player currentPlayer) {
        newTurn(currentPlayer, true); // verbose by default
    }

    //everything that needs to be done at the end of each turn
    //draw infection cards, put new cubes, handle epidemic cards
    //if verbose is true, it'l print info (should be false for simulations)
    public void newTurn(Player currentPlayer, boolean verbose) {

        if (playerdeck.deckSize() < 2) { //if we can't draw cards, we've lost
            haveLost = true;
        }

        //draw player cards and do epidemics
        for (int i = 0; i < 2; i++) { //loop until 2 cards are added to player hand

            Card drawnCard = playerdeck.pop();

            if (drawnCard.getCardType() == Card.CardType.EPIDEMIC) {
                if (verbose) {
                    System.out.println("Drew an Epidemic card!!");
                }

                //increase the infection rate
                if (infectionrateindex < 6) {
                    infectionrateindex++;
                }

                //add 3 cubes
                InfectionCard cardToInfect = (InfectionCard) infectiondeck.getBottomNormalCard();
                nodes.get(cardToInfect.getCity()).addCubes(3, this);
                infectiondeck.pushToDiscard(cardToInfect);

                if (verbose) {
                    System.out.println("Added cubes to " + cardToInfect.getCity());
                }

                infectiondeck.shuffleBack();
            } else {// otherwise it must be a normal player card
                currentPlayer.addCardToHand((PlayerCard) drawnCard);

                if (verbose) {
                    System.out.println("Player added " + drawnCard.getCardInfoString() + " to their hand");
                }
            }
        }

        //draw infection cards and update cubes
        int amountCards = infectionrates[infectionrateindex]; //get the infection rate

        for (int i = 0; i < amountCards; i++) { //depends on the infection rate
            Card card = infectiondeck.draw();
            if (verbose) {
                System.out.println("Drew an infection card");
            }

            InfectionCard infcard = (InfectionCard) card;

            if (!isDiseaseEradicated(infcard.getColor())) {
                City city = nodes.get(infcard.getCity());

                if (verbose) {
                    System.out.println("Added cubes to " + city.getName());
                }
                city.incrementCubes(this); //put new cube
            }
        }

        updateEradicated();
    }

    // updates if all cubes of a certain color are gone
    protected void updateEradicated() {
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

    // checks if we've reached a game over
    public boolean haveLost() {
        if (!haveLost) { // if we've already lost, don't do anything
            haveLost = (outbreak >= 8); // if 8 or more outbreaks
        }

        if (!haveLost) { // if we've already lost, don't do anything

            //check if we're out of cubes
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

    //returns the amount of cubes in total for each of the colors
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

    public ArrayList<String> getResearchStations() {
        return stations;
    }

    public int getInfectionRate() {
        return infectionrates[infectionrateindex];
    }

    public int getInfectionrateindex() {
        return infectionrateindex;
    }

    public InfectionDeck getInfectiondeck() {
        return infectiondeck;
    }

    //gets the total of each color in the players hands
    public HashMap<String, Integer> getPlayerColorCount() {
        ArrayList<Card> playerdiscard = playerdeck.getDiscard();
        HashMap<String, Integer> colorcount = new HashMap<>();
        colorcount.put("U", 0);
        colorcount.put("B", 0);
        colorcount.put("R", 0);
        colorcount.put("Y", 0);

        for (Card c : playerdiscard) {
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

    /* Predict Player gives the percentages that
    * a certain color might be drawn from the player deck */
    public HashMap<String,Double> predictPlayer() {
        HashMap<String,Double> predictions = new HashMap<>();
        ArrayList<Player> players = getPlayers();
        double uCount = 0, bCount = 0, yCount = 0, rCount = 0;           //counts the color card within players hands
        double uDiscard = 0, rDiscard = 0, yDiscard = 0, bDiscard = 0;   //counts the color card within the discard pile
        double uPossible = 0, rPossible = 0, yPossible = 0, bPossible = 0; //this will represent 12-count-discard
        double deck = getPlayerDeck().deckSize();
        for (Player c : players) {
            HashMap<String, Integer> counter = c.colorCount();
            for (Map.Entry<String, Integer> cubes : counter.entrySet()) {
                String key = cubes.getKey();
                Integer value = cubes.getValue();
                //number of cards in the players hands and what colors
                if (key.equals("U")) { uCount += value; }
                if (key.equals("R")) { rCount += value; }
                if (key.equals("Y")) { yCount += value; }
                if (key.equals("B")) { bCount += value; }
            }
            HashMap<String, Integer> discardCounter = getPlayerColorCount();
            for (Map.Entry<String, Integer> num : discardCounter.entrySet()) {
                String key = num.getKey();
                //counts number of cards in the discard
                Integer value = num.getValue();
                if (key.equals("U")) { uDiscard += value; }
                if (key.equals("R")) { rDiscard += value; }
                if (key.equals("Y")) { yDiscard += value; }
                if (key.equals("B")) { bDiscard += value; }
            }
        }
        //calculates the percentages and puts them in the hashmap
        uPossible = 12 - uCount - uDiscard;
        rPossible = 12 - rCount - rDiscard;
        yPossible = 12 - yCount - yDiscard;
        bPossible = 12 - bCount - bDiscard;
        double u = (((uPossible / deck) * 2f) - ((uPossible / deck) * ((uPossible - 1) / deck))) * 100f;
        predictions.put("U",u);

        double r = (((rPossible / deck) * 2f) - ((rPossible / deck) * ((rPossible - 1) / deck))) * 100f;
        predictions.put("R",r);

        double y = (((yPossible / deck) * 2f) - ((yPossible / deck) * ((yPossible - 1) / deck))) * 100f;
        predictions.put("Y",y);

        double b = (((bPossible / deck) * 2f) - ((bPossible / deck) * ((bPossible - 1) / deck))) * 100f;
        predictions.put("B",b);

        return predictions;
    }

    public HashMap<Integer, ComputerPlayer> getAiList() {
        return aiList;
    }

    public ComputerPlayer getAI(int i) {
        return aiList.get(i);
    }

    public boolean isAI(int i) {
        return aiList.containsKey(i);
    }

    // register a new ai
    public void makeAI(int i) {
        if (players.size() > i && !aiList.containsKey(i)) {
            aiList.put(i, new ComputerPlayer(this, players.get(i), i));
        } else {
            // bad
            System.out.println("Tried to make an invalid ai - GameState.makeAI()");
            assert false;
        }
    }

    public HashMap<String,Double> avgCityTime(Player player){
        /*
        This will say the average distance from every city to a designated
        research station. Used to help TTW when it was done using only math.
        In the future it should avg the distance from cities to each other for every color
         */
        HashMap<String,City> cities = getCities();
        HashMap<String,Double> averages = new HashMap<>();
        double blueAVG=0; double redAVG=0; double blackAVG=0; double yellowAVG=0;int goNormal=0;
        for (Map.Entry<String,City> c : cities.entrySet()) {
            String key = c.getKey();
            City value = c.getValue();

            if (value.getColor().equals("U")) {
                goNormal = player.goNormal("Baghdad",key,this).size();
                blackAVG = (double) goNormal + blackAVG;
           }
           if (value.getColor().equals("Y")) {
                goNormal = player.goNormal("SaoPaulo",key,this).size();
                yellowAVG = (double) goNormal + yellowAVG;
            }
            if (value.getColor().equals("R")) {
                goNormal = player.goNormal("Bangkok",key,this).size();
                redAVG = (double) goNormal + redAVG;
            }
            if (value.getColor().equals("B")) {
                goNormal = player.goNormal("NewYork",key,this).size();
                blueAVG = (double) goNormal + blueAVG;
            }
        }
        blackAVG = blackAVG/11.0; blueAVG = blueAVG/11.0; redAVG = redAVG/11.0; yellowAVG = yellowAVG/12.0;
       // System.out.println(blackAVG);

        averages.put("U", blackAVG);
        averages.put("B", blueAVG);
        averages.put("R", redAVG);
        averages.put("Y", yellowAVG);

        return averages;
    }

}


