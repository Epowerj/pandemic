public class InfectionCard extends Card {
    private String city;
    private String color;

    public InfectionCard(String cityname, String colorstring){
        city = cityname;
        color = colorstring;
        cardtype = CardType.INFECTION;
    }

    public String getCardInfoString(){
        return "InfectionCard-" + city + ":" + color;
    }

    public String getColor(){
        return color;
    }

    public String getCity(){
        return city;
    }
}
