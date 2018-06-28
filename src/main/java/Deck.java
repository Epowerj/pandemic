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

    public ArrayList<Card> getDiscard(){ return discard; }

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

        shuffle(); // shuffling only normal cards at this point

        //now add the epidemic cards back in
        Random r = new Random();
        int random;

        // insert the first one
        if (!isEpidemicDrawn || currentEpoch > 1) {
            random = r.nextInt(gameState.getEpochOverflow()); // random number from 0 to epochOverflow
            insert(new EpidemicCard(), random); // insert into player deck
        }

        // for each epidemic except the first one
        for (int i = 0; i < currentEpoch - 1; i++) {
            random = r.nextInt(gameState.getEpochSize()); // random number from 0 to epochSize
            random += i * gameState.getEpochSize() + gameState.getEpochOverflow(); // epoch offset
            insert(new EpidemicCard(), random); // insert into player deck
        }
    }
}
