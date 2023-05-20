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
import robot.grpc.GreetingServiceImpl;
import robot.grpc.GreetingServiceClient;

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

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class CleaningRobot implements ICleaningRobot {
    private ConfigurationHandler configurationHandler;

    private int id;
    private String host;
    private String port;
    private int district; 
    private List<CleaningRobotInfo> activeCleaningRobot;

    private Client client;
    private String administratorServerURI;
    private AdministratorServerHandler administratorServerHandler;

    private SlidingWindow slidingWindow;
    private MqttAsyncClient mqttAsyncClient; 
    private MqttClientHandler mqttClientHandler;
    private MeasurementStream measurementStream; 

    private PM10Simulator pm10Simulator;
    private ComputeAverageThread computeAverageThread;
    private SendAverageThread sendAverageThread;

    private Server grpcServer;
    

    public CleaningRobot() {}

    public CleaningRobot(int id) {
        // ConfigurationHandler
        this.configurationHandler = ConfigurationHandler.getInstance();

        // CleaningRobot
        this.id = id;
        this.host = this.configurationHandler.getRobotHost(); 
        this.port = String.valueOf(10000 + this.id);
        this.district = -1;
        this.activeCleaningRobot = null;

        // AdministratorServer
        this.client = Client.create();
        this.administratorServerURI = configureAdministratorServerURI(configurationHandler); 
        this.administratorServerHandler = new AdministratorServerHandler(this.client, this.administratorServerURI);
        
        // MQTT
        this.slidingWindow = new SlidingWindow(
                Integer.valueOf(this.configurationHandler.getSlidingWindowSize()),
                Integer.valueOf(this.configurationHandler.getSlidingWindowOverlap())
                );
        this.mqttAsyncClient = MqttClientFactory.createMqttClient();
        this.mqttClientHandler = new MqttClientHandler(mqttAsyncClient);
        this.measurementStream = new MeasurementStream();

        // GRPC
        GreetingServiceClient greetingServiceClient = new GreetingServiceClient();
        GreetingServiceImpl greetingServiceImpl = new GreetingServiceImpl(this);
        grpcServer = ServerBuilder.forPort(Integer.valueOf(this.port))
                                  .addService(greetingServiceImpl)
                                  .build();

    }

    public int getId() {
        return this.id;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public String getPort() {
        return this.port;
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
                                                       this.getId());
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
    
    public void createAllThreads() {
        this.createPm10Simulator();
        this.createComputeAverageThread();
        this.createSendAverageThread();
    }

    public void startAllThreads() {
        this.startPm10Simulator();
        this.startComputeAverageThread();
        this.startSendAverageThread();
    }

    public void stopAllThreads() {
        this.stopPm10Simulator();
        this.stopComputeAverageThread();
        this.stopSendAverageThread();
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
        if (clientResponse.getStatus() != 200) {
            System.exit(0);
        }
        this.district = robotAddResponse.district;
        this.activeCleaningRobot = robotAddResponse.listActiveCleaningRobot != null ? 
                                    robotAddResponse.listActiveCleaningRobot
                                                    .stream()
                                                    .map(cr -> new CleaningRobotInfo(cr.getId(),
                                                                                     cr.getHost(),
                                                                                     cr.getPort()))
                                                    .collect(Collectors.toList()) : null;
    }
    
    public void removeFromAdministratorServer() {
        ClientResponse clientResponse = administratorServerHandler.removeCleaningRobot(this);
        System.out.println("Response: " + clientResponse);
    }

    public void startGrpcServer() {
        try {
            grpcServer.start();
            System.out.println("Server started!");
            grpcServer.awaitTermination();
        } catch (IOException e) { 
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopGrpcServer() {
        grpcServer.shutdown();
    }

    public void sayGreeting() {
        // TODO
        fsdkjm
    }

    public void sayGreetingToAll() {
        // TODO
        fsd,kmh
    }
    
    public void systemExit0() {
        System.out.println("CleaningRobot " + this.getId() + " is going to exit");
        this.removeFromAdministratorServer();
        this.stopAllThreads();
        this.disconnectMqttClient();
        this.stopGrpcServer();
        System.exit(0);
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
            cleaningRobot.createAllThreads();
            cleaningRobot.startAllThreads();
            cleaningRobot.startGrpcServer();
            //TODO hello to other robots
            

            int choice;
            while(true) { 
                printMenu();

                BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
                try {
                    choice = Integer.parseInt(inFromUser.readLine());
                    switch (choice) {
                        case 0:
                            System.out.println("crash");
                            System.exit(1);
                        case 1:
                            System.out.println("quit");
                            // TODO complete any operation at the mechanic 
                            // TODO send to the other robot?
                            cleaningRobot.systemExit0();
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
                } finally {
                    cleaningRobot.systemExit0();
                    System.out.println("Bye bye");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }

}
