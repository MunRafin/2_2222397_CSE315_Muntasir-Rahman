import java.util.Random;

/**
 * Philosopher Thread
 * Represents a philosopher who alternates between thinking and eating
 */
public class Philosopher implements Runnable {
    
    private int id;
    private DiningServer server;
    private Random random;
    private int timesToEat;
    
    /**
     * Constructor
     * @param id Philosopher's ID (0-4)
     * @param server The dining server for synchronization
     * @param timesToEat Number of times this philosopher will eat
     */
    public Philosopher(int id, DiningServer server, int timesToEat) {
        this.id = id;
        this.server = server;
        this.random = new Random();
        this.timesToEat = timesToEat;
    }
    
    /**
     * Simulate thinking
     */
    private void think() throws InterruptedException {
        System.out.println("Philosopher " + id + " is THINKING");
        // Think for random time (100-500ms)
        Thread.sleep(random.nextInt(400) + 100);
    }
    
    /**
     * Simulate eating
     */
    private void eat() throws InterruptedException {
        // Eat for random time (100-500ms)
        Thread.sleep(random.nextInt(400) + 100);
    }
    
    /**
     * Main philosopher behavior
     */
    @Override
    public void run() {
        try {
            for (int i = 0; i < timesToEat; i++) {
                // Think
                think();
                
                // Get hungry and try to get forks
                server.takeForks(id);
                
                // Eat
                eat();
                
                // Return forks
                server.returnForks(id);
            }
            
            System.out.println(">>> Philosopher " + id + " has finished dining (ate " + timesToEat + " times)");
            
        } catch (InterruptedException e) {
            System.err.println("Philosopher " + id + " was interrupted");
            e.printStackTrace();
        }
    }
}