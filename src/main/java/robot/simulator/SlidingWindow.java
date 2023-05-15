package robot.simulator;

import java.util.List;

public class SlidingWindow implements Buffer {

    private final int size;
    private final Buffer buffer;

    public SlidingWindow(int size, Buffer buffer) {
        this.size = size;
        this.buffer = buffer;
    }

    public synchronized void addMeasurement(Measurement m) {
        // TODO
    }
    
    public synchronized List<Measurement> readAllAndClean() {
        //TODO
        return null;
    }

}
