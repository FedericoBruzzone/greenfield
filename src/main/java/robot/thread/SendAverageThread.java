package robot.thread;

import robot.thread.MeasurementStream;
import util.MqttClientHandler;

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
        String payload = String.valueOf(0 + (Math.random() * 10)); // create a random number between 0 and 10
        System.out.println(" Publishing message: " + payload + " ...");
        mqttClientHandler.publishMessage(payload, "0");
        System.out.println(" Message published");
    }
}
