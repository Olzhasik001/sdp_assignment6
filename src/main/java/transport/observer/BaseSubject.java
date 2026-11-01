package transport.observer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
public abstract class BaseSubject implements TransportSubject {
    private final Map<EventType, List<TransportListener>> registry = new ConcurrentHashMap<>();
    @Override
    public void subscribe(EventType type, TransportListener listener) {
        registry.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(listener);
    }
    @Override
    public void unsubscribe(EventType type, TransportListener listener) {
        List<TransportListener> list = registry.get(type);
        if (list != null) list.remove(listener);
    }
    protected void publish(TransportEvent event) {
        List<TransportListener> list = registry.get(event.getType());
        if (list != null) {
            for (TransportListener listener : list) {
                listener.onEvent(event);
            }
        }
    }
}