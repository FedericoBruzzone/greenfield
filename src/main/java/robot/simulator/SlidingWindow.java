package robot.simulator;

import robot.simulator.Measurement;

import java.util.List;
import java.util.Deque;
import java.util.ArrayDeque;

public class SlidingWindow implements Buffer {

    private final int size;
    private final Buffer buffer;
    private final Deque<Measurement> queue; 

    public SlidingWindow(int size, Buffer buffer) {
        this.size = size;
        this.buffer = buffer;
        this.queue = new ArrayDeque<Measurement>();
    }

    public synchronized void addMeasurement(Measurement m) {
        while(this.queue.size() == this.size) {
            try {
                this.wait();
            } catch(InterruptedException e) { 
                e.printStackTrace();
            }
        }
        this.queue.add(m);
        

    }
    
    public synchronized List<Measurement> readAllAndClean() {
        //TODO
        return null;
    }

}
