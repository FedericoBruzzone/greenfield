package administrator.server;

import administrator.IAdministratorServer;

import util.IConfigurationHandler; 
import util.ConfigurationHandler; 

public final class AdministratorServer implements IAdministratorServer  {
    public static void main(String[] args) {
        IConfigurationHandler configurationHandler = ConfigurationHandler.instance();
        System.out.println("AdministratorServer: " + configurationHandler.getEndpointAdministratorServer()); 
    }
}
