package transport.visitor;
import transport.element.*;
public interface TransportVisitor<R> {
    R visitNetwork(TransportNetwork network);
    R visitRoute(Route route);
    R visitBus(Bus bus);
    R visitTram(Tram tram);
    R visitStop(Stop stop);
    R visitStation(Station station);
}