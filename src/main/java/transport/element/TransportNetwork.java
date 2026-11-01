package transport.element;
import java.util.List;
import transport.visitor.TransportVisitor;
public class TransportNetwork implements TransportElement {
    private final String id;
    private final List<Route> routes;
    public TransportNetwork(String id, List<Route> routes) { this.id = id; this.routes = routes; }
    public String getId() { return id; }
    public List<Route> getRoutes() { return routes; }
    @Override public <R> R accept(TransportVisitor<R> visitor) { return visitor.visitNetwork(this); }
}