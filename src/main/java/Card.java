public abstract class Card {
    CardType cardtype = null;

    public CardType getCardType() {
        return cardtype;
    }

    public abstract String getCardInfoString(); //for debug info

    enum CardType {PLAYER, INFECTION, EPIDEMIC}
}
