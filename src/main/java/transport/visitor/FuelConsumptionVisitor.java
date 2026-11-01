package transport.visitor;
import transport.element.*;
public class FuelConsumptionVisitor implements TransportVisitor<Double> {
    @Override public Double visitBus(Bus bus) { return bus.getFuelConsumed(); }
    @Override public Double visitTram(Tram tram) { return 0.0; } // Trams use electricity, not fuel
    @Override public Double visitStop(Stop stop) { return 0.0; }
    @Override public Double visitStation(Station station) { return 0.0; }
    @Override public Double visitRoute(Route route) {
        return route.getElements().stream().mapToDouble(e -> e.accept(this)).sum();
    }
    @Override public Double visitNetwork(TransportNetwork network) {
        return network.getRoutes().stream().mapToDouble(r -> r.accept(this)).sum();
    }
}