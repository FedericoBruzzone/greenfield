package simulator;

import java.util.List;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * This class implements a sliding window buffer.
 *
 * <p>
 * A sliding window buffer is a buffer that stores the last <code>size</code> measurements
 * and returns them when the buffer is full. The buffer is filled with the last <code>overlap</code>
 * measurements of the previous buffer. The buffer is thread-safe.
 * </p>
 *
 * @see Buffer
 * @see Measurement
*/
public class SlidingWindow implements Buffer {

    private final int size;
    private final int overlap;
    private final Deque<Measurement> queue; 

    /**
     * Constructs a sliding window buffer.
     *
     * @param size the size of the buffer
     * @param overlap the overlap of the buffer
     */
    public SlidingWindow(int size, int overlap) {
        this.size = size;
        this.overlap = overlap;
        this.queue = new ArrayDeque<Measurement>(this.size);
    }
   
    /**
     * Adds a measurement to the buffer.
     *
     * <p>
     * If the buffer is full, the method notifies all the threads waiting on the buffer.
     * </p>
     *
     * @param m the measurement to add
     */
    public synchronized void addMeasurement(Measurement m) {
        // System.out.println("[SlidingWindow] addMeasurement: " + m);
        this.queue.add(m);
        if(this.queue.size() >= this.size) {
            // System.out.println("Notifying");
            this.notify();
        }
    }
   
    /**
     * Reads all the measurements in the buffer and cleans the buffer.
     *
     * <p>
     * If the buffer is not full, the method waits until the buffer is full.
     * If the buffer is full, the method returns the last <code>size</code> measurements
     * and cleans the buffer by removing the last <code>overlap</code> measurements.
     * The method notifies all the threads waiting on the buffer.
     * </p>
     *
     * @return the last <code>size</code> measurements
     */
    public synchronized List<Measurement> readAllAndClean() {
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
