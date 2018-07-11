import java.util.ArrayList;

// represents an abstract plan that the AI can consider
public class Plan {
    private String description;
    private int TTWDelta;
    private int TTLDelta;
    private ArrayList<String> path;

    public Plan(String desc, int ttwD, int ttlD, ArrayList<String> p) {
        description = desc;
        TTWDelta = ttwD;
        TTLDelta = ttlD;
        path = p;
    }

    public String getDescription() {
        return description;
    }

    public int getTTWDelta() {
        return TTWDelta;
    }

    public int getTTLDelta() {
        return TTLDelta;
    }

    public int getDeltaValue() { //TODO make this take into account base TTL and TTW
        //want to make TTWDelta smaller and TTLDelta larger
        return TTLDelta - TTWDelta;
    }
}
