package transport.element;
import transport.visitor.TransportVisitor;
public class Stop implements TransportElement {
    private final String id;
    private final boolean hasShelter;
    public Stop(String id, boolean hasShelter) { this.id = id; this.hasShelter = hasShelter; }
    public String getId() { return id; }
    public boolean hasShelter() { return hasShelter; }
    @Override public <R> R accept(TransportVisitor<R> visitor) { return visitor.visitStop(this); }
}