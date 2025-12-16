import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * DiningServerImpl - Implements the dining philosophers synchronization
 * Uses locks and condition variables to prevent deadlock and starvation
 */
public class DiningServerImpl implements DiningServer {
    
    // Number of philosophers
    private static final int NUM_PHILOSOPHERS = 5;
    
    // Philosopher states
    private enum State {
        THINKING, HUNGRY, EATING
    }
    
    // State of each philosopher
    private State[] state;
    
    // Lock for synchronization
    private Lock lock;
    
    // Condition variable for each philosopher
    private Condition[] self;
    
    /**
     * Constructor - Initialize the dining server
     */
    public DiningServerImpl() {
        state = new State[NUM_PHILOSOPHERS];
        lock = new ReentrantLock();
        self = new Condition[NUM_PHILOSOPHERS];
        
        // Initialize all philosophers to THINKING state
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            state[i] = State.THINKING;
            self[i] = lock.newCondition();
        }
    }
    
    /**
     * Get the left neighbor's index
     */
    private int leftNeighbor(int philosopherNumber) {
        return (philosopherNumber + NUM_PHILOSOPHERS - 1) % NUM_PHILOSOPHERS;
    }
    
    /**
     * Get the right neighbor's index
     */
    private int rightNeighbor(int philosopherNumber) {
        return (philosopherNumber + 1) % NUM_PHILOSOPHERS;
    }
    
    /**
     * Test if philosopher can eat (both neighbors are not eating)
     */
    private void test(int philosopherNumber) {
        if (state[philosopherNumber] == State.HUNGRY &&
            state[leftNeighbor(philosopherNumber)] != State.EATING &&
            state[rightNeighbor(philosopherNumber)] != State.EATING) {
            
            // Philosopher can eat
            state[philosopherNumber] = State.EATING;
            System.out.println("Philosopher " + philosopherNumber + " is EATING");
            
            // Signal the philosopher that they can eat
            self[philosopherNumber].signal();
        }
    }
    
    /**
     * Philosopher wants to take forks (eat)
     */
    @Override
    public void takeForks(int philosopherNumber) {
        lock.lock();
        try {
            // Change state to HUNGRY
            state[philosopherNumber] = State.HUNGRY;
            System.out.println("Philosopher " + philosopherNumber + " is HUNGRY");
            
            // Try to acquire both forks
            test(philosopherNumber);
            
            // If unable to eat, wait
            while (state[philosopherNumber] != State.EATING) {
                try {
                    self[philosopherNumber].await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Philosopher returns forks (finished eating)
     */
    @Override
    public void returnForks(int philosopherNumber) {
        lock.lock();
        try {
            // Change state to THINKING
            state[philosopherNumber] = State.THINKING;
            System.out.println("Philosopher " + philosopherNumber + " is THINKING (finished eating)");
            
            // Test if left and right neighbors can now eat
            test(leftNeighbor(philosopherNumber));
            test(rightNeighbor(philosopherNumber));
        } finally {
            lock.unlock();
        }
    }
}