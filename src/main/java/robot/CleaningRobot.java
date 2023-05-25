package robot;

import util.ConfigurationHandler;
import util.MqttClientFactory;
import util.MqttClientHandler;
import common.response.IResponse;
import common.response.RobotAddResponse;
import simulator.SlidingWindow;
import simulator.PM10Simulator;
import robot.thread.ComputeAverageThread;
import robot.thread.HeartbeatThread;
import robot.thread.SendAverageThread;
import robot.thread.MeasurementStream; 
import robot.grpc.GreetingServiceImpl;
import robot.grpc.GreetingServiceClient;
import robot.grpc.HeartbeatServiceClient;
import robot.grpc.HeartbeatServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
    private volatile List<CleaningRobotInfo> activeCleaningRobots;

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

    // private GreetingServiceClient greetingServiceClient;
    // private GreetingServiceImpl greetingServiceImpl;
    // private HeartbeatServiceClient heartbeatServiceClient;
    // private HeartbeatServiceImpl heartbeatServiceImpl;
    private Server grpcServer;
    private HeartbeatThread heartbeatThread;

    public CleaningRobot() {}

    public CleaningRobot(int id) {
        // ConfigurationHandler
        this.configurationHandler = ConfigurationHandler.getInstance();

        // CleaningRobot
        this.id = id;
        this.host = this.configurationHandler.getRobotHost(); 
        this.port = String.valueOf(10000 + this.id);
        this.district = -1;
        // this.activeCleaningRobot = null;
        this.activeCleaningRobots = new ArrayList<CleaningRobotInfo>();

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
        // this.greetingServiceClient = new GreetingServiceClient();
        // this.greetingServiceImpl = new GreetingServiceImpl(this);
        this.grpcServer = ServerBuilder.forPort(Integer.valueOf(this.port))
                                  .addService(new GreetingServiceImpl(this))
                                  .addService(new HeartbeatServiceImpl(this))
                                  .build();
        
    
    }
    
    ////////////////////////////////////////////////////////////////////////////////
    //Cleaning robot                                                              //
    ////////////////////////////////////////////////////////////////////////////////
    public int getId() {
        return this.id;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public String getPort() {
        return this.port;
    }
    
    public int getDistrict() {
        return this.district;
    }
    
    public String getServerURI() {
        return this.administratorServerURI;
    }
  
    public List<CleaningRobotInfo> getActiveCleaningRobots() {
        return new ArrayList<CleaningRobotInfo>(this.activeCleaningRobots);
    }

    public void addActiveCleaningRobot(CleaningRobotInfo cleaningRobotInfo) {
        synchronized(this.activeCleaningRobots) {
            this.activeCleaningRobots.add(cleaningRobotInfo);
            System.out.println("Active cleaning robot: " + this.activeCleaningRobots);
        }
    }

    public void removeUnactiveCleaningRobot(CleaningRobotInfo cleaningRobotInfo) {
        synchronized(this.activeCleaningRobots) {
            this.activeCleaningRobots.remove(cleaningRobotInfo);
            System.out.println("Active cleaning robot: " + this.activeCleaningRobots);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Administrator server                                                       //
    ////////////////////////////////////////////////////////////////////////////////
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
        // System.out.println("Response: " + clientResponse);        
        if (clientResponse.getStatus() != 200) {
            System.exit(0);
        }
        this.district = robotAddResponse.district;
        synchronized(this.activeCleaningRobots) {
            if (robotAddResponse.listActiveCleaningRobot != null) {
                this.activeCleaningRobots.addAll(robotAddResponse.listActiveCleaningRobot
                                                                 .stream()
                                                                 .map(cr -> new CleaningRobotInfo(cr.getId(),
                                                                                                  cr.getHost(),
                                                                                                  cr.getPort(),
                                                                                                  cr.getDistrict()))
                                                                 .collect(Collectors.toList()));
            }
            System.out.println("Active cleaning robot: " + this.activeCleaningRobots);
        }
    }

    public void removeFromAdministratorServer() {
        ClientResponse clientResponse = administratorServerHandler.removeCleaningRobot(this);
        // System.out.println("Response: " + clientResponse);
    }

    public void removeCleaningRobotFromAdministratorServer(CleaningRobotInfo cleaningRobotInfo) {
        ClientResponse clientResponse = administratorServerHandler.removeCleaningRobot(cleaningRobotInfo);        
        // System.out.println("Response: " + clientResponse);
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Mqtt                                                                       //
    ////////////////////////////////////////////////////////////////////////////////
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

    public void disconnectMqttClient() {
        this.mqttClientHandler.disconnect();
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Grpc                                                                       //
    ////////////////////////////////////////////////////////////////////////////////
    public void startGrpcServer() {
        try {
            grpcServer.start();
            // System.out.println("Server started!");
            // grpcServer.awaitTermination();
        } catch (IOException e) { 
            e.printStackTrace();
        }// } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
    }

    public void stopGrpcServer() {
        grpcServer.shutdown();
    }

    public void sendGreeting(CleaningRobotInfo cleaningRobotInfo) {
        try {
            // greetingServiceClient.asynchronousStreamCall(host, port, this);
            GreetingServiceClient.asynchronousStreamCall(cleaningRobotInfo, this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendGreetingToAll() {
        synchronized(this.activeCleaningRobots) {
            this.activeCleaningRobots.forEach(cr -> {
                this.sendGreeting(cr);
            });
        }
    }
    
    public void sendHeartbeat(CleaningRobotInfo cleaningRobotInfo) {
        try {
            HeartbeatServiceClient.asynchronousStreamCall(cleaningRobotInfo, this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendHeartbeatToAll() {
        synchronized(this.activeCleaningRobots) {
            this.activeCleaningRobots.forEach(cr -> {
                this.sendHeartbeat(cr);
            });
        }
    }

    public void createHeartbeatThread() {
        this.heartbeatThread = new HeartbeatThread(this);
    }
    
    public void startHeartbeatThread() {
        this.heartbeatThread.start();
    }
    
    public void stopHeartbeatThread() {
        this.heartbeatThread.stop();
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Utility                                                                    //
    ////////////////////////////////////////////////////////////////////////////////
    public void createAllThreads() {
        this.createPm10Simulator();
        this.createComputeAverageThread();
        this.createSendAverageThread();
        this.createHeartbeatThread();
    }

    public void startAllThreads() {
        this.startPm10Simulator();
        this.startComputeAverageThread();
        this.startSendAverageThread();
        this.startHeartbeatThread();
    }

    public void stopAllThreads() {
        this.stopPm10Simulator();
        this.stopComputeAverageThread();
        this.stopSendAverageThread();
        this.stopHeartbeatThread();
    }

    public void systemExit0() {
        System.out.println("CleaningRobot " + this.getId() + " is going to exit");
        this.removeFromAdministratorServer();
        // addio a tutti
        this.stopAllThreads();
        this.disconnectMqttClient();
        this.stopGrpcServer();
        System.exit(0);
    }

    public void start() {
        this.startGrpcServer();
        this.registerToAdministratorServer();
        this.sendGreetingToAll();
        this.createAllThreads();
        this.startAllThreads();
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

            cleaningRobot.start();
            

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
                } 
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }

}
