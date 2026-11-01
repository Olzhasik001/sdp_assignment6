package transport.visitor;
import transport.element.*;
import java.util.ArrayList;
import java.util.List;
public class VehicleFilterVisitor implements TransportVisitor<List<TransportElement>> {
    @Override public List<TransportElement> visitBus(Bus bus) { return List.of(bus); }
    @Override public List<TransportElement> visitTram(Tram tram) { return List.of(tram); }
    @Override public List<TransportElement> visitStop(Stop stop) { return List.of(); }
    @Override public List<TransportElement> visitStation(Station station) { return List.of(); }
    @Override public List<TransportElement> visitRoute(Route route) {
        List<TransportElement> list = new ArrayList<>();
        route.getElements().forEach(e -> list.addAll(e.accept(this)));
        return list;
    }
    @Override public List<TransportElement> visitNetwork(TransportNetwork network) {
        List<TransportElement> list = new ArrayList<>();
        network.getRoutes().forEach(r -> list.addAll(r.accept(this)));
        return list;
    }
}