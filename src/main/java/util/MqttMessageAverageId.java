package util;

import java.util.ArrayList;

import simulator.Measurement;

public class MqttMessageAverageId {
    private ArrayList<Measurement> measurementList;
    private int id;

    public MqttMessageAverageId(ArrayList<Measurement> measurementList, int id) {
        this.measurementList = measurementList;
        this.id = id;
    }

    public ArrayList<Measurement> getMeasurementList() {
        return new ArrayList<Measurement>(this.measurementList);
    }

    public int getId() {
        return this.id;
    } 
}
