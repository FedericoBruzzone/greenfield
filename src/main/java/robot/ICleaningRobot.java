package robot;

import java.util.List;
import com.sun.jersey.api.client.Client;

public interface ICleaningRobot {
    public int getId();
    public String getHost();
    public String getPort();
    public int getDistrict();
    public String getServerURI();
    public List<CleaningRobotInfo> getActiveCleaningRobots();
    public void addActiveCleaningRobot(CleaningRobotInfo cleaningRobotInfo);
    public void removeUnactiveCleaningRobot(CleaningRobotInfo cleaningRobotInfo);
    
    public void setAdministratorServerHandler(Client client, String serverURI);
    public void setAdministratorServerHandler(AdministratorServerHandler administratorServerHandler);
    public void registerToAdministratorServer(); 
    public void removeFromAdministratorServer(); 
    public void removeCleaningRobotFromAdministratorServer(CleaningRobotInfo cleaningRobotInfo);
    public void removeUnactiveCleaningRobot(int cleaningRobotId);
    
    public void createPm10Simulator();
    public void createComputeAverageThread();
    public void createSendAverageThread();
    public void startPm10Simulator();
    public void startComputeAverageThread();
    public void startSendAverageThread();
    public void stopPm10Simulator();
    public void stopComputeAverageThread();
    public void stopSendAverageThread();
    public void disconnectMqttClient();

    public void startGrpcServer();
    public void stopGrpcServer();
    public void sendGreeting(CleaningRobotInfo cleaningRobotInfo);
    public void sendGreetingToAll();
    public void sendGoodbye(CleaningRobotInfo cleaningRobotInfo);
    public void sendGoodbyeToAll();
    public void sendHeartbeat(CleaningRobotInfo cleaningRobotInfo);
    public void sendHeartbeatCrash(CleaningRobotInfo cleaningRobotInfo, int cleaningRobotIdCrashed); 
    public void sendHeartbeatToAll();
    public void sendHeartbeatCrashToAll(int cleaningRobotIdCrashed);
    public void createHeartbeatThread();
    public void startHeartbeatThread();
    public void stopHeartbeatThread();
    

    public void createAllThreads();
    public void startAllThreads();
    public void stopAllThreads();
    public void systemExit0();
    public void start();
}
