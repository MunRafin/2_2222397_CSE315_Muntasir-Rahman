/**
 * Process Class
 * Represents a process in the CPU scheduling simulation
 */
public class Process {
    private int processId;
    private int arrivalTime;
    private int burstTime;
    private int remainingTime;
    private int priority;
    private int completionTime;
    private int turnaroundTime;
    private int waitingTime;
    private int responseTime;
    private boolean hasStarted;
    private int firstResponseTime;
    
    /**
     * Constructor
     */
    public Process(int processId, int arrivalTime, int burstTime, int priority) {
        this.processId = processId;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.priority = priority;
        this.hasStarted = false;
        this.firstResponseTime = -1;
    }
    
    // Getters
    public int getProcessId() { return processId; }
    public int getArrivalTime() { return arrivalTime; }
    public int getBurstTime() { return burstTime; }
    public int getRemainingTime() { return remainingTime; }
    public int getPriority() { return priority; }
    public int getCompletionTime() { return completionTime; }
    public int getTurnaroundTime() { return turnaroundTime; }
    public int getWaitingTime() { return waitingTime; }
    public int getResponseTime() { return responseTime; }
    public boolean hasStarted() { return hasStarted; }
    
    // Setters
    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }
    
    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
    }
    
    public void setHasStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }
    
    public void setFirstResponseTime(int firstResponseTime) {
        this.firstResponseTime = firstResponseTime;
    }
    
    /**
     * Calculate metrics after process completes
     */
    public void calculateMetrics() {
        // Turnaround Time = Completion Time - Arrival Time
        this.turnaroundTime = completionTime - arrivalTime;
        
        // Waiting Time = Turnaround Time - Burst Time
        this.waitingTime = turnaroundTime - burstTime;
        
        // Response Time = First Response Time - Arrival Time
        this.responseTime = firstResponseTime - arrivalTime;
    }
    
    /**
     * Check if process is completed
     */
    public boolean isCompleted() {
        return remainingTime == 0;
    }
    
    /**
     * Execute process for given time
     */
    public void execute(int time) {
        if (remainingTime > 0) {
            remainingTime -= time;
            if (remainingTime < 0) {
                remainingTime = 0;
            }
        }
    }
    
    @Override
    public String toString() {
        return String.format("P%d [Arrival=%d, Burst=%d, Priority=%d]", 
                           processId, arrivalTime, burstTime, priority);
    }
    
    /**
     * Get detailed metrics string
     */
    public String getMetricsString() {
        return String.format("P%d: CT=%d, TAT=%d, WT=%d, RT=%d",
                           processId, completionTime, turnaroundTime, 
                           waitingTime, responseTime);
    }
}