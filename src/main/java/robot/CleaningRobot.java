package robot;

import util.ConfigurationHandler;
import util.MqttClientFactory;
import util.MqttClientHandler;
import common.response.RobotAddResponse;
import simulator.SlidingWindow;
import simulator.PM10Simulator;
import robot.thread.ComputeAverageThread;
import robot.thread.HeartbeatThread;
import robot.thread.MalfunctionsThread;
import robot.thread.SendAverageThread;
import robot.thread.MeasurementStream; 
import robot.grpc.GreetingServiceImpl;
import robot.grpc.GreetingServiceClient;
import robot.grpc.GoodbyeServiceImpl;
import robot.grpc.GoodbyeServiceClient;
import robot.grpc.HeartbeatServiceClient;
import robot.grpc.HeartbeatServiceImpl;
import robot.grpc.BrokenServiceClient;
import robot.grpc.BrokenServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
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
    
    private Boolean isBroken;
    private CleaningRobotInfo myTimestampRequestImBroken;
    private List<CleaningRobotInfo> cleaningRobotsWithTimestampGreaterThanMine;
    private HashMap<CleaningRobotInfo, Boolean> responseCleaningRobotsISentThatImBroken;
    private MalfunctionsThread malfunctionsThread;

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
        this.activeCleaningRobots = new ArrayList<CleaningRobotInfo>();

        this.isBroken = false;
        this.myTimestampRequestImBroken = new CleaningRobotInfo(this.id, this.host, this.port, this.district, -1);
        this.responseCleaningRobotsISentThatImBroken = new HashMap<CleaningRobotInfo, Boolean>();
        this.cleaningRobotsWithTimestampGreaterThanMine = new ArrayList<CleaningRobotInfo>();
        
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
                                       .addService(new GoodbyeServiceImpl(this))
                                       .addService(new HeartbeatServiceImpl(this))
                                       .addService(new BrokenServiceImpl(this))
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
            this.activeCleaningRobots.removeIf(cr -> cr.id == cleaningRobotInfo.id);
            System.out.println("Active cleaning robot: " + this.activeCleaningRobots);
        }
    }

    public void removeUnactiveCleaningRobot(int cleaningRobotId) {
        synchronized(this.activeCleaningRobots) {
            this.activeCleaningRobots.removeIf(cr -> cr.id == cleaningRobotId);
            System.out.println("Active cleaning robot: " + this.activeCleaningRobots);
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////////
    // Mechanical                                                                 //
    ////////////////////////////////////////////////////////////////////////////////
    public void setIsBroken(Boolean isBroken) {
        synchronized(this.isBroken) {
            this.isBroken = isBroken;
        }
    }

    public Boolean getIsBroken() {
        synchronized(this.isBroken) {
            return this.isBroken;
        }
    }
  
    public void setMyTimestampRequestImBroken(long timestamp) {
        synchronized(this.myTimestampRequestImBroken) {
            this.myTimestampRequestImBroken.timestamp = timestamp;
        }
    }

    public long getMyTimestampRequestImBroken() {
        synchronized(this.myTimestampRequestImBroken) {
            return this.myTimestampRequestImBroken.timestamp;
        }
    }
    
    public void addCleaningRobotsWithTimestampGreaterThanMine(CleaningRobotInfo cleaningRobotInfo) {
        synchronized(this.cleaningRobotsWithTimestampGreaterThanMine) {
            this.cleaningRobotsWithTimestampGreaterThanMine.add(cleaningRobotInfo);
        }
    }

    public void removeCleaningRobotsWithTimestampGreaterThanMine(CleaningRobotInfo cleaningRobotInfo) {
        synchronized(this.cleaningRobotsWithTimestampGreaterThanMine) {
            this.cleaningRobotsWithTimestampGreaterThanMine.removeIf(cr -> cr.id == cleaningRobotInfo.id);
        }
    }

    public void removeAllCleaningRobotsWithTimestampGreaterThanMine(CleaningRobotInfo cleaningRobotInfo) {
        synchronized(this.cleaningRobotsWithTimestampGreaterThanMine) {
            this.cleaningRobotsWithTimestampGreaterThanMine.clear();
        }
    } 

    public void setResponseCleaningRobotsISentThatImBroken(CleaningRobotInfo cleaningRobotInfo, Boolean response) {
        synchronized(this.responseCleaningRobotsISentThatImBroken) {
            this.responseCleaningRobotsISentThatImBroken.put(cleaningRobotInfo, response);
        }
    }

    public HashMap<CleaningRobotInfo, Boolean> getResponseCleaningRobotsISentThatImBroken() {
        synchronized(this.responseCleaningRobotsISentThatImBroken) {
            return new HashMap<CleaningRobotInfo, Boolean>(this.responseCleaningRobotsISentThatImBroken);
        }
    }

    public void sendImBroken(CleaningRobotInfo cleaningRobotInfo, CleaningRobotInfo myTimestampRequestImBroken) {
        System.out.println("Cleaning robot " + this.id + " sent to cleaning robot " + cleaningRobotInfo.id + " that it is broken");
        try {
            BrokenServiceClient.asynchronousStreamCall(cleaningRobotInfo, this, myTimestampRequestImBroken);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void sendImBrokenToAll() {
        synchronized(this.activeCleaningRobots) {
            this.activeCleaningRobots.forEach(cleaningRobotInfo -> {
                this.responseCleaningRobotsISentThatImBroken.put(cleaningRobotInfo, false);
            });
        }
        long timestamp = System.currentTimeMillis();
        this.myTimestampRequestImBroken = new CleaningRobotInfo(this.id, 
                                                                this.host, 
                                                                this.port, 
                                                                this.district, 
                                                                timestamp); 
        this.responseCleaningRobotsISentThatImBroken.forEach((cleaningRobotInfo, response) -> {
                sendImBroken(cleaningRobotInfo, myTimestampRequestImBroken);
        });
    }

    public void sendImFixedToCleaningRobotsWithTimestampGreaterThanMine() {
        synchronized(this.cleaningRobotsWithTimestampGreaterThanMine) {
             
        } 
    }

    public void createMalfunctionsThread() {
        this.malfunctionsThread = new MalfunctionsThread(this);
    }

    public void startMalfunctionsThread() {
        this.malfunctionsThread.start();
    }

    public void stopMalfunctionsThread() {
        this.malfunctionsThread.stop();
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
        // System.out.println("Response: " + clientResponse);        
        if (clientResponse.getStatus() != 200) {
            System.out.println("Failed [" +clientResponse.getStatus()+ "]: There is another Cleaning Robot with this ID");
            System.exit(0);
        }
        RobotAddResponse robotAddResponse = clientResponse.getEntity(RobotAddResponse.class);
        // System.out.println("Response: " + robotAddResponse);        

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
                                                             this.measurementStream,
                                                             this);
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
    
    public void sendGoodbye(CleaningRobotInfo cleaningRobotInfo) {
        try {
            GoodbyeServiceClient.asynchronousStreamCall(cleaningRobotInfo, this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendGoodbyeToAll() {
        synchronized(this.activeCleaningRobots) {
            this.activeCleaningRobots.forEach(cr -> {
                this.sendGoodbye(cr);
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

    public void sendHeartbeatCrash(CleaningRobotInfo cleaningRobotInfo, int cleaningRobotIdCrashed) {
        try {
            HeartbeatServiceClient.asynchronousStreamCallCrash(cleaningRobotInfo, cleaningRobotIdCrashed);
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

    public void sendHeartbeatCrashToAll(int cleaningRobotIdCrashed) {
        synchronized(this.activeCleaningRobots) {
            this.activeCleaningRobots.forEach(cr -> {
                this.sendHeartbeatCrash(cr, cleaningRobotIdCrashed);
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
        this.createMalfunctionsThread();
    }

    public void startAllThreads() {
        this.startPm10Simulator();
        this.startComputeAverageThread();
        this.startSendAverageThread();
        this.startHeartbeatThread();
        this.startMalfunctionsThread();
    }

    public void stopAllThreads() {
        this.stopPm10Simulator();
        this.stopComputeAverageThread();
        this.stopSendAverageThread();
        this.stopHeartbeatThread();
        this.stopMalfunctionsThread();
    }

    public void systemExit0() {
        // TODO complete any operation at the mechanic 
        this.removeFromAdministratorServer();
        this.sendGoodbyeToAll(); 
        this.stopAllThreads();
        this.disconnectMqttClient();
        this.stopGrpcServer();
        System.exit(0);
    }

    public void start() {
        this.registerToAdministratorServer();
        this.startGrpcServer();
        this.sendGreetingToAll();
        this.createAllThreads();
        this.startAllThreads();
    }

    private static void printMenu() {
        System.out.println("Type:\n" +
                               "\t- 0 Crash\n" +
                               "\t- 1 Quit\n" +
                               "\t- 2 Fix\n");
    }

    public static void main(String[] args) {
        BufferedReader inFromUserId = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Insert id: ");
        try {
            int id = Integer.parseInt(inFromUserId.readLine());  
            ICleaningRobot cleaningRobot = new CleaningRobot(id);

            cleaningRobot.start();
            
            int choice;
            printMenu();
            while(true) { 

                System.out.println("Insert your choice: ");
                BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
                try {
                    choice = Integer.parseInt(inFromUser.readLine());
                    switch (choice) {
                        case 0:
                            System.out.println("\tCrash");
                            System.exit(1);
                        case 1:
                            System.out.println("\tQuit");
                            cleaningRobot.systemExit0();
                        case 2:
                            System.out.println("\tFix");
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
