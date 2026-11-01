package transport.visitor;
import transport.element.*;
import java.util.stream.Collectors;
public class JsonExportVisitor implements TransportVisitor<String> {
    @Override public String visitBus(Bus bus) { return "{\"type\":\"Bus\",\"id\":\"" + bus.getId() + "\"}"; }
    @Override public String visitTram(Tram tram) { return "{\"type\":\"Tram\",\"id\":\"" + tram.getId() + "\"}"; }
    @Override public String visitStop(Stop stop) { return "{\"type\":\"Stop\",\"id\":\"" + stop.getId() + "\"}"; }
    @Override public String visitStation(Station station) { return "{\"type\":\"Station\",\"id\":\"" + station.getId() + "\"}"; }
    @Override public String visitRoute(Route route) {
        String children = route.getElements().stream().map(e -> e.accept(this)).collect(Collectors.joining(","));
        return "{\"type\":\"Route\",\"id\":\"" + route.getId() + "\",\"elements\":[" + children + "]}";
    }
    @Override public String visitNetwork(TransportNetwork network) {
        String children = network.getRoutes().stream().map(r -> r.accept(this)).collect(Collectors.joining(","));
        return "{\"type\":\"Network\",\"id\":\"" + network.getId() + "\",\"routes\":[" + children + "]}";
    }
}