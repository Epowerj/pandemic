
// represents a generic card
// used in cases where we don't know what type a specific card is
// all the other cards are subclasses
public abstract class Card {
    CardType cardtype = null;

    public CardType getCardType() {
        return cardtype;
    }

    public abstract String getCardInfoString(); // for debug info - prints a description of a card

    enum CardType {PLAYER, INFECTION, EPIDEMIC} // possible card types
}
