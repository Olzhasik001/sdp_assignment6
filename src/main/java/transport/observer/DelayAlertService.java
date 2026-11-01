package transport.observer;
import transport.element.TransportNetwork;
import transport.element.Route;
import transport.visitor.JsonExportVisitor;

// Integration link: Is both a Listener (receives DELAY) and a Subject (publishes REPORT)
public class DelayAlertService extends BaseSubject implements TransportListener {
    private final TransportNetwork network;
    public String lastReport = null;

    public DelayAlertService(TransportNetwork network) {
        this.network = network;
    }

    @Override
    public void onEvent(TransportEvent event) {
        if (event.getType() == EventType.DELAY && event.getValue() > 10) {
            // Find affected route and run Visitor
            Route affectedRoute = network.getRoutes().stream()
                .filter(r -> r.getId().equals(event.getRouteId()))
                .findFirst().orElse(null);

            if (affectedRoute != null) {
                JsonExportVisitor exporter = new JsonExportVisitor();
                lastReport = affectedRoute.accept(exporter);
                publish(new TransportEvent(EventType.REPORT_GENERATED, event.getRouteId(), "System", 0, lastReport));
            }
        }
    }
}