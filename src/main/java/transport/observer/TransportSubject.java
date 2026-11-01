package transport.observer;
public interface TransportSubject {
    void subscribe(EventType type, TransportListener listener);
    void unsubscribe(EventType type, TransportListener listener);
}