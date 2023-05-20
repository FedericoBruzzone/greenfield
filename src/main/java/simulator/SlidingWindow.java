package simulator;

import java.util.List;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class SlidingWindow implements Buffer {

    private final int size;
    private final int overlap;
    private final Deque<Measurement> queue; 

    public SlidingWindow(int size, int overlap) {
        this.size = size;
        this.overlap = overlap;
        this.queue = new ArrayDeque<Measurement>(this.size);
    }
    
    public synchronized void addMeasurement(Measurement m) {
        // System.out.println("[SlidingWindow] addMeasurement: " + m);
        this.queue.add(m);
        if(this.queue.size() >= this.size) {
            // System.out.println("Notifying");
            this.notify();
        }
    }
    
    public synchronized List<Measurement> readAllAndClean() {
        // System.out.println("[SlidingWindow] readAllAndClean");
        while (this.queue.size() < this.size) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ArrayDeque<Measurement> queueClone = this.queue.stream()
                                                       .limit(this.size)
                                                       .collect(Collectors.toCollection(ArrayDeque::new));
        this.queue.stream()
                  .limit(this.overlap)
                  .forEach(m -> this.queue.removeFirst());

        this.notify(); 
        return new ArrayList<Measurement>(queueClone);
    }

}
