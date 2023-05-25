package robot;

import util.RestHandler;
import common.CommonCleaningRobot;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

public class AdministratorServerHandler {
    private final Client client;
    private final String administratorServerURI;

    public AdministratorServerHandler(Client client, String administratorServerURI) {
        this.client = client;
        this.administratorServerURI = administratorServerURI;
    }

    public ClientResponse registerCleaningRobot(ICleaningRobot cleaningRobot) {
        CommonCleaningRobot commonCleaningRobot = new CommonCleaningRobot(cleaningRobot.getId(), 
                                                                          cleaningRobot.getHost(), 
                                                                          cleaningRobot.getPort());
        ClientResponse clientResponse = RestHandler.postRequest(client, administratorServerURI+"/robot/add", commonCleaningRobot);
        // if (clientResponse.getStatus() != 200) {
        //     throw new RuntimeException("Failed [" +clientResponse.getStatus()+ "]: There is another Cleaning Robot with this ID ");
        // } 
        return clientResponse;
    }

    public ClientResponse removeCleaningRobot(ICleaningRobot cleaningRobot) {
        CommonCleaningRobot commonCleaningRobot = new CommonCleaningRobot(cleaningRobot.getId(), 
                                                                          cleaningRobot.getHost(), 
                                                                          cleaningRobot.getPort());        
        ClientResponse clientResponse = RestHandler.deleteRequest(client, administratorServerURI+"/robot/remove", commonCleaningRobot);
        if (clientResponse.getStatus() != 200) {
            throw new RuntimeException("Failed [" +clientResponse.getStatus()+ "]: There is no Cleaning Robot with this ID ");
        }
        return clientResponse;
    }

    public ClientResponse removeCleaningRobot(CleaningRobotInfo cleaningRobotInfo) {
        CommonCleaningRobot commonCleaningRobot = new CommonCleaningRobot(cleaningRobotInfo.id, 
                                                                          cleaningRobotInfo.host, 
                                                                          cleaningRobotInfo.port);        
        ClientResponse clientResponse = RestHandler.deleteRequest(client, administratorServerURI+"/robot/remove", commonCleaningRobot);
        if (clientResponse.getStatus() != 200) {
            throw new RuntimeException("Failed [" +clientResponse.getStatus()+ "]: There is no Cleaning Robot with this ID ");
        }
        return clientResponse;
    }

}
