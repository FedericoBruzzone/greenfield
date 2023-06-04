package robot.thread;

import simulator.SlidingWindow;
import simulator.Measurement;

import java.lang.Thread;
import java.util.List;


import robot.CleaningRobot;

public class ComputeAverageThread extends Thread {
    
    private SlidingWindow slidingWindow;
    private MeasurementStream measurementStream;
    private CleaningRobot cleaningRobot;
    private int id = 1;

    public ComputeAverageThread(SlidingWindow slidingWindow, MeasurementStream measurementStream, CleaningRobot cleaningRobot) {
        this.slidingWindow = slidingWindow;
        this.measurementStream = measurementStream;
        this.cleaningRobot = cleaningRobot;
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
            Measurement measurementMean = new Measurement(
                    Integer.toString(this.cleaningRobot.getId()),
                    "mean " + this.id,
                    this.mean(measurements),
                    System.currentTimeMillis() 
                    );
            // double mean = this.mean(measurements);
            measurementStream.add(measurementMean);
        }
    }
}
