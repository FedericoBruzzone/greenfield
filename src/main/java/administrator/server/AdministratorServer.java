package administrator.server;

import java.io.IOException;

import administrator.IAdministratorServer;
import util.ConfigurationHandler; 
import util.MqttClientFactory;
import util.MqttClientHandler;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;

/**
 * This class is responsible for starting the HTTP server and subscribing to the MQTT topics.
 */
public final class AdministratorServer implements IAdministratorServer {
    private ConfigurationHandler configurationHandler;
    private String serverURI;
    private HttpServer httpServer;
    private MqttAsyncClient client;
    private MqttClientHandler mqttClientHandler;
    
    /**
     * Constructs a new AdministratorServer object.
     */
    public AdministratorServer() {
        this.configurationHandler = ConfigurationHandler.getInstance();
        this.serverURI = configurationHandler.getEndpointAdministratorServer();
        this.createHttpServer(); 
        this.client = MqttClientFactory.createMqttClient();
        this.mqttClientHandler = new MqttClientHandler(client);
    }
    
    /**
     * Creates a new HTTP server.
     */
    public void createHttpServer() {
        try {
            this.httpServer = HttpServerFactory.create(serverURI + "/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Subscribes to the MQTT topics.
     */
    private void subscribeToDistricts() {
        this.mqttClientHandler.subscribeToDistrict("0");
        this.mqttClientHandler.subscribeToDistrict("1");
        this.mqttClientHandler.subscribeToDistrict("2");
        this.mqttClientHandler.subscribeToDistrict("3");
    }
   
    /**
     * Starts the HTTP server.
     */
    public void startHttpServer() {
        try {
            httpServer.start();

            this.subscribeToDistricts();

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

    public static void main(String[] args) {
        AdministratorServer administratorServer = new AdministratorServer();
        administratorServer.startHttpServer(); 
    }

}
