package administrator.server;

import administrator.IAdministratorServer;
import util.ConfigurationHandler; 
import util.MqttClientFactory;
import util.MqttClientHandler;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;

public final class AdministratorServer implements IAdministratorServer {
    private static void mqttInitializer() {
        MqttAsyncClient client = MqttClientFactory.createMqttClient();
        MqttClientHandler mqttClientHandler = new MqttClientHandler(client); 
        mqttClientHandler.subscribeToDistrict("0");
        mqttClientHandler.subscribeToDistrict("1");
        mqttClientHandler.subscribeToDistrict("2");
        mqttClientHandler.subscribeToDistrict("3");
    }

    public static void main(String[] args) {
        ConfigurationHandler configurationHandler = ConfigurationHandler.getInstance();
        String serverURI = configurationHandler.getEndpointAdministratorServer();
        try {
            final HttpServer httpServer = HttpServerFactory.create(serverURI + "/");

            httpServer.start();

            mqttInitializer();

            System.out.println("Server running!");
            System.out.println("Server started on: " + serverURI);

            System.out.println("Hit return to stop...");
            System.in.read();
            System.out.println("Stopping server");
            httpServer.stop(0);
            System.out.println("Server stopped");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
