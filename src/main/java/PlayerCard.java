
// represents a Player card
// subclass of Card
public class PlayerCard extends Card {
    private String city;
    private String color;

    public PlayerCard(String cityname, String colorstring) {
        city = cityname;
        color = colorstring;
        cardtype = CardType.PLAYER;
    }

    public String getCardInfoString() {
        return "PlayerCard-" + city + ":" + color;
    }

    public String getColor() {
        return color;
    }


    public String getCity() {
        return city;
    }
}
