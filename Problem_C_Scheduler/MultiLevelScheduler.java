import java.util.ArrayList;
import java.util.List;

/**
 * MultiLevelScheduler
 * Implements multi-level queue CPU scheduling
 */
public class MultiLevelScheduler {
    
    private List<SchedulerQueue> queues;
    private List<Process> allProcesses;
    private List<Process> completedProcesses;
    private int currentTime;
    private int totalIdleTime;
    
    /**
     * Constructor
     */
    public MultiLevelScheduler() {
        queues = new ArrayList<>();
        allProcesses = new ArrayList<>();
        completedProcesses = new ArrayList<>();
        currentTime = 0;
        totalIdleTime = 0;
    }
    
    /**
     * Add a scheduling queue
     */
    public void addQueue(SchedulerQueue queue) {
        queues.add(queue);
    }
    
    /**
     * Add a process to the system
     */
    public void addProcess(Process process) {
        allProcesses.add(process);
    }
    
    /**
     * Assign process to appropriate queue based on priority
     */
    private void assignProcessToQueue(Process process) {
        // Priority-based assignment:
        // Priority 1-2 -> High Priority Queue (index 0)
        // Priority 3-4 -> Medium Priority Queue (index 1)
        // Priority 5+  -> Low Priority Queue (index 2)
        
        int queueIndex;
        if (process.getPriority() <= 2) {
            queueIndex = 0;  // High priority queue
        } else if (process.getPriority() <= 4) {
            queueIndex = 1;  // Medium priority queue
        } else {
            queueIndex = 2;  // Low priority queue
        }
        
        if (queueIndex < queues.size()) {
            queues.get(queueIndex).addProcess(process);
            System.out.println("Time " + currentTime + ": " + process + 
                             " assigned to " + queues.get(queueIndex).getQueueName());
        }
    }
    
    /**
     * Run the scheduler simulation
     */
    public void simulate() {
        System.out.println("\n=== Starting Multi-Level Queue Scheduling Simulation ===\n");
        
        // Sort processes by arrival time
        allProcesses.sort((p1, p2) -> Integer.compare(p1.getArrivalTime(), p2.getArrivalTime()));
        
        int processIndex = 0;
        
        while (completedProcesses.size() < allProcesses.size()) {
            // Add newly arrived processes to queues
            while (processIndex < allProcesses.size() && 
                   allProcesses.get(processIndex).getArrivalTime() <= currentTime) {
                assignProcessToQueue(allProcesses.get(processIndex));
                processIndex++;
            }
            
            // Find the highest priority non-empty queue
            Process currentProcess = null;
            SchedulerQueue currentQueue = null;
            
            for (SchedulerQueue queue : queues) {
                if (!queue.isEmpty()) {
                    currentQueue = queue;
                    currentProcess = queue.getNextProcess();
                    break;
                }
            }
            
            // If no process is ready, CPU is idle
            if (currentProcess == null) {
                System.out.println("Time " + currentTime + ": CPU IDLE");
                currentTime++;
                totalIdleTime++;
                continue;
            }
            
            // Record first response time
            if (!currentProcess.hasStarted()) {
                currentProcess.setHasStarted(true);
                currentProcess.setFirstResponseTime(currentTime);
            }
            
            // Execute process
            int executionTime;
            if (currentQueue.getAlgorithm() == SchedulerQueue.SchedulingAlgorithm.ROUND_ROBIN) {
                // Round Robin: execute for time quantum or remaining time
                executionTime = Math.min(currentQueue.getTimeQuantum(), 
                                        currentProcess.getRemainingTime());
            } else {
                // FCFS or Priority: execute until completion
                executionTime = currentProcess.getRemainingTime();
            }
            
            System.out.println("Time " + currentTime + ": Executing " + 
                             currentProcess.getProcessId() + " from " + 
                             currentQueue.getQueueName() + " for " + executionTime + " units");
            
            currentProcess.execute(executionTime);
            currentTime += executionTime;
            
            // Check if process completed
            if (currentProcess.isCompleted()) {
                currentProcess.setCompletionTime(currentTime);
                currentProcess.calculateMetrics();
                completedProcesses.add(currentProcess);
                System.out.println("Time " + currentTime + ": Process P" + 
                                 currentProcess.getProcessId() + " COMPLETED");
            } else {
                // Process not completed (Round Robin), put back in queue
                currentQueue.addProcess(currentProcess);
            }
        }
        
        System.out.println("\n=== All Processes Completed ===\n");
    }
    
    /**
     * Display performance metrics
     */
    public void displayMetrics() {
        System.out.println("=== Performance Metrics ===\n");
        
        // Individual process metrics
        System.out.println("Individual Process Metrics:");
        System.out.println("Process | Arrival | Burst | Completion | Turnaround | Waiting | Response");
        System.out.println("--------|---------|-------|------------|------------|---------|----------");
        
        for (Process p : completedProcesses) {
            System.out.printf("P%-6d | %-7d | %-5d | %-10d | %-10d | %-7d | %-8d\n",
                            p.getProcessId(),
                            p.getArrivalTime(),
                            p.getBurstTime(),
                            p.getCompletionTime(),
                            p.getTurnaroundTime(),
                            p.getWaitingTime(),
                            p.getResponseTime());
        }
        
        // Average metrics
        double avgTurnaroundTime = completedProcesses.stream()
                                     .mapToInt(Process::getTurnaroundTime)
                                     .average()
                                     .orElse(0.0);
        
        double avgWaitingTime = completedProcesses.stream()
                                  .mapToInt(Process::getWaitingTime)
                                  .average()
                                  .orElse(0.0);
        
        double avgResponseTime = completedProcesses.stream()
                                   .mapToInt(Process::getResponseTime)
                                   .average()
                                   .orElse(0.0);
        
        // CPU Utilization
        int totalBurstTime = completedProcesses.stream()
                              .mapToInt(Process::getBurstTime)
                              .sum();
        
        double cpuUtilization = ((double) totalBurstTime / currentTime) * 100;
        
        // Throughput
        double throughput = (double) completedProcesses.size() / currentTime;
        
        System.out.println("\n=== Overall Statistics ===");
        System.out.printf("Average Turnaround Time: %.2f\n", avgTurnaroundTime);
        System.out.printf("Average Waiting Time: %.2f\n", avgWaitingTime);
        System.out.printf("Average Response Time: %.2f\n", avgResponseTime);
        System.out.printf("CPU Utilization: %.2f%%\n", cpuUtilization);
        System.out.printf("Throughput: %.4f processes/unit time\n", throughput);
        System.out.printf("Total Time: %d units\n", currentTime);
        System.out.printf("Total Idle Time: %d units\n", totalIdleTime);
    }
}