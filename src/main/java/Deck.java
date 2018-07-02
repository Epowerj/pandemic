import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Deck {
    //These are the decks
    protected ArrayList<Card> deck;
    protected ArrayList<Card> discard;

    public Deck() {
        deck = new ArrayList<Card>();
        discard = new ArrayList<Card>();
    }

    //copy constructor
    public Deck(Deck other) {
        deck = other.getDeckCopy();
        discard = other.getDiscardCopy();
    }

    //retrieves the top card and returns it
    public Card pop() {
        return deck.remove(deck.size() - 1);
    }

    public Card draw() {
        Card card = pop();
        discard.add(card);
        return card;
    }

    public Card getBottomNormalCard() {
        int current = 0;

        while (deck.get(current).getCardType() == Card.CardType.EPIDEMIC) {
            current++;
        }

        Card card = deck.get(current);
        deck.remove(current);
        return card;
    }

    //shuffle the discard and put the cards back into the normal deck
    public void shuffleBack() {
        Collections.shuffle(discard);

        for (Card card : discard) {
            deck.add(card);
        }

        discard = new ArrayList<Card>();
    }

    //adding a card to the deck
    public void push(Card card) {
        deck.add(card);
    }

    public void insert(Card card, int location) {
        deck.add(location, card);
    }

    public void pushToDiscard(Card card) {
        discard.add(card);
    }


    //used for shuffling the deck
    public void shuffle() {
        Collections.shuffle(deck);
    }

    /*public Card getCardColor(String color) {
        for (int i = 0; i < deck.size(); i++) {
            PlayerCard card = (PlayerCard) deck.get(i);

            if (card.getColor().equals(color)) {
                deck.remove(i);
                return card;
            }
        }

        return null;
    }*/

    //used for debug - prints everything to console
    public void printAllCards() {
        System.out.print("Cards in the deck: ");
        for (Card card : deck) {
            System.out.print(card.getCardInfoString() + ", ");
        }

        System.out.print("\n");
    }

    public int deckSize() {
        return deck.size();
    }

    public int discardSize() {
        return discard.size();
    }

    public ArrayList<Card> getDiscard() {
        return discard;
    }

    //returns a shallow copy of the deck
    public ArrayList<Card> getDeckCopy() {
        ArrayList<Card> copy = new ArrayList<>();

        for (int i = 0; i < deck.size(); i++) {
            copy.add(i, deck.get(i));
        }

        return copy;
    }

    //returns a shallow copy of the discard
    public ArrayList<Card> getDiscardCopy() {
        ArrayList<Card> copy = new ArrayList<>();

        for (int i = 0; i < discard.size(); i++) {
            copy.add(i, discard.get(i));
        }

        return copy;
    }

    // reshuffle the existing cards keeping epidemic order proper
    // prevents the simulations from 'cheating'
    public void reshuffleExistingDeck(GameState gameState) {
        //figure out where we are in terms of epidemics
        int deckSize = deck.size();
        int currentEpoch = 1;
        int currentEpochSize = gameState.getEpochOverflow() + 1;
        double predictor = 0;

        while (deckSize > currentEpochSize) {
            //this isn't the epoch we're looking for; increment

            currentEpoch++;
            deckSize -= currentEpochSize;
            currentEpochSize = gameState.getEpochSize() + 1;
        }

        //now we know which epoch we're in
        boolean isEpidemicDrawn = (gameState.getInfectionrateindex() > (gameState.getEpidemicDifficulty() - currentEpoch));
        int cardsLeft = deckSize;

        //now remove all the epidemic cards
        for (int i = 0; i < deck.size(); i++) {
            Card card = deck.get(i);

            if (card.cardtype == Card.CardType.EPIDEMIC) {
                deck.remove(i);
            }
        }

        // if there was an epidemic in the last epoch, we've removed it
        if (!isEpidemicDrawn) {
            cardsLeft--;
        }

        shuffle(); // shuffling only normal cards at this point

        //now add the epidemic cards back in
        Random r = new Random();
        int random;

        ArrayList<Card> result = new ArrayList<>();

        // insert the first one

        //if we are part way into the current epoch, use cardsLeft instead of epochSize
        int cardsInEpoch;
        if (currentEpoch > 1) {// if we have more than just one epoch
            cardsInEpoch = gameState.getEpochOverflow();
        } else {// current epoch is 1, so we are part way into the last epoch
            cardsInEpoch = cardsLeft;
        }

        // move cards over from the current deck into the current epoch
        ArrayList<Card> epochToAdd = new ArrayList<>();
        for (int i = 0; i < cardsInEpoch; i++) {
            epochToAdd.add(deck.get(deck.size() - 1)); // add the last card in the deck to the current epoch
            deck.remove(deck.get(deck.size() - 1)); // remove the one we copied from the deck
        }

        if (!isEpidemicDrawn) {
            random = r.nextInt(cardsInEpoch - 1); // random number from 0 to epochOverflow
            epochToAdd.add(random, new EpidemicCard()); // insert into epoch
        }

        result.addAll(epochToAdd); // add epoch to result

        // for each epidemic except the first one
        for (int i = 1; i < currentEpoch; i++) {

            //if we are part way into the current epoch, use cardsLeft instead of epochSize
            if (i != currentEpoch - 1) {
                cardsInEpoch = gameState.getEpochSize();
            } else {// otherwise, we are part way into the epoch
                cardsInEpoch = cardsLeft;
            }

            // move cards over from the current deck into the current epoch
            epochToAdd = new ArrayList<>();
            for (int j = 0; j < cardsInEpoch; j++) {
                epochToAdd.add(deck.get(deck.size() - 1)); // add the last card in the deck to the current epoch
                deck.remove(deck.get(deck.size() - 1));
            }

            // if we aren't in the last epoch
            // or if we are, then if no epidemic has been drawn
            if (i != currentEpoch - 1 || !isEpidemicDrawn) {
                random = r.nextInt(cardsInEpoch - 1); // random number from 0 to epochSize
                epochToAdd.add(random, new EpidemicCard()); // insert into epoch
            }

            result.addAll(epochToAdd); // add epoch to result
        }

        deck = result; //now set the deck
    }
}
