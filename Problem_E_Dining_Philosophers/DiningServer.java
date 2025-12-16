/**
 * DiningServer Interface
 * Defines the contract for dining philosophers synchronization
 */
public interface DiningServer {
    
    /**
     * Called by a philosopher when it wishes to eat
     * @param philosopherNumber The ID of the philosopher (0-4)
     */
    public void takeForks(int philosopherNumber);
    
    /**
     * Called by a philosopher when it is finished eating
     * @param philosopherNumber The ID of the philosopher (0-4)
     */
    public void returnForks(int philosopherNumber);
}