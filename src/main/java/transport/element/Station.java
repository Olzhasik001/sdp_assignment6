package transport.element;
import transport.visitor.TransportVisitor;
public class Station implements TransportElement {
    private final String id;
    private final int platforms;
    public Station(String id, int platforms) { this.id = id; this.platforms = platforms; }
    public String getId() { return id; }
    public int getPlatforms() { return platforms; }
    @Override public <R> R accept(TransportVisitor<R> visitor) { return visitor.visitStation(this); }
}