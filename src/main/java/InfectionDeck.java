import java.util.ArrayList;
import java.util.Collections;

public class InfectionDeck extends Deck {
    private ArrayList<InfectionCard> deck;
    private ArrayList<InfectionCard> discard;

    private ArrayList<ArrayList<InfectionCard>> shuffleBacks = new ArrayList<>();

    //shuffle the discard and put the cards back into the normal deck
    @Override
    public void shuffeBack() {
        Collections.shuffle(discard);
        ArrayList<InfectionCard> toAdd = new ArrayList<>();

        for (InfectionCard card : discard) {
            deck.add(card);
            toAdd.add(card);
        }

        discard = new ArrayList<InfectionCard>();

        shuffleBacks.add(toAdd);
    }

    //retrieves the top card and returns it
    @Override
    public InfectionCard pop() {
        return deck.remove(deck.size() - 1);
    }

    @Override
    public Card draw() {
        InfectionCard card = pop();
        discard.add(card);

        ArrayList<InfectionCard> lastShuffle = shuffleBacks.get(shuffleBacks.size() - 1);
        lastShuffle.remove(card);

        if (lastShuffle.isEmpty()) {
            shuffleBacks.remove(lastShuffle);
        }

        return card;
    }

    public ArrayList<ArrayList<InfectionCard>> getShuffleBack() {
        return shuffleBacks;
    }

    public boolean isInDeck(String target) {
        target = target.toLowerCase();
        for (InfectionCard card : deck) {
            if (card.getCity().toLowerCase().equals(target)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInDiscard(String target) {
        target = target.toLowerCase();
        for (InfectionCard card : discard) {
            if (card.getCity().toLowerCase().equals(target)) {
                return true;
            }
        }
        return false;
    }
}
