package robot.thread;

import java.util.ArrayList;

public class MeasurementStream {

    public ArrayList<Double> measurementStream; 

    public MeasurementStream() {
        measurementStream = new ArrayList<Double>();
    }

    public synchronized void add(double value) {
        measurementStream.add(value);
        this.notify();
    }

    public synchronized ArrayList<Double> getAndClean() {
        while(measurementStream.size() == 0) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ArrayList<Double> measurementStreamClone = (measurementStream.size() > 0) ? 
                                        new ArrayList<>(measurementStream) :
                                        new ArrayList<>();
        
        return measurementStreamClone;
    }

}

