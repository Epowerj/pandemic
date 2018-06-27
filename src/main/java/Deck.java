import java.util.ArrayList;
import java.util.Collections;

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
        return (ArrayList<Card>) deck.clone();
    }

    //returns a shallow copy of the discard
    public ArrayList<Card> getDiscardCopy() {
        return (ArrayList<Card>) discard.clone();
    }

}
