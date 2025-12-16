import java.util.LinkedList;
import java.util.Queue;

/**
 * SchedulerQueue Class
 * Represents a single queue in the multi-level queue system
 */
public class SchedulerQueue {
    
    public enum SchedulingAlgorithm {
        FCFS,           // First Come First Serve
        ROUND_ROBIN,    // Round Robin
        PRIORITY        // Priority Scheduling
    }
    
    private String queueName;
    private SchedulingAlgorithm algorithm;
    private int timeQuantum;  // For Round Robin
    private Queue<Process> processes;
    
    /**
     * Constructor
     */
    public SchedulerQueue(String queueName, SchedulingAlgorithm algorithm, int timeQuantum) {
        this.queueName = queueName;
        this.algorithm = algorithm;
        this.timeQuantum = timeQuantum;
        this.processes = new LinkedList<>();
    }
    
    /**
     * Add process to queue
     */
    public void addProcess(Process process) {
        processes.add(process);
    }
    
    /**
     * Get next process based on algorithm
     */
    public Process getNextProcess() {
        if (processes.isEmpty()) {
            return null;
        }
        
        switch (algorithm) {
            case FCFS:
            case ROUND_ROBIN:
                return processes.poll();  // FIFO
                
            case PRIORITY:
                return getHighestPriorityProcess();
                
            default:
                return processes.poll();
        }
    }
    
    /**
     * Get highest priority process (lowest priority number = highest priority)
     */
    private Process getHighestPriorityProcess() {
        if (processes.isEmpty()) {
            return null;
        }
        
        Process highestPriority = null;
        for (Process p : processes) {
            if (highestPriority == null || p.getPriority() < highestPriority.getPriority()) {
                highestPriority = p;
            }
        }
        
        if (highestPriority != null) {
            processes.remove(highestPriority);
        }
        
        return highestPriority;
    }
    
    /**
     * Check if queue is empty
     */
    public boolean isEmpty() {
        return processes.isEmpty();
    }
    
    /**
     * Get queue size
     */
    public int size() {
        return processes.size();
    }
    
    /**
     * Get time quantum (for Round Robin)
     */
    public int getTimeQuantum() {
        return timeQuantum;
    }
    
    /**
     * Get scheduling algorithm
     */
    public SchedulingAlgorithm getAlgorithm() {
        return algorithm;
    }
    
    /**
     * Get queue name
     */
    public String getQueueName() {
        return queueName;
    }
    
    @Override
    public String toString() {
        return String.format("%s (Algorithm: %s, Size: %d)", 
                           queueName, algorithm, processes.size());
    }
}