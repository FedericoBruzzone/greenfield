package robot.thread;

import robot.simulator.SlidingWindow;
import robot.simulator.Measurement;

import java.lang.Thread;
import java.util.List;

public class ComputeAverageThread extends Thread {
    
    private final SlidingWindow slidingWindow;

    public ComputeAverageThread(SlidingWindow slidingWindow) {
        this.slidingWindow = slidingWindow;
    }

    public double mean(List<Measurement> measurements) {
        return measurements.stream()
                           .map(m -> m.getValue())
                           .reduce((a, b) -> a + b)
                           .orElse(0.0d) / measurements.size();
    }

    public void run() {
        while (true) {
            List<Measurement> measurements = this.slidingWindow.readAllAndClean();
            double mean = this.mean(measurements);
            System.out.println("Mean: " + mean);
        }
    }
}
