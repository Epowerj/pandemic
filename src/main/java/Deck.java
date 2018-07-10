import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

// represents a deck and the discard pile for that deck
public class Deck {
    // these are the decks
    protected ArrayList<Card> deck;
    protected ArrayList<Card> discard;

    public Deck() {
        deck = new ArrayList<Card>();
        discard = new ArrayList<Card>();
    }

    // copy constructor
    public Deck(Deck other) {
        deck = other.getDeckCopy();
        discard = other.getDiscardCopy();
    }

    // retrieves the top card and returns it
    public Card pop() {
        return deck.remove(deck.size() - 1);
    }

    // removes the top card and returns it
    public Card draw() {
        Card card = pop();
        discard.add(card);
        return card;
    }

    // this is called when an epidemic card is drawn
    // get the bottom most card that isn't an epidemic
    public Card getBottomNormalCard() {
        int current = 0;

        //loop until we get to a card that isn't an epidemic
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

        while (deckSize > currentEpochSize) {
            //this isn't the epoch we're looking for; increment

            currentEpoch++;
            deckSize -= currentEpochSize;
            currentEpochSize = gameState.getEpochSize() + 1;
        }

        //now we know which epoch we're in
        boolean isEpidemicDrawn = (gameState.getInfectionrateindex() > (gameState.getEpidemicDifficulty() - currentEpoch));
        int cardsLeft = deckSize;

        ArrayList<Card> noEpidemics = new ArrayList<>(); // list of everything except the epidemics
        int epidemicCount = 0;

        //now 'remove' all the epidemic cards
        for (int i = 0; i < deck.size(); i++) {
            Card card = deck.get(i);

            if (card.cardtype != Card.CardType.EPIDEMIC) {
                noEpidemics.add(card);
            } else {
                epidemicCount++;
            }
        }

        deck = noEpidemics;

        // if there was an epidemic in the last epoch, we've removed it
        if (!isEpidemicDrawn) {
            cardsLeft--;
        }

        shuffle(); // shuffling only normal cards at this point

        // now we need to add the epidemic cards back in
        Random r = new Random();
        int random;

        ArrayList<Card> result = new ArrayList<>();

        for (int i = 0; i < epidemicCount; i++) { // for each epoch
            int amountToAdd; // how many cards we have to add to the new deck, not including the epidemic card

            if (i == epidemicCount - 1) { // if this is the last epoch
                amountToAdd = deck.size(); // just add in whatever's left

            } else if (i == 0) { // if this is the first epoch
                amountToAdd = gameState.getEpochOverflow();

            } else { // otherwise, it's not the first epoch
                amountToAdd = gameState.getEpochSize();
            }

            random = r.nextInt(amountToAdd); // choose a location to insert the epidemic card
            boolean haveAddedEpidemic = false;

            for (int j = 0; j < amountToAdd; j++) {
                // insert an epidemic if we're at the random position
                if (j == random && !haveAddedEpidemic) {
                    result.add(new EpidemicCard());
                    haveAddedEpidemic = true;
                    j--;
                } else {
                    result.add(deck.get(deck.size() - 1)); // add the last card from the deck into result
                    deck.remove(deck.size() - 1); // remove that card from the deck
                }
            }
        }

        deck = result; //now set the deck
    }
}
