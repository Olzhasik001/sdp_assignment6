package transport.visitor;
import transport.element.*;
public class AccessibilityAuditVisitor implements TransportVisitor<Integer> {
    @Override public Integer visitBus(Bus bus) { return bus.isWheelchairAccessible() ? 1 : 0; }
    @Override public Integer visitTram(Tram tram) { return tram.isWheelchairAccessible() ? 1 : 0; }
    @Override public Integer visitStop(Stop stop) { return 1; } // Assume all stops basic access
    @Override public Integer visitStation(Station station) { return 1; }
    @Override public Integer visitRoute(Route route) {
        return route.getElements().stream().mapToInt(e -> e.accept(this)).sum();
    }
    @Override public Integer visitNetwork(TransportNetwork network) {
        return network.getRoutes().stream().mapToInt(r -> r.accept(this)).sum();
    }
}