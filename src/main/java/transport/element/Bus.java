package transport.element;
import transport.visitor.TransportVisitor;
public class Bus implements TransportElement {
    private final String id;
    private final double fuelConsumed;
    private final boolean wheelchairAccessible;
    public Bus(String id, double fuelConsumed, boolean accessible) {
        this.id = id; this.fuelConsumed = fuelConsumed; this.wheelchairAccessible = accessible;
    }
    public String getId() { return id; }
    public double getFuelConsumed() { return fuelConsumed; }
    public boolean isWheelchairAccessible() { return wheelchairAccessible; }
    @Override public <R> R accept(TransportVisitor<R> visitor) { return visitor.visitBus(this); }
}