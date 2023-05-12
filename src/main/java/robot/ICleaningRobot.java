package robot;

import com.sun.jersey.api.client.Client;

public interface ICleaningRobot {
    public int getID();
    public String getServerURI();
    public int getDistrict();
    public void registerToAdministratorServer(); 
    public void removeFromAdministratorServer(); 
    public void setAdministratorServerHandler(Client client, String serverURI);
    public void setAdministratorServerHandler(AdministratorServerHandler administratorServerHandler);
}
