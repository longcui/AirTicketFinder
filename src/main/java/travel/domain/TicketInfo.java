package travel.domain;

public class TicketInfo {
    private double cheapest = Double.MAX_VALUE,
            quickest = Double.MAX_VALUE,
            best = Double.MAX_VALUE;

    public double getCheapest() {
        return cheapest;
    }

    public void setCheapest(double cheapest) {
        this.cheapest = cheapest;
    }

    public double getQuickest() {
        return quickest;
    }

    public void setQuickest(double quickest) {
        this.quickest = quickest;
    }

    public double getBest() {
        return best;
    }

    public void setBest(double best) {
        this.best = best;
    }

    @Override
    public String toString() {
        return "TicketInfo{" +
                "cheapest=" + cheapest +
                ", quickest=" + quickest +
                ", best=" + best +
                '}';
    }
}
