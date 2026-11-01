package transport.element;
import transport.visitor.TransportVisitor;
public class Tram implements TransportElement {
    private final String id;
    private final double energyConsumed;
    private final boolean wheelchairAccessible;
    public Tram(String id, double energyConsumed, boolean accessible) {
        this.id = id; this.energyConsumed = energyConsumed; this.wheelchairAccessible = accessible;
    }
    public String getId() { return id; }
    public double getEnergyConsumed() { return energyConsumed; }
    public boolean isWheelchairAccessible() { return wheelchairAccessible; }
    @Override public <R> R accept(TransportVisitor<R> visitor) { return visitor.visitTram(this); }
}