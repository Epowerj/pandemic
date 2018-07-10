
// represents an Epidemic card
// subclass of card
public class EpidemicCard extends Card {

    public EpidemicCard() {
        cardtype = CardType.EPIDEMIC;
    }

    public String getCardInfoString() {
        return "EpidemicCard";
    }
}
