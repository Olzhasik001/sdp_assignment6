package transport.observer;
public class VehicleTracker extends BaseSubject {
    public void trackArrival(String routeId, String busId) {
        publish(new TransportEvent(EventType.ARRIVAL, routeId, busId, 0, "Arrived at stop"));
    }
    public void trackDelay(String routeId, String busId, int delayMinutes) {
        publish(new TransportEvent(EventType.DELAY, routeId, busId, delayMinutes, "Delayed"));
    }
}