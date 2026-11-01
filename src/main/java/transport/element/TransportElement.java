package transport.element;
import transport.visitor.TransportVisitor;
public interface TransportElement {
    <R> R accept(TransportVisitor<R> visitor);
}