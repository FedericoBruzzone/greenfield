package util;

import util.ConfigurationHandler;

import java.sql.Timestamp;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class MqttClientHandler {
    private final MqttAsyncClient client;
    private final ConfigurationHandler configurationHandler;

    public MqttClientHandler(MqttAsyncClient client) {
        this.client = client;
        checkConnection();
        this.configurationHandler = ConfigurationHandler.getInstance();
        // System.out.println(configurationHandler.getMqttTopic());
    }

    private void checkConnection() {
        if (!this.client.isConnected()) {
            try {
                this.client.connect().waitForCompletion();
            } catch (MqttException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    } 
  
    public void disconnect() {
        try {
            this.client.disconnect().waitForCompletion();
        } catch (MqttException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void subscribeToDistrict(String district) {
        this.client.setCallback(new MqttCallback() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                // System.out.println("Message arrived: " + new String(message.getPayload()));
                String time = new Timestamp(System.currentTimeMillis()).toString();
                    String receivedMessage = new String(message.getPayload());
                    System.out.println(client.getClientId() +" Received a Message! - Callback - Thread PID: " + Thread.currentThread().getId() +
                            "\n\tTime:    " + time +
                            "\n\tTopic:   " + topic +
                            "\n\tMessage: " + receivedMessage +
                            "\n\tQoS:     " + message.getQos() + "\n");
                    
                    // convert the message from json to ArrayList
                    // and add it to the Queue Static

                    // System.out.println("\n ***  Press a random key to exit *** \n");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // System.out.println("Delivery complete");
            }

            @Override
            public void connectionLost(Throwable cause) {
                System.out.println(client.getClientId() + " Connection lost! cause:" + cause.getMessage()+ "-  Thread PID: " + Thread.currentThread().getId());
                // System.out.println("Connection lost");
            }
        });
        try {
            this.client.subscribe(configurationHandler.getMqttTopic() + district, 2).waitForCompletion();
        } catch (MqttException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void unSubscribeToDistrict(String district) {
        try {
            this.client.unsubscribe(configurationHandler.getMqttTopic() + district).waitForCompletion();
        } catch (MqttException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void publishMessage(String message, String district) {
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        mqttMessage.setQos(2);
        try {
            this.client.publish(configurationHandler.getMqttTopic() + district, mqttMessage).waitForCompletion();
        } catch (MqttException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
