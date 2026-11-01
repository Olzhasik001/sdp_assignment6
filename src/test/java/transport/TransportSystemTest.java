package transport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import transport.element.*;
import transport.observer.*;
import transport.visitor.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class TransportSystemTest {

    private Bus bus;
    private Tram tram;
    private Stop stop;
    private Station station;
    private Route route;
    private TransportNetwork network;

    @BeforeEach
    void setUp() {
        bus = new Bus("B1", 20.5, true);
        tram = new Tram("T1", 15.0, false);
        stop = new Stop("S1", true);
        station = new Station("ST1", 4);
        route = new Route("R1", List.of(stop, station, bus, tram));
        network = new TransportNetwork("NW1", List.of(route));
    }

    // --- VISITOR TESTS (Checked against hand-calculated values) ---
    @Test void testFuelConsumptionVisitor() { // Test 1
        FuelConsumptionVisitor v = new FuelConsumptionVisitor();
        assertEquals(20.5, network.accept(v), 0.001); // Only bus uses fuel
    }

    @Test void testAccessibilityAuditVisitor() { // Test 2
        AccessibilityAuditVisitor v = new AccessibilityAuditVisitor();
        // Bus(1) + Tram(0) + Stop(1) + Station(1) = 3
        assertEquals(3, network.accept(v)); 
    }

    @Test void testVehicleFilterVisitor() { // Test 3
        VehicleFilterVisitor v = new VehicleFilterVisitor();
        List<TransportElement> vehicles = network.accept(v);
        assertEquals(2, vehicles.size());
        assertTrue(vehicles.contains(bus));
        assertTrue(vehicles.contains(tram));
    }

    @Test void testJsonExportVisitorLeafs() { // Test 4
        JsonExportVisitor v = new JsonExportVisitor();
        assertEquals("{\"type\":\"Bus\",\"id\":\"B1\"}", bus.accept(v));
        assertEquals("{\"type\":\"Tram\",\"id\":\"T1\"}", tram.accept(v));
    }

    @Test void testJsonExportVisitorComposite() { // Test 5
        JsonExportVisitor v = new JsonExportVisitor();
        String json = route.accept(v);
        assertTrue(json.startsWith("{\"type\":\"Route\",\"id\":\"R1\",\"elements\":["));
        assertTrue(json.contains("B1"));
    }

    @Test void testEmptyRouteVisitor() { // Test 6
        Route empty = new Route("R2", List.of());
        assertEquals(0.0, empty.accept(new FuelConsumptionVisitor()));
    }

    // --- OBSERVER TESTS ---
    @Test void testEventIsImmutable() { // Test 7
        TransportEvent e = new TransportEvent(EventType.ARRIVAL, "R1", "B1", 0, "msg");
        assertEquals(EventType.ARRIVAL, e.getType());
        assertEquals("R1", e.getRouteId());
        assertEquals("B1", e.getElementId());
        assertEquals(0, e.getValue());
    }

    @Test void testArrivalNotifiesSubscribers() { // Test 8
        VehicleTracker tracker = new VehicleTracker();
        AtomicInteger count = new AtomicInteger(0);
        tracker.subscribe(EventType.ARRIVAL, e -> count.incrementAndGet());
        tracker.trackArrival("R1", "B1");
        assertEquals(1, count.get());
    }

    @Test void testUnsubscribeWorks() { // Test 9
        VehicleTracker tracker = new VehicleTracker();
        AtomicInteger count = new AtomicInteger(0);
        TransportListener l = e -> count.incrementAndGet();
        tracker.subscribe(EventType.ARRIVAL, l);
        tracker.unsubscribe(EventType.ARRIVAL, l);
        tracker.trackArrival("R1", "B1");
        assertEquals(0, count.get());
    }

    @Test void testUnsubscribeFromInsideHandler() { // Test 10 (Thread-safety of CopyOnWriteArrayList)
        VehicleTracker tracker = new VehicleTracker();
        AtomicInteger count = new AtomicInteger(0);
        TransportListener l = new TransportListener() {
            @Override public void onEvent(TransportEvent event) {
                count.incrementAndGet();
                tracker.unsubscribe(EventType.ARRIVAL, this);
            }
        };
        tracker.subscribe(EventType.ARRIVAL, l);
        tracker.trackArrival("R1", "B1");
        tracker.trackArrival("R1", "B2"); // Should not be received
        assertEquals(1, count.get());
    }

    // --- INTEGRATION TESTS ---
    @Test void testIntegrationDelayTriggersReport() { // Test 11
        DelayAlertService alertService = new DelayAlertService(network);
        VehicleTracker tracker = new VehicleTracker();
        tracker.subscribe(EventType.DELAY, alertService);

        tracker.trackDelay("R1", "B1", 15); // >10 triggers visitor
        assertNotNull(alertService.lastReport);
        assertTrue(alertService.lastReport.contains("R1"));
    }

    @Test void testIntegrationMinorDelayIgnored() { // Test 12
        DelayAlertService alertService = new DelayAlertService(network);
        alertService.onEvent(new TransportEvent(EventType.DELAY, "R1", "B1", 5, "Minor"));
        assertNull(alertService.lastReport);
    }

    @Test void testChainOfObservers() { // Test 13
        DelayAlertService alertService = new DelayAlertService(network);
        AtomicInteger reportCount = new AtomicInteger(0);
        alertService.subscribe(EventType.REPORT_GENERATED, e -> reportCount.incrementAndGet());

        alertService.onEvent(new TransportEvent(EventType.DELAY, "R1", "B1", 15, "Major"));
        assertEquals(1, reportCount.get()); // Chain works: Delay -> AlertService -> Report -> Listener
    }

    @Test void testMissingRouteIgnored() { // Test 14
        DelayAlertService alertService = new DelayAlertService(network);
        alertService.onEvent(new TransportEvent(EventType.DELAY, "GHOST_ROUTE", "B1", 15, "Major"));
        assertNull(alertService.lastReport);
    }

    // --- CONCURRENCY STRESS TEST ---
    @Test void stressTestConcurrentEvents() throws InterruptedException { // Test 15
        VehicleTracker tracker = new VehicleTracker();
        AtomicInteger counter = new AtomicInteger(0);
        tracker.subscribe(EventType.ARRIVAL, e -> counter.incrementAndGet());

        int threads = 4;
        int eventsPerThread = 1000;
        CountDownLatch latch = new CountDownLatch(threads * eventsPerThread);
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < eventsPerThread; j++) {
                    tracker.trackArrival("R1", "B1");
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        executor.shutdown();
        assertEquals(4000, counter.get(), "Exact expected count of 4000 missed!");
    }
}