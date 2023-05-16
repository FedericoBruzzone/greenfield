package robot.simulator;

import robot.simulator.Measurement;

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
        System.out.println("Adding measurement: " + m);
        this.queue.add(m);
        if(this.queue.size() >= this.size) {
            System.out.println("Notifying");
            this.notify();
        }
    }
    
    public synchronized List<Measurement> readAllAndClean() {
        if (this.queue.size() < this.size) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ArrayDeque<Measurement> queueClone = this.queue.stream()
                                                       .limit(this.size)
                                                       .collect(Collectors.toCollection(ArrayDeque::new));
        System.out.println(queueClone);

        this.queue.stream()
                  .limit(this.overlap)
                  .forEach(m -> this.queue.removeFirst());

        this.notify(); 
        return new ArrayList<Measurement>(queueClone);
    }

    // TODO: not here this method
    public double mean(List<Measurement> measurements) {
        return measurements.stream()
                           .map(m -> m.getValue())
                           .reduce((a, b) -> a + b)
                           .orElse(0.0d) / measurements.size();
    }
}
