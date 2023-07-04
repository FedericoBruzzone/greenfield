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
    private volatile Boolean stop;

    public ComputeAverageThread(SlidingWindow slidingWindow, MeasurementStream measurementStream, CleaningRobot cleaningRobot) {
        this.slidingWindow = slidingWindow;
        this.measurementStream = measurementStream;
        this.cleaningRobot = cleaningRobot;
        this.stop = false;
    }

    public double mean(List<Measurement> measurements) {
        return measurements.stream()
                           .map(m -> m.getValue())
                           .reduce((a, b) -> a + b)
                           .orElse(0.0d) / measurements.size();
    }

    public void stopMeGently() {
        this.stop = true; 
    }

    public void run() {
        while (!stop) {
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
