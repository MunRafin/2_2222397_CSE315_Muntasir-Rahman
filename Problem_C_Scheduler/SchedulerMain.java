/**
 * Main Program for Multi-Level Queue CPU Scheduler
 * Demonstrates scheduling with multiple queues and algorithms
 */
public class SchedulerMain {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║   Multi-Level Queue CPU Scheduler Simulation      ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        
        // Create scheduler
        MultiLevelScheduler scheduler = new MultiLevelScheduler();
        
        // Create three queues with different algorithms
        // Queue 0: High Priority - Round Robin (quantum = 2)
        SchedulerQueue highPriorityQueue = new SchedulerQueue(
            "High Priority Queue",
            SchedulerQueue.SchedulingAlgorithm.ROUND_ROBIN,
            2
        );
        
        // Queue 1: Medium Priority - Round Robin (quantum = 4)
        SchedulerQueue mediumPriorityQueue = new SchedulerQueue(
            "Medium Priority Queue",
            SchedulerQueue.SchedulingAlgorithm.ROUND_ROBIN,
            4
        );
        
        // Queue 2: Low Priority - FCFS
        SchedulerQueue lowPriorityQueue = new SchedulerQueue(
            "Low Priority Queue",
            SchedulerQueue.SchedulingAlgorithm.FCFS,
            0
        );
        
        // Add queues to scheduler (order matters - higher priority first)
        scheduler.addQueue(highPriorityQueue);
        scheduler.addQueue(mediumPriorityQueue);
        scheduler.addQueue(lowPriorityQueue);
        
        System.out.println("\nQueue Configuration:");
        System.out.println("  1. High Priority Queue (Priority 1-2): Round Robin (Quantum=2)");
        System.out.println("  2. Medium Priority Queue (Priority 3-4): Round Robin (Quantum=4)");
        System.out.println("  3. Low Priority Queue (Priority 5+): FCFS");
        
        // Create sample processes
        // Process(ID, ArrivalTime, BurstTime, Priority)
        System.out.println("\nCreating Processes:");
        
        Process p1 = new Process(1, 0, 8, 1);   // High priority
        Process p2 = new Process(2, 1, 4, 3);   // Medium priority
        Process p3 = new Process(3, 2, 9, 5);   // Low priority
        Process p4 = new Process(4, 3, 5, 2);   // High priority
        Process p5 = new Process(5, 4, 3, 4);   // Medium priority
        Process p6 = new Process(6, 5, 7, 1);   // High priority
        Process p7 = new Process(7, 6, 6, 5);   // Low priority
        Process p8 = new Process(8, 7, 2, 3);   // Medium priority
        
        scheduler.addProcess(p1);
        scheduler.addProcess(p2);
        scheduler.addProcess(p3);
        scheduler.addProcess(p4);
        scheduler.addProcess(p5);
        scheduler.addProcess(p6);
        scheduler.addProcess(p7);
        scheduler.addProcess(p8);
        
        System.out.println("  " + p1);
        System.out.println("  " + p2);
        System.out.println("  " + p3);
        System.out.println("  " + p4);
        System.out.println("  " + p5);
        System.out.println("  " + p6);
        System.out.println("  " + p7);
        System.out.println("  " + p8);
        
        // Run simulation
        scheduler.simulate();
        
        // Display metrics
        scheduler.displayMetrics();
        
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║           Simulation Completed Successfully       ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
    }
}