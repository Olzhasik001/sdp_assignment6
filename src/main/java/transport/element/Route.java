package transport.element;
import java.util.List;
import transport.visitor.TransportVisitor;
public class Route implements TransportElement {
    private final String id;
    private final List<TransportElement> elements;
    public Route(String id, List<TransportElement> elements) { this.id = id; this.elements = elements; }
    public String getId() { return id; }
    public List<TransportElement> getElements() { return elements; }
    @Override public <R> R accept(TransportVisitor<R> visitor) { return visitor.visitRoute(this); }
}