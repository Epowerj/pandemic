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

    static void ioloop(GameState gamestate) {
        System.out.print("\n\n");
        Scanner reader = new Scanner(System.in);

        boolean looping = true;

        ArrayList<Player> players = gamestate.getPlayers();

        printPlayerInfo(gamestate);
        printResearchStations(gamestate);

        while (looping == true) { //each turn

            for (int playerNum = 0; playerNum < players.size() && looping; playerNum++) { //each player

                Player player = players.get(playerNum);

                System.out.println("Its player " + playerNum + "s turn!");

                for (int actionNum = 4; actionNum > 0 && looping; actionNum--) { //4 actions per player

                    System.out.println("Player " + playerNum + " has " + actionNum + " actions left");

                    //get input
                    System.out.print("{info, cubeinfo, drive, directflight, charterflight, shuttleflight, buildstation, treat, share, take, discover} \nYour input: \n");
                    String line = reader.nextLine().toLowerCase(); //TODO no next line error
                    String[] input = line.split(" "); //list of words

                    //quit
                    if (input[0].equals("exit") || input[0].equals("quit") || input[0].equals("q")) {
                        looping = false;
                    } else {
                        //now do actions
                        boolean success = doMove(input, gamestate, player);

                        while (!success) { //keep trying until success
                            //get input again
                            System.out.println("Try again!");
                            System.out.print("{info, cubeinfo, drive, directflight, charterflight, shuttleflight, buildstation, treat, share, take, discover} \nYour input: \n");
                            line = reader.nextLine().toLowerCase();
                            input = line.split(" "); //list of words

                            success = doMove(input, gamestate, player);
                        }
                    }
                }

                //at the end of each player's moves

                gamestate.newTurn(player);

                //TODO update user on changes

                //if there are more than 7 cards in this player's hand, the user must discard
                if (player.getHand().size() > 7) {
                    System.out.println("Player " + playerNum + " has over 7 cards!");

                    printPlayerInfo(gamestate);

                    for (int j = 0; j < player.getHand().size() - 7; j++) {
                        System.out.println("Need to discard " + (player.getHand().size() - 7) + " cards");

                        System.out.println("Which card to discard: ");
                        String discardCard = reader.nextLine();
                        player.discardfromhand(discardCard);
                    }
                }

            }
        }

        System.out.println(" - Done - ");
    }

    static boolean doMove(String[] input, GameState gamestate, Player player) {
        String move = input[0];

        boolean success = false;

        if (move.equals("info")) {
            printPlayerInfo(gamestate);
            printResearchStations(gamestate);
            printBoardInfo(gamestate);

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

            if (success == false) {
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

            if (success == false) {
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

            if (success == false) {
                System.out.println("Bad move");
                System.out.println("Usage: shuttleflight <destination>");
            }
        }

        if (move.equals("buildstation")) {
            success = player.buildResearchStation();
            if (success == false) {
                System.out.println("Bad move");
                System.out.println("Usage: buildstation");
            }
        }

        if (move.equals("treat")) {
            player.treatDisease();
            success = true;
            if (success == false) {
                System.out.println("Bad move");
                System.out.println("Usage: treat");
            }
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

            if (success == false) {
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

            if (success == false) {
                System.out.println("Bad move");
                System.out.println("Usage: take <destination card> <player number>");
            }
        }

        if (move.equals("discover")) {

            if (input.length >= 6) {

                success = player.discoverCure(input[1], input[2], input[3], input[4], input[5]);
            }

            if (success == false) {
                System.out.println("Bad move");
                System.out.println("Usage: discover <card> <card> <card> <card> <card>");
            }
        }

        return success;
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
}
