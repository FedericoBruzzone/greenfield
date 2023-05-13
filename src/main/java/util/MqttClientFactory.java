package util;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;

public class MqttClientFactory {
    private final static ConfigurationHandler configurationHandler;
    private final static String broker;

    static {
        configurationHandler = ConfigurationHandler.getInstance();
        broker = configurationHandler.getEndpointBroker();
    }

    public static MqttClient createMqttClient() {
        try {
            String clientId = MqttClient.generateClientId(); 
            // MqttAsyncClient mqttClient = new MqttAsyncClient(broker, clientId);
            MqttClient mqttClient = new MqttClient(broker, clientId);
            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setCleanSession(true);
            mqttConnectOptions.setAutomaticReconnect(true);
            mqttConnectOptions.setConnectionTimeout(10);
            mqttConnectOptions.setKeepAliveInterval(10);
            mqttClient.connect(mqttConnectOptions);

            return mqttClient;
        } catch (MqttException e) {
            e.printStackTrace();
            return null;
        } 
    }
}
