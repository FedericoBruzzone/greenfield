package robot;

import util.ConfigurationHandler;
import util.MqttClientFactory;
import util.MqttClientHandler;
import common.response.IResponse;
import common.response.RobotAddResponse;
import simulator.SlidingWindow;
import simulator.PM10Simulator;
import robot.thread.ComputeAverageThread;
import robot.thread.SendAverageThread;
import robot.thread.MeasurementStream; 

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient; 


public class CleaningRobot implements ICleaningRobot {
    private int id;
    private int district; 
    private String administratorServerURI;
    private List<CleaningRobotInfo> activeCleaningRobot;
    private AdministratorServerHandler administratorServerHandler;
    private Client client;
    private SlidingWindow slidingWindow;
    private ConfigurationHandler configurationHandler;
    private PM10Simulator pm10Simulator;
    private ComputeAverageThread computeAverageThread;
    private SendAverageThread sendAverageThread;
    private MqttAsyncClient mqttAsyncClient; 
    private MqttClientHandler mqttClientHandler;
    private MeasurementStream measurementStream; 

    public CleaningRobot() {}

    public CleaningRobot(int id) {
        this.id = id;
        this.district = -1;
        this.activeCleaningRobot = null;
        this.configurationHandler = ConfigurationHandler.getInstance();
        this.administratorServerURI = configureAdministratorServerURI(configurationHandler); 
        this.client = Client.create();
        this.administratorServerHandler = new AdministratorServerHandler(this.client, this.administratorServerURI);
        this.slidingWindow = new SlidingWindow(
                Integer.valueOf(this.configurationHandler.getSlidingWindowSize()),
                Integer.valueOf(this.configurationHandler.getSlidingWindowOverlap())
                );
        this.mqttAsyncClient = MqttClientFactory.createMqttClient();
        this.mqttClientHandler = new MqttClientHandler(mqttAsyncClient);
        this.measurementStream = new MeasurementStream();
    }

    public int getID() {
        return this.id;
    }
    
    public String getServerURI() {
        return this.administratorServerURI;
    }

    public int getDistrict() {
        return this.district;
    }
    
    public void createPm10Simulator() {
        this.pm10Simulator = new PM10Simulator(this.slidingWindow);
    }
    
    public void createComputeAverageThread() {
        this.computeAverageThread = new ComputeAverageThread(this.slidingWindow, 
                                                             this.measurementStream);
    }

    public void createSendAverageThread() {
        this.sendAverageThread = new SendAverageThread(this.measurementStream, 
                                                       this.mqttClientHandler, 
                                                       this.getDistrict(), 
                                                       this.getID());
    } 

    public void startPm10Simulator() {
        this.pm10Simulator.start();
    }
    
    public void startComputeAverageThread() {
        this.computeAverageThread.start();
    }

    public void startSendAverageThread() {
        this.sendAverageThread.start();
    }
    
    public void stopPm10Simulator() {
        this.pm10Simulator.stop();
    }
    
    public void stopComputeAverageThread() {
        this.computeAverageThread.stop();
    }

    public void stopSendAverageThread() {
        this.sendAverageThread.stop();
    }
   
    public void disconnectMqttClient() {
        this.mqttClientHandler.disconnect();
    }

    private String configureAdministratorServerURI(ConfigurationHandler configurationHandler) {
        return configurationHandler.getEndpointAdministratorServer();
    }

    public void setAdministratorServerHandler(Client client, String administratorServerURI) {
        this.administratorServerHandler = new AdministratorServerHandler(client, administratorServerURI);
    }
    
    public void setAdministratorServerHandler(AdministratorServerHandler administratorServerHandler) {
        this.administratorServerHandler = administratorServerHandler;
    }
    
    public void registerToAdministratorServer() {
        ClientResponse clientResponse = administratorServerHandler.registerCleaningRobot(this);
        RobotAddResponse robotAddResponse = clientResponse.getEntity(RobotAddResponse.class);
        // System.out.println("Response: " + robotAddResponse);        
        System.out.println("Response: " + clientResponse);        
        this.district = robotAddResponse.district;
        this.activeCleaningRobot = robotAddResponse.listActiveCleaningRobot != null ? 
                                    robotAddResponse.listActiveCleaningRobot
                                                    .stream()
                                                    .map(cr -> new CleaningRobotInfo(cr.getId()))
                                                    .collect(Collectors.toList()) : null;
        this.createPm10Simulator();
        this.createComputeAverageThread();
        this.createSendAverageThread();
    }
    
    public void removeFromAdministratorServer() {
        ClientResponse clientResponse = administratorServerHandler.removeCleaningRobot(this);
        System.out.println("Response: " + clientResponse);
    }

    private static void printMenu() {
        System.out.println("Type:\n" +
                               "\t- 0 crash\n" +
                               "\t- 1 quit\n" +
                               "\t- 2 fix\n");
    }

    public static void main(String[] args) {
        BufferedReader inFromUserId = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Insert id: ");
        try {
            int id = Integer.parseInt(inFromUserId.readLine());  
            ICleaningRobot cleaningRobot = new CleaningRobot(id);
            cleaningRobot.registerToAdministratorServer();
            
            //TODO hello to other robots
            
            // MqttAsyncClient client = MqttClientFactory.createMqttClient();
            // MqttClientHandler mqttClientHandler = new MqttClientHandler(client); 
            
            
            cleaningRobot.startPm10Simulator();
            cleaningRobot.startComputeAverageThread();
            cleaningRobot.startSendAverageThread();

            int choice;
            while(true) { 
                printMenu();

                // String payload = String.valueOf(0 + (Math.random() * 10)); // create a random number between 0 and 10
                // System.out.println(" Publishing message: " + payload + " ...");
                // mqttClientHandler.publishMessage(payload, "0");
                // System.out.println(" Message published");

                BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
                try {
                    choice = Integer.parseInt(inFromUser.readLine());
                    switch (choice) {
                        case 0:
                            System.out.println("crash");
                            // TODO send to the other robot?
                            // cleaningRobot.removeFromAdministratorServer();
                            System.exit(1);
                        case 1:
                            System.out.println("quit");
                            // TODO complete any operation at the mechanic 
                            // TODO send to the other robot?
                            cleaningRobot.removeFromAdministratorServer();
                            
                            cleaningRobot.stopPm10Simulator();
                            cleaningRobot.stopComputeAverageThread();
                            cleaningRobot.stopSendAverageThread();
                            
                            cleaningRobot.disconnectMqttClient();

                            System.exit(0);
                        case 2:
                            System.out.println("fix");
                            // TODO
                            break;
                        default:
                            System.out.println("This choice is not available.");
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // main
    // instance of server (connect to server)
    // chiedo di entrare
    // prendo la risposta e setto il distretto per usarla come topic mqtt
    // start all threads
    //     accendi sensori
    //     saluti gli altri (per aggiungerti alla loro lista)
    //     connettiti a mqtt

}
