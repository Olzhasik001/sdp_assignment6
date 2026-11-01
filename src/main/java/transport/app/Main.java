package transport.app;

import transport.element.*;
import transport.observer.*;
import transport.visitor.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // 1. Composition Root - Build Tree
        Bus bus1 = new Bus("B1", 50.0, true);
        Stop stop1 = new Stop("S1", true);
        Route route1 = new Route("R1", List.of(stop1, bus1));
        TransportNetwork network = new TransportNetwork("CityNet", List.of(route1));

        // 2. Setup Observer Subjects & Listeners
        VehicleTracker tracker = new VehicleTracker();
        DelayAlertService alertService = new DelayAlertService(network); // Integrator

        tracker.subscribe(EventType.DELAY, alertService);

        // 3. Concurrency - 3 Producer Threads
        ExecutorService executor = Executors.newFixedThreadPool(3);

        Runnable task1 = () -> tracker.trackArrival("R1", "B1");
        Runnable task2 = () -> tracker.trackDelay("R1", "B1", 15); // Will trigger report
        Runnable task3 = () -> tracker.trackDelay("R1", "B1", 5);  // Will NOT trigger report (delay <= 10)

        executor.submit(task1);
        executor.submit(task2);
        executor.submit(task3);

        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.SECONDS);

        System.out.println("System shutdown graceful. Events processed.");
    }
}