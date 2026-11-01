package transport.element;
import transport.visitor.TransportVisitor;
public class ElectricScooter implements TransportElement {
    private final String id;
    public ElectricScooter(String id) { this.id = id; }
    public String getId() { return id; }
    @Override public <R> R accept(TransportVisitor<R> visitor) { return visitor.visitScooter(this); }
}