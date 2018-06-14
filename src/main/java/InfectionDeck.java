import java.util.ArrayList;
import java.util.Collections;

public class InfectionDeck extends Deck {
    private ArrayList<ArrayList<InfectionCard>> shuffleBacks = new ArrayList<>();

    //shuffle the discard and put the cards back into the normal deck
    @Override
    public void shuffleBack() {
        Collections.shuffle(discard);
        ArrayList<InfectionCard> toAdd = new ArrayList<>();

        for (Card card : discard) {
            deck.add(card);
            toAdd.add((InfectionCard) card);
        }

        discard = new ArrayList<Card>();

        shuffleBacks.add(toAdd);
    }

    @Override
    public Card draw() {
        InfectionCard card = (InfectionCard) pop();
        discard.add(card);

        if (!shuffleBacks.isEmpty()) {
            ArrayList<InfectionCard> lastShuffle = shuffleBacks.get(shuffleBacks.size() - 1);
            lastShuffle.remove(card);

            if (lastShuffle.isEmpty()) {
                shuffleBacks.remove(lastShuffle);
            }
        }

        return card;
    }

    public ArrayList<ArrayList<InfectionCard>> getShuffleBack() {
        return shuffleBacks;
    }

    public boolean isInDeck(String target) {
        target = target.toLowerCase();
        for (Card c : deck) {
            InfectionCard card = (InfectionCard) c;

            if (card.getCity().toLowerCase().equals(target)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInDiscard(String target) {
        target = target.toLowerCase();
        for (Card c : discard) {
            InfectionCard card = (InfectionCard) c;

            if (card.getCity().toLowerCase().equals(target)) {
                return true;
            }
        }
        return false;
    }
}
