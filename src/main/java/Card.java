public abstract class Card {
    enum CardType {PLAYER, INFECTION, EPIDEMIC}
    CardType cardtype = null;

    public CardType getCardType(){
        return cardtype;
    }
    public abstract String getCardInfoString(); //for debug info
}
