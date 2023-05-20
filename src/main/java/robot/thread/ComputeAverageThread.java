package robot.thread;

import simulator.SlidingWindow;
import simulator.Measurement;
import robot.thread.MeasurementStream;

import java.lang.Thread;
import java.util.List;

public class ComputeAverageThread extends Thread {
    
    private SlidingWindow slidingWindow;
    private MeasurementStream measurementStream;

    public ComputeAverageThread(SlidingWindow slidingWindow, MeasurementStream measurementStream) {
        this.slidingWindow = slidingWindow;
        this.measurementStream = measurementStream;
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
            measurementStream.add(mean);
        }
    }
}
