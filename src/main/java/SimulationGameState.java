public class SimulationGameState extends GameState {

    // copy constructor
    public SimulationGameState(GameState other) {
        super(other);
    }

    public void copy(GameState other) { // makes copy public on this class
        super.copy(other);
    }

    public void treatDisease(String city, int amount) {
        City targetCity = GameState.getCities().get(city);

        //if disease is cured
        if (isDiseaseCured(targetCity.getColor())) {
            targetCity.removeCubes(targetCity.getCubeCount());
        } else {
            targetCity.removeCubes(amount);
        }
    }

    public int simulateUntilLoss() {
        int turns = 0;
        int player = 0;

        while (!haveLost()) {
            turns++;

            newTurn(players.get(player)); //TODO need to shuffle and copy decks!

            //switch to the next player
            if (player < players.size() - 1) {
                player++;
            } else {
                player = 0;
            }
        }

        return turns;

        // |   ||
        // ||  |_
    }

    @Override
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
                nodes.get(cardToInfect.getCity()).addCubes(3, this);
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
                city.incrementCubes(this); //put new cube
            }
        }

        updateEradicated();
    }
}
