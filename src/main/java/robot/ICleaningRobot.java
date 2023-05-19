package robot;

import com.sun.jersey.api.client.Client;

public interface ICleaningRobot {
    public int getId();
    public String getHost();
    public String getPort();
    public String getServerURI();
    public int getDistrict();
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
    public void registerToAdministratorServer(); 
    public void removeFromAdministratorServer(); 
    public void setAdministratorServerHandler(Client client, String serverURI);
    public void setAdministratorServerHandler(AdministratorServerHandler administratorServerHandler);
}
