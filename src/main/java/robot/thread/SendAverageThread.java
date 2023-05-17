package robot.thread;

import robot.thread.MeasurementStream;
import util.MqttClientHandler;
import com.google.gson.Gson;

import java.lang.Thread;

public class SendAverageThread extends Thread {
    // Send to the Administrator Server the list of average values
   
    private MeasurementStream measurementStream;
    private MqttClientHandler mqttClientHandler;
    private int district;

    public SendAverageThread(MeasurementStream measurementStream, MqttClientHandler mqttClientHandler, int district) {
        this.measurementStream = measurementStream; 
        this.mqttClientHandler = mqttClientHandler;
        this.district = district;
    }

    public void run() {
        // every 15 second getAndClean from measurementStream and publish it to the correct topic 
        Gson gson = new Gson();

        while (true) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String measurementStreamJson = gson.toJson(measurementStream.getAndClean()); 
            System.out.println("[SendAverageThread] send " + measurementStreamJson + " to topic " + district);
            mqttClientHandler.publishMessage(measurementStreamJson, String.valueOf(district));
        }
    }
}
