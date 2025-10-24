import java.util.*;

class Process {
    String pid;
    int arrival, burst, priority, remaining;

    Process(String pid, int arrival, int burst, int priority) {
        this.pid = pid;
        this.arrival = arrival;
        this.burst = burst;
        this.priority = priority;
        this.remaining = burst;
    }
}

public class SPOS_04 {

    // ---------- FCFS Scheduling ----------
    public static void fcfs(ArrayList<Process> processes) {
        Collections.sort(processes, new Comparator<Process>() {
            public int compare(Process o1, Process o2) {
                return o1.arrival - o2.arrival;
            }
        });

        int time = 0;
        for (Process p : processes) {
            if (time < p.arrival) time = p.arrival;
            System.out.println("Time " + time + " -> " + p.pid);
            time += p.burst;
        }
    }

    // ---------- SJF Preemptive Scheduling ----------
    public static void sjfPreemptive(ArrayList<Process> processes) {
        int time = 0, completed = 0;
        int n = processes.size();

        while (completed < n) {
            ArrayList<Process> ready = new ArrayList<>();
            for (Process p : processes) {
                if (p.arrival <= time && p.remaining > 0) {
                    ready.add(p);
                }
            }

            Process shortest = null;
            for (Process p : ready) {
                if (shortest == null || p.remaining < shortest.remaining) {
                    shortest = p;
                }
            }

            if (shortest != null) {
                System.out.println("Time " + time + " -> " + shortest.pid);
                shortest.remaining--;
                if (shortest.remaining == 0) completed++;
            } else {
                System.out.println("Time " + time + " -> Idle");
            }
            time++;
        }
    }

    // ---------- Priority Non-Preemptive Scheduling ----------
    public static void priorityNonPreemptive(ArrayList<Process> processes) {
        int time = 0, completed = 0;
        int n = processes.size();

        while (completed < n) {
            Process next = null;
            for (Process p : processes) {
                if (p.arrival <= time && p.remaining > 0) {
                    if (next == null || p.priority < next.priority) {
                        next = p;
                    }
                }
            }

            if (next != null) {
                System.out.println("Time " + time + " -> " + next.pid);
                time += next.burst;
                next.remaining = 0;
                completed++;
            } else {
                System.out.println("Time " + time + " -> Idle");
                time++;
            }
        }
    }

    // ---------- Round Robin Scheduling ----------
    public static void roundRobin(ArrayList<Process> processes, int quantum) {
        LinkedList<Process> queue = new LinkedList<>();
        int time = 0, i = 0;
        int n = processes.size();

        Collections.sort(processes, new Comparator<Process>() {
            public int compare(Process o1, Process o2) {
                return o1.arrival - o2.arrival;
            }
        });

        while (i < n || !queue.isEmpty()) {
            while (i < n && processes.get(i).arrival <= time) {
                queue.add(processes.get(i));
                i++;
            }

            if (!queue.isEmpty()) {
                Process p = queue.removeFirst();
                int execTime = Math.min(quantum, p.remaining);
                System.out.println("Time " + time + " -> " + p.pid);
                time += execTime;
                p.remaining -= execTime;

                while (i < n && processes.get(i).arrival <= time) {
                    queue.add(processes.get(i));
                    i++;
                }

                if (p.remaining > 0) queue.add(p);
            } else {
                System.out.println("Time " + time + " -> Idle");
                time++;
            }
        }
    }

    // ---------- Main Function ----------
    public static void main(String[] args) {
        ArrayList<Process> processes = new ArrayList<>();
        processes.add(new Process("P1", 0, 5, 2));
        processes.add(new Process("P2", 1, 3, 1));
        processes.add(new Process("P3", 2, 8, 4));
        processes.add(new Process("P4", 3, 6, 3));

        System.out.println("\n🔷 FCFS Scheduling");
        fcfs(cloneList(processes));

        System.out.println("\n🔷 SJF Preemptive Scheduling");
        sjfPreemptive(cloneList(processes));

        System.out.println("\n🔷 Priority Non-Preemptive Scheduling");
        priorityNonPreemptive(cloneList(processes));

        System.out.println("\n🔷 Round Robin Scheduling (Quantum = 2)");
        roundRobin(cloneList(processes), 2);
    }

    // ---------- Clone Helper Function ----------
    public static ArrayList<Process> cloneList(ArrayList<Process> original) {
        ArrayList<Process> copy = new ArrayList<>();
        for (Process p : original) {
            copy.add(new Process(p.pid, p.arrival, p.burst, p.priority));
        }
        return copy;
    }
}
