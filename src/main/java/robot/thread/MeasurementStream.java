package robot.thread;

import java.util.ArrayList;

import simulator.Measurement;

public class MeasurementStream {

    public ArrayList<Measurement> measurementStream; 

    public MeasurementStream() {
        measurementStream = new ArrayList<Measurement>();
    }

    public synchronized void add(Measurement value) {
        measurementStream.add(value);
        this.notify();
    }

    public synchronized ArrayList<Measurement> getAndClean() {
        while(measurementStream.size() == 0) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ArrayList<Measurement> measurementStreamClone = (measurementStream.size() > 0) ? 
                                        new ArrayList<>(measurementStream) :
                                        new ArrayList<>();
       
        measurementStream.clear();

        return measurementStreamClone;
    }

}

