package robot.thread;

import robot.thread.MeasurementStream;
import util.MqttClientHandler;
import util.MqttMessageAverageId;
import simulator.Measurement;

import com.google.gson.Gson;

import java.lang.Thread;
import java.util.ArrayList;

public class SendAverageThread extends Thread {
    private MeasurementStream measurementStream;
    private MqttClientHandler mqttClientHandler;
    private int district;
    private int id;

    public SendAverageThread(MeasurementStream measurementStream, 
                             MqttClientHandler mqttClientHandler, 
                             int district,
                             int id) {
        this.measurementStream = measurementStream; 
        this.mqttClientHandler = mqttClientHandler;
        this.district = district;
        this.id = id;
    }

    public void run() {
        Gson gson = new Gson();

        while (true) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ArrayList<Measurement> measurementList = measurementStream.getAndClean();
            MqttMessageAverageId mqttMessageAverageId = new MqttMessageAverageId(measurementList, this.id);

            String measurementStreamJson = gson.toJson(mqttMessageAverageId); 
            System.out.println("[SendAverageThread] send " + measurementStreamJson + " to topic " + district);
            mqttClientHandler.publishMessage(measurementStreamJson, String.valueOf(district));
        }
    }
}
