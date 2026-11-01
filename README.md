# Assignment 6: Public Transport System (Observer & Visitor)

## Design Decisions
1. **Observer Pattern (Push vs Pull):** We selected the **Push** model. Events (`TransportEvent`) contain all necessary context (type, elementId, value, message). Listeners (displays, apps) do not need to call the subject back to ask for details. This reduces coupling and fits the streaming nature of transit data.
2. **Visitor Pattern (Traversal):** Traversal is handled by the **Visitor** itself (e.g., `visitRoute` iterates over `route.getElements()`). This is better than element-driven traversal because different visitors might need to traverse differently (or not traverse children at all, like in a simple filter).
3. **Concurrency:** `BaseSubject` uses a `ConcurrentHashMap` of `CopyOnWriteArrayList`s. This ensures thread-safety without locking the entire subject during event publishing, allowing listeners to subscribe/unsubscribe dynamically (even from within their own `onEvent` handlers).

## Trade-off Proof (Expression Problem)
This repository contains two specific commits showing the cost of changes:
1. **Commit: Add new visitor (RevenueVisitor)**
   * **Files changed:** Only `RevenueVisitor.java` added.
   * **Cost:** Very low. Adding a new operation over the tree touches **zero** element classes.
2. **Commit: Add new element (ElectricScooter)**
   * **Files changed:** `ElectricScooter.java` (added), `TransportVisitor.java` (modified), and **ALL 5** existing visitor implementations (modified).
   * **Cost:** Very high. Adding a new data variant requires modifying the central interface and updating every single visitor to handle the new type. This is why Visitor is only good when the object structure is stable.