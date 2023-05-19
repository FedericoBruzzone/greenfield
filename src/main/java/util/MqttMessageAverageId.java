package util;

import java.util.ArrayList;

public class MqttMessageAverageId {
    private ArrayList<Double> measurementList;
    private int id;

    public MqttMessageAverageId(ArrayList<Double> measurementList, int id) {
        this.measurementList = measurementList;
        this.id = id;
    }

    public ArrayList<Double> getMeasurementList() {
        return this.measurementList;
    }

    public int getId() {
        return this.id;
    } 
}
