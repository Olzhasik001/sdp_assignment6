package transport.observer;
public class TransportEvent {
    private final EventType type;
    private final String routeId;
    private final String elementId;
    private final int value;
    private final String message;
    public TransportEvent(EventType type, String routeId, String elementId, int value, String message) {
        this.type = type; this.routeId = routeId; this.elementId = elementId; this.value = value; this.message = message;
    }
    public EventType getType() { return type; }
    public String getRouteId() { return routeId; }
    public String getElementId() { return elementId; }
    public int getValue() { return value; }
    public String getMessage() { return message; }
}