public class Plan {
    private String description;
    private int TTWDelta;
    private int TTLDelta;

    public Plan(String desc, int ttwD, int ttlD) {
        description = desc;
        TTWDelta = ttwD;
        TTLDelta = ttlD;
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
}
