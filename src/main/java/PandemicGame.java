
import java.util.ArrayList;
import java.util.Scanner;

//Main Game
public class PandemicGame{
    //this is the main
    public static void main(String[] args){
        System.out.println("- Start -");

        GameState gamestate = new GameState("cities.txt");

        gamestate.gameSetup();

        ioloop(gamestate);
    }


    static void ioloop(GameState gamestate){
        Scanner reader = new Scanner(System.in);
        boolean looping = true;
        boolean success = true;
        String response = "";
        boolean responsebreak = true;
        ArrayList<Player> players= gamestate.getPlayers();
        System.out.print("\n\n");

        while(looping==true) {
            String move = "";
            for (Player player : players) {
                while(responsebreak ==true) {
                    System.out.println("Would you like information?");
                    response = reader.nextLine();
                    //cubes
                    //list how many cubes of each
                    if (response.equals("y")) {
                        System.out.println("What information would you like?");
                        response = reader.nextLine();
                        if (response.equals("infectionrates")) {
                            System.out.println(gamestate.getInfectionrateindex());
                        }
                        if (response.equals("outbreaks")) {
                            System.out.println(gamestate.getOutbreak());
                        }
                        if (response.equals("researchstations")) {
                            gamestate.printResearchStations();
                        }
                        if (response.equals("cures")){
                            System.out.println("Which color");
                            response = reader.nextLine();
                            System.out.println(gamestate.isDiseaseCured(response));
                        }
                        if (response.equals("cubes")){
                            System.out.println("Getting the cube count");


                        }
                        System.out.println("Would you like anything else");
                        response = reader.nextLine();
                        if (response.equals("no")){
                            responsebreak = false;
                        }
                    } else {
                        responsebreak = false;
                    }

                }

                String discaredcard="";
                player.drawCard(gamestate.getPlayerdeck());
                player.drawCard(gamestate.getPlayerdeck());

                if (player.getHand().size() > 7){
                    for (int j=0; j < player.getHand().size()-7; j++){
                        System.out.println("Which card to discard");
                        discaredcard= reader.nextLine();
                        player.discardfromhand(discaredcard);
                    }
                }

                for (int i = 0; i < 4; i++) {
                    System.out.print("Possible moves - {drive, directflight, charterflight, shuttleflight,\n" +
                            "buildresearchstation, treat, share, take, discover} \n");
                    System.out.print("Choose your move: ");
                    move = reader.nextLine();

                    if (move.equals("drive")) {
                        //call the action drive
                        System.out.print("What is the destination: ");
                        String destination = reader.nextLine();

                        success = player.drive(destination);
                        if (success == false) {
                            System.out.println("Bad move");
                            i--;
                        }
                    }
                    if (move.equals("directflight")) {
                        System.out.print("What is the destination: ");
                        String destination = reader.nextLine();

                        success = player.directFlight(destination);
                        if (success == false) {
                            System.out.println("Bad move");
                            i--;
                        }
                    }

                    if (move.equals("charterflight")) {
                        System.out.print("What is the destination: ");
                        String destination = reader.nextLine();

                        success = player.charterFlight(destination);
                        if (success == false) {
                            System.out.println("Bad move");
                            i--;
                        }
                    }

                    if (move.equals("shuttleflight")) {
                        System.out.print("What is the destination: ");
                        String destination = reader.nextLine();

                        success = player.shuttleFlight(destination);
                        if (success == false) {
                            System.out.println("Bad move");
                            i--;
                        }
                    }

                    if (move.equals("buildresearchstation")) {
                        success = player.buildResearchStation();
                        if (success == false) {
                            System.out.println("Bad move");
                            i--;
                        }
                    }

                    if (move.equals("treat")) {
                        player.treatDisease();
                        if (success == false) {
                            System.out.println("Bad move");
                            i--;
                        }
                    }

                    if (move.equals("share")) {
                        System.out.print("What card: ");
                        String destination = reader.nextLine();

                        System.out.println("What player(num): ");
                        String pnum = reader.nextLine();

                        success = player.shareKnowledge(players.get(Integer.parseInt(pnum)),destination);

                        if (success == false) {
                            System.out.println("Bad move");
                            i--;
                        }
                    }

                    if (move.equals("take")) {
                        System.out.print("What card: ");
                        String destination = reader.nextLine();

                        System.out.print("What player(number): ");
                        String pnum = reader.nextLine();

                        success = player.takeKnowledge(players.get(Integer.parseInt(pnum)), destination);
                        if (success == false) {
                            System.out.println("Bad move");
                            i--;
                        }
                    }

                    if (move.equals("discover")) {
                        System.out.print("What is the card1: ");
                        String card1 = reader.nextLine();

                        System.out.print("What is the card2: ");
                        String card2 = reader.nextLine();

                        System.out.print("What is the card3: ");
                        String card3 = reader.nextLine();

                        System.out.print("What is the card4: ");
                        String card4 = reader.nextLine();

                        System.out.print("What is the card5: ");
                        String card5 = reader.nextLine();

                        success = player.discoverCure(card1, card2, card3, card4, card5);
                        if (success == false) {
                            System.out.println("Bad move");
                            i--;
                        }

                    }

                }
                gamestate.newTurn();
            }
            //looping = false;
        }
    }
}

