/**
 * Main Program for Dining Philosophers Problem
 * Demonstrates deadlock-free solution using condition variables
 */
public class DiningPhilosophersMain {
    
    private static final int NUM_PHILOSOPHERS = 5;
    private static final int TIMES_TO_EAT = 5; // Each philosopher eats 5 times
    
    public static void main(String[] args) {
        System.out.println("=== Dining Philosophers Problem ===");
        System.out.println("Number of Philosophers: " + NUM_PHILOSOPHERS);
        System.out.println("Each philosopher will eat " + TIMES_TO_EAT + " times");
        System.out.println("===================================\n");
        
        // Create the dining server
        DiningServer server = new DiningServerImpl();
        
        // Create philosopher threads
        Thread[] philosophers = new Thread[NUM_PHILOSOPHERS];
        
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            philosophers[i] = new Thread(
                new Philosopher(i, server, TIMES_TO_EAT),
                "Philosopher-" + i
            );
        }
        
        // Start all philosopher threads
        System.out.println("Starting all philosophers...\n");
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            philosophers[i].start();
        }
        
        // Wait for all philosophers to finish
        try {
            for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
                philosophers[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("\n===================================");
        System.out.println("All philosophers have finished dining!");
        System.out.println("No deadlock occurred!");
        System.out.println("===================================");
    }
}