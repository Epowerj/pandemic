import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

//Main Game
public class PandemicGame {
    //this is the main
    public static void main(String[] args) {
        System.out.println("- Start -");

        GameState gamestate = new GameState("cities.txt");

        gamestate.gameSetup();

        ioloop(gamestate);
    }

    private static void ioloop(GameState gamestate) {
        System.out.print("\n\n");
        Scanner reader = new Scanner(System.in);

        String commandlist = "\n{godrive, info, cubeinfo, deckinfo, drive, directflight, charterflight, shuttleflight, buildstation, treat, share, take, discover} \nYour input: \n";

        boolean looping = true;

        ArrayList<Player> players = gamestate.getPlayers();

        printPlayerInfo(gamestate);
        printResearchStations(gamestate);
        printCubeInfo(gamestate);

        while (looping) { //each turn

            for (int playerNum = 0; playerNum < players.size() && looping; playerNum++) { //each player

                Player player = players.get(playerNum);

                System.out.println("Its player " + playerNum + "s turn!");

                for (int actionNum = 4; actionNum > 0 && looping; actionNum--) { //4 actions per player

                    discardExtra(player, playerNum, gamestate, reader);

                    System.out.println("Player " + playerNum + " has " + actionNum + " actions left");

                    //get input
                    System.out.print(commandlist);
                    String line = reader.nextLine().toLowerCase(); //TODO no next line error
                    String[] input = line.split(" "); //list of words

                    //quit
                    //TODO doesn't work
                    if (input[0].equals("exit") || input[0].equals("quit") || input[0].equals("q")) {
                        looping = false;
                    } else {
                        //now do actions
                        boolean success = doMove(input, gamestate, player);

                        while (!success) { //keep trying until success
                            //get input again
                            System.out.println("Try again!");
                            System.out.print(commandlist);
                            line = reader.nextLine().toLowerCase();
                            input = line.split(" "); //list of words

                            success = doMove(input, gamestate, player);
                        }
                    }

                    discardExtra(player, playerNum, gamestate, reader);
                }

                //at the end of each player's moves

                gamestate.newTurn(player);

                discardExtra(player, playerNum, gamestate, reader);

            }
        }

        System.out.println(" - Done - ");
    }

    private static boolean doMove(String[] input, GameState gamestate, Player player) {
        String move = input[0];

        boolean success = false;

        if (move.equals("info")) {
            printPlayerInfo(gamestate);
            printResearchStations(gamestate);
            printBoardInfo(gamestate);

            predictPlayer(gamestate, player);

            String target = input[1];
            predictInfection(gamestate, target);

            success = false;
        }

        if (move.equals("cubeinfo")) {
            printCubeInfo(gamestate);

            success = false;
        }

        if (move.equals("deckinfo")) {
            printDeckInfo(gamestate);

            success = false;
        }

        if (move.equals("godrive")) {
            if (input.length >= 2) {
                String destination = input[1];

                System.out.println(player.goDrivePrint(destination));

                success = true;
            } else {
                success = false;
            }

            if (success == false) {
                System.out.println("Bad move");
                System.out.println("Usage: godrive <destination>");
            }

            success = false; //don't want to actually count this though
        }

        if (move.equals("go")) {
            if (input.length >= 2) {
                String destination = input[1];

                System.out.println(player.goAnyPrint(destination));

                success = true;
            } else {
                success = false;
            }

            if (success == false) {
                System.out.println("Bad move");
                System.out.println("Usage: go <destination>");
            }

            success = false; //don't want to actually count this though
        }

        if (move.equals("drive")) {
            if (input.length >= 2) {
                String destination = input[1];

                success = player.drive(destination);
            } else {
                success = false;
            }

            if (success == false) {
                System.out.println("Bad move");
                System.out.println("Usage: drive <destination>");
            }
        }

        if (move.equals("directflight")) {
            if (input.length >= 2) {
                String destination = input[1];

                success = player.directFlight(destination);
            } else {
                success = false;
            }

            if (!success) {
                System.out.println("Bad move");
                System.out.println("Usage: directflight <destination>");
            }
        }

        if (move.equals("charterflight")) {
            if (input.length >= 2) {
                String destination = input[1];

                success = player.charterFlight(destination);
            } else {
                success = false;
            }

            if (!success) {
                System.out.println("Bad move");
                System.out.println("Usage: charterflight <destination>");
            }
        }

        if (move.equals("shuttleflight")) {
            if (input.length >= 2) {
                String destination = input[1];

                success = player.shuttleFlight(destination);
            } else {
                success = false;
            }

            if (!success) {
                System.out.println("Bad move");
                System.out.println("Usage: shuttleflight <destination>");
            }
        }

        if (move.equals("buildstation")) {
            success = player.buildResearchStation();
            if (!success) {
                System.out.println("Bad move");
                System.out.println("Usage: buildstation");
            }
        }

        if (move.equals("treat")) {
            player.treatDisease();
            success = true;
        }

        if (move.equals("share")) {
            ArrayList<Player> players = gamestate.getPlayers();

            if (input.length >= 3) {
                String destination = input[1];

                String pnum = input[2];

                success = player.shareKnowledge(players.get(Integer.parseInt(pnum)), destination);
            } else {
                success = false;
            }

            if (!success) {
                System.out.println("Bad move");
                System.out.println("Usage: share <destination card> <player number>");
            }
        }

        if (move.equals("take")) {

            ArrayList<Player> players = gamestate.getPlayers();

            if (input.length >= 3) {
                String destination = input[1];

                String pnum = input[2];

                success = player.takeKnowledge(players.get(Integer.parseInt(pnum)), destination);
            } else {
                success = false;
            }

            if (!success) {
                System.out.println("Bad move");
                System.out.println("Usage: take <destination card> <player number>");
            }
        }

        if (move.equals("discover")) {

            if (input.length >= 6) {

                success = player.discoverCure(input[1], input[2], input[3], input[4], input[5]);
            }

            if (!success) {
                System.out.println("Bad move");
                System.out.println("Usage: discover <card> <card> <card> <card> <card>");
            }
        }

        return success;
    }

    //discard extra cards from a players hand if they have more than 7
    static private void discardExtra(Player player, int playerNum, GameState gamestate, Scanner reader) {
        //TODO only runs twice for 3
        //if there are more than 7 cards in this player's hand, the user must discard
        if (player.getHand().size() > 7) {
            System.out.println("Player " + playerNum + " has over 7 cards!");

            printPlayerInfo(gamestate);

            while (player.getHand().size() > 7) {
                System.out.println("Need to discard " + (player.getHand().size() - 7) + " cards");

                System.out.println("Which card to discard: ");
                String discardCard = reader.nextLine().toLowerCase();

                if (player.isHoldingCityCard(discardCard)) {
                    player.discardFromHand(discardCard);
                } else {
                    System.out.println("Player " + player + " isn't holding that card!");
                }
            }
        }
    }

    static void printPlayerInfo(GameState gamestate) {
        ArrayList<Player> players = gamestate.getPlayers();

        System.out.println("Player info: ");
        for (int i = 0; i < players.size(); i++) {
            System.out.print("Player " + i + "[" + players.get(i).getCurrentCity() + "] - ");
            System.out.print("Has cards: ");
            for (Card card : players.get(i).getHand()) {
                System.out.print(card.getCardInfoString() + ", ");
            }
            System.out.print("\n");
        }

        System.out.print("\n");
    }

    static void printCubeInfo(GameState gameState) {
        HashMap<String, City> cities = gameState.getCities();

        for (Map.Entry<String, City> entry : cities.entrySet()) {
            String key = entry.getKey();
            City value = entry.getValue();

            HashMap<String, Integer> cubelist = value.getCubeList();

            for (Map.Entry<String, Integer> cubenum : cubelist.entrySet()) {
                String color = cubenum.getKey();
                Integer count = cubenum.getValue();

                if (count > 0) {
                    System.out.println(value.getName() + " has " + count + " " + color + " cubes");
                }
            }
        }
    }


    static void printDeckInfo(GameState gameState) {
        ArrayList<Player> players = gameState.getPlayers();
        System.out.println("Player deck: ");
        gameState.getPlayerDeck().printAllCards();
        System.out.println("Infection deck: ");
        gameState.getInfectionDeck().printAllCards();

    }

    static void printBoardInfo(GameState gameState) {

        System.out.println("Outbreaks: " + gameState.getOutbreak());
        System.out.println("Infection rate: " + gameState.getInfectionRate());
        //TODO cured status System.out.println("");
    }

    static void printResearchStations(GameState gameState) {
        ArrayList<String> stations = gameState.getStations();

        System.out.print("Research stations are located in: ");

        for (String station : stations) {
            System.out.print(station + ", ");
        }

        System.out.print("\n\n");
    }

    static void predictPlayer(GameState gameState, Player player) {
        double deck = gameState.getPlayerDeckSize();
        double u, b, y, r;
        HashMap<String, Integer> counter = player.colorCount();
        for (Map.Entry<String, Integer> cubes : counter.entrySet()) {
            String key = cubes.getKey();
            Integer value = cubes.getValue();
            if (key.equals("U")) {
                u = (((12 - value) / deck) * 2) * 100;
                System.out.println("There is a " + Math.round(u) + "% chance that you will get a Black Card");
            }
            if (key.equals("R")) {
                r = (((12 - value) / deck) * 2) * 100;
                System.out.println("There is a " + Math.round(r) + "% chance that you will get a Red Card");

            }
            if (key.equals("Y")) {
                y = (((12 - value) / deck) * 2) * 100;
                System.out.println("There is a " + Math.round(y) + "% chance that you will get a yellow Card");

            }
            if (key.equals("B")) {
                b = (((12 - value) / deck) * 2) * 100;
                System.out.println("There is a " + Math.round(b) + "% chance that you will get a Blue Card");

            }
        }
    }

    static void predictInfection(GameState gameState, String target) {
        double decksize = gameState.getInfectionSize();
        double infections = gameState.getInfectionRate();
        double outbreaks = gameState.getOutbreak();
        boolean success = gameState.isInfectionDiscard(target);
        //System.out.print(success);
        if (success == false) {
            double predict = ((1 / decksize) * infections) * 100;
            System.out.println("The percentage of you getting " + target + " is " + Math.round(predict) + "%");
        }
        if (success == true) {

        }
    }

    static void congestedCities() {
        //will decide which cities should be worried about the most
    }


    static void predictEpidemic(GameState gameState) {
        int outbreaks = gameState.getOutbreak();
        double deck = gameState.getPlayerDeckSize();
        double predictor = (((6 - outbreaks) / deck) * 2) * 100;
        System.out.println("There is a " + Math.round(predictor) + "% chance that you will get an Epidemic Card");
    }
}

