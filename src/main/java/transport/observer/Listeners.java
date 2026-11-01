package transport.observer;

class StopDisplay implements TransportListener {
    @Override public void onEvent(TransportEvent event) {
        // System.out.println("Display: " + event.getElementId() + " " + event.getMessage());
    }
}

class PassengerApp implements TransportListener {
    @Override public void onEvent(TransportEvent event) {
        // System.out.println("App notification: Route " + event.getRouteId() + " updated.");
    }
}

class RidershipStats implements TransportListener {
    private int arrivalsCount = 0;
    @Override public void onEvent(TransportEvent event) {
        if (event.getType() == EventType.ARRIVAL) arrivalsCount++;
    }
    public int getArrivalsCount() { return arrivalsCount; }
}

class MaintenanceNotifier implements TransportListener {
    public boolean emergencyTriggered = false;
    @Override public void onEvent(TransportEvent event) {
        if (event.getType() == EventType.EMERGENCY) emergencyTriggered = true;
    }
}