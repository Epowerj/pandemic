import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Player {

    ArrayList<PlayerCard> hand = new ArrayList<>();
    String currentCity = "atlanta"; //default start location
    Role role;

    public Player(Role playerole) {
        role = playerole;
    }

    public Role getRole() {
        return role;
    }

    public String getCurrentCity() {
        return currentCity;
    }

    //not an action - used only for dispatcherTeleport and manually setting position
    public void setCurrentCity(String toSet) {
        currentCity = toSet;
    }

    public ArrayList<PlayerCard> getHand() {
        return hand;
    }

    private PlayerCard getCityCardFromHand(String city) {
        for (PlayerCard card : hand) {
            if (card.getCity().equals(city)) {
                return card;
            }
        }

        return null;
    }

    public boolean isHoldingCityCard(String target) {
        target = target.toLowerCase();

        for (PlayerCard card : hand) {
            if (card.getCity().toLowerCase().equals(target)) {
                return true;
            }
        }

        return false;
    }

    //returns the card and removes it from hand
    private PlayerCard takeCityCard(String targetCity) {
        PlayerCard toReturn = null;

        for (int i = 0; i < hand.size(); i++) {
            PlayerCard card = hand.get(i);

            if (card.getCity().toLowerCase().equals(targetCity)) {
                toReturn = card;
                hand.remove(i);
            }
        }

        return toReturn;
    }

    public void addCardToHand(PlayerCard card) {
        hand.add(card);
    }

    //move to an adjacent city
    //returns false if move isn't possible
    public boolean drive(String destination) {
        HashMap<String, City> cities = GameState.getCities();

        if (cities.get(currentCity).isAdjacent(destination)) {
            currentCity = destination; //do the move
            return true;
        } else {
            return false;
        }
    }

    //move to a city of a held card
    //returns false if move isn't possible
    public boolean directFlight(String destination) {
        if (this.isHoldingCityCard(destination)) {
            currentCity = destination; //do the move
            PlayerCard toDiscard = takeCityCard(destination);
            GameState.discardPlayerCard(toDiscard);
            return true;
        } else {
            return false;
        }
    }

    //move to any city if card matches current position
    //returns false if move isn't possible
    public boolean charterFlight(String destination) {
        if (this.isHoldingCityCard(currentCity)) {
            String previousCity = currentCity;
            currentCity = destination; //do the move
            PlayerCard toDiscard = takeCityCard(previousCity);
            GameState.discardPlayerCard(toDiscard);
            return true;
        } else {
            return false;
        }
    }

    //move between research station cities
    //returns false if move isn't possible
    public boolean shuttleFlight(String destination) {
        //if current location and destination both have research stations
        if (GameState.cityHasResearchStation(currentCity) && GameState.cityHasResearchStation(destination)) {
            currentCity = destination; //do the move
            return true;
        } else {
            return false;
        }
    }

    //build research station at current location
    //returns false if not possible
    public boolean buildResearchStation() {
        if (role == Role.OPERATION) { //if the player is an operations expert
            GameState.placeResearchStation(currentCity);

            return true;
        } else if (this.isHoldingCityCard(currentCity)) { //if the player has a card of their current position
            GameState.placeResearchStation(currentCity);

            PlayerCard toDiscard = takeCityCard(currentCity);
            GameState.discardPlayerCard(toDiscard);

            return true;
        } else {
            return false;
        }
    }

    //destroy a disease cube at the current position
    //TODO removing cured disease is an automatic and free action for medic
    public void treatDisease() {
        City targetCity = GameState.getCities().get(currentCity);

        //if disease is cured or if player is a medic
        if (GameState.isDiseaseCured(targetCity.getColor()) || role == Role.MEDIC) {
            targetCity.removeCubes(targetCity.getCubeCount());
        } else {
            targetCity.removeCubes(1);
        }
    }

    //share knowledge with another player
    //returns false if it isn't possible
    public boolean shareKnowledge(Player targetPlayer, String targetCity) {

        boolean isResearcher = (role == Role.RESEARCHER);
        boolean inTargetCity = (targetCity.equals(currentCity));
        boolean playersShareCity = (targetPlayer.getCurrentCity().equals(currentCity));
        boolean haveTargetCityCard = (isHoldingCityCard(targetCity));

        if (haveTargetCityCard && playersShareCity && (isResearcher || inTargetCity)) {
            //note: target player has to throw out a card if they have too many

            PlayerCard card = takeCityCard(targetCity);
            targetPlayer.addCardToHand(card);

            return true;
        } else {
            return false;
        }
    }

    //take knowledge from another player
    //returns false if it isn't possible
    public boolean takeKnowledge(Player targetPlayer, String targetCity) {

        boolean isResearcher = (targetPlayer.getRole() == Role.RESEARCHER);
        boolean inTargetCity = (targetCity.equals(currentCity));
        boolean playersShareCity = (targetPlayer.getCurrentCity().equals(currentCity));
        boolean haveTargetCityCard = (targetPlayer.isHoldingCityCard(targetCity));

        if (haveTargetCityCard && playersShareCity && (isResearcher || inTargetCity)) {
            //note: target player has to throw out a card if they have too many

            PlayerCard card = targetPlayer.takeCityCard(targetCity);
            addCardToHand(card);

            return true;
        } else {
            return false;
        }
    }

    //cure a disease by sacrificing 5 cards of the same color at a research station
    //if player is a scientist, only 4 cards are needed, the last argument can be any string
    //returns false if it's not possible
    public boolean discoverCure(String cardCity1, String cardCity2, String cardCity3, String cardCity4, String cardCity5) {

        boolean haveCards = true;
        String color = "";

        if (!isHoldingCityCard(cardCity1)) {
            haveCards = false;
        } else {
            color = getCityCardFromHand(cardCity1).getColor(); //get color
        }

        if (!isHoldingCityCard(cardCity2) && getCityCardFromHand(cardCity2).getColor().equals(color)) {
            haveCards = false;
        }

        if (!isHoldingCityCard(cardCity3) && getCityCardFromHand(cardCity3).getColor().equals(color)) {
            haveCards = false;
        }

        if (!isHoldingCityCard(cardCity4) && getCityCardFromHand(cardCity4).getColor().equals(color)) {
            haveCards = false;
        }

        //scientist doesn't need a fifth card
        if (role != Role.SCIENTIST) {
            if (!isHoldingCityCard(cardCity5) && getCityCardFromHand(cardCity5).getColor().equals(color)) {
                haveCards = false;
            }
        }

        if (GameState.cityHasResearchStation(currentCity) && haveCards) {
            GameState.setCured(color);

            //discard cards
            PlayerCard toDiscard = takeCityCard(cardCity1);
            GameState.discardPlayerCard(toDiscard);
            toDiscard = takeCityCard(cardCity2);
            GameState.discardPlayerCard(toDiscard);
            toDiscard = takeCityCard(cardCity3);
            GameState.discardPlayerCard(toDiscard);
            toDiscard = takeCityCard(cardCity4);
            GameState.discardPlayerCard(toDiscard);
            if (role != Role.SCIENTIST) { //Don't discard the last card if you don't need to
                toDiscard = takeCityCard(cardCity5);
                GameState.discardPlayerCard(toDiscard);
            }

            return true;
        } else {
            return false;
        }
    }

    //move from a research station to any city by discarding any city card
    //only operations expert can do this
    //returns false if not possible
    //TODO only possible once per turn
    public boolean operationsMove(String destination, String cityCardToDiscard) {

        if (role == Role.OPERATION && GameState.cityHasResearchStation(currentCity)) {
            currentCity = destination; //do the move
            PlayerCard toDiscard = takeCityCard(cityCardToDiscard);
            GameState.discardPlayerCard(toDiscard);

            return true;
        } else {
            return false;
        }
    }

    //move another player
    //only dispatcher can do this
    //returns false if not possible
    public boolean dispatcherMove(Player targetPlayer, String moveType, String destination) {

        if (role == Role.DISPATCHER) {
            if (moveType.equals("drive")) {
                return targetPlayer.drive(destination);
            } else if (moveType.equals("directflight")) {
                return targetPlayer.directFlight(destination);
            } else if (moveType.equals("charterflight")) {
                return targetPlayer.charterFlight(destination);
            } else if (moveType.equals("shuttleflight")) {
                return targetPlayer.shuttleFlight(destination);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    //move any player to any other player
    //only Dispatcher can do this
    //returns false if not possible
    public boolean dispatcherTeleport(Player tomove, Player target) {

        if (role == Role.DISPATCHER) {
            tomove.setCurrentCity(target.getCurrentCity());

            return true;
        } else {
            return false;
        }
    }

    public void discardFromHand(String card) {
        Card todiscard = null;

        for (PlayerCard c : hand) {
            if (c.getCity().equals(card)) {
                todiscard = c;
            }
        }

        hand.remove(todiscard);
    }

    enum Role {OPERATION, MEDIC, PLANNER, DISPATCHER, SPECIALIST, RESEARCHER, SCIENTIST}

    //TODO specialist stops cube updates

    //prints the fastest path to drive to destination
    public String goDrivePrint(String destination) {
        HashMap<String, City> cities = GameState.getCities();

        LinkedList<String> queue = new LinkedList<>(); //for some reason LinkedList is a queue
        ArrayList<String> visited = new ArrayList<>();
        HashMap<String, String> meta = new HashMap<>();

        String root = currentCity.toLowerCase();
        meta.put(root, "");
        queue.add(root);

        while (!queue.isEmpty()) {
            String subRoot = queue.poll();

            if (subRoot.equals(destination)) {
                return goDrivePrintConstruct(subRoot, meta); //return
            } else {
                City subRootCity = cities.get(subRoot);

                for (String child : subRootCity.getAdjacent()) {

                    if (!visited.contains(child)) {
                        meta.put(child, subRoot);
                        queue.add(child);
                        visited.add(child);
                    }
                }

                visited.add(subRoot);
            }
        }

        return "Drive failed";
    }

    //helper method for godriveprint
    private String goDrivePrintConstruct(String state, HashMap<String, String> meta) {
        ArrayList<String> actionList = new ArrayList<>();
        String destination = state;
        String result = "";

        while (!meta.get(state).equals("")) { //until root node
            String action = meta.get(state);
            actionList.add(action);
            state = action;
        }

        ArrayList<String> toReturn = new ArrayList<>();

        //reverse the list
        for (int i = (actionList.size() - 1); i >= 0; i--) {
            //toReturn.add(actionList.get(i));

            result += (actionList.get(i) + " -> "); //append
        }

        result += (destination);

        return result;
    }

    //prints the fastest path to get to destination
    public String goAnyPrint(String destination) {
        HashMap<String, City> cities = GameState.getCities();

        LinkedList<String> queue = new LinkedList<>(); //for some reason LinkedList is a queue
        ArrayList<String> visited = new ArrayList<>();
        HashMap<String, String> meta = new HashMap<>();

        String root = currentCity.toLowerCase();
        meta.put(root, "");
        queue.add(root);

        while (!queue.isEmpty()) {
            String subRoot = queue.poll();

            if (subRoot.equals(destination)) {
                return goAnyPrintConstruct(subRoot, meta); //return
            } else {
                City subRootCity = cities.get(subRoot);

                for (String child : subRootCity.getAdjacent()) {

                    if (!visited.contains(child)) {
                        meta.put(child, subRoot);
                        queue.add(child);
                        visited.add(child);
                    }
                }

                //TODO add another loop for flyable cities
                //TODO make sure order is right

                //TODO handle shuttle flights
                if (GameState.getStations().contains(subRootCity.getName())) { //if there is a station at current position
                    for (String city : GameState.getStations()) {

                        if (!visited.contains(city)) { //shuttle flights
                            meta.put(city, subRoot);
                            queue.add(city);
                            visited.add(city);
                        }
                    }
                }

                for (PlayerCard cityCard : hand) {
                    String city = cityCard.getCity();

                    if (!visited.contains(city)) { //direct flight
                        meta.put(city, subRoot);
                        queue.add(city);
                        visited.add(city);
                    }

                    //TODO handle charter flights

                }

                visited.add(subRoot);
            }
        }

        return "Drive failed";
    }

    //helper method for goAnyPrint
    private String goAnyPrintConstruct(String state, HashMap<String, String> meta) {
        ArrayList<String> actionList = new ArrayList<>();
        String destination = state;
        String result = "";

        while (!meta.get(state).equals("")) { //until root node
            String action = meta.get(state);
            actionList.add(action);
            state = action;
        }

        ArrayList<String> toReturn = new ArrayList<>();

        //reverse the list
        for (int i = (actionList.size() - 1); i >= 0; i--) {
            //toReturn.add(actionList.get(i));

            result += (actionList.get(i) + " -> "); //append
        }

        result += (destination);

        return result;
    }

}
