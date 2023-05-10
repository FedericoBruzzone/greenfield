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
        CommonCleaningRobot commonCleaningRobot = new CommonCleaningRobot(cleaningRobot.getID());
        ClientResponse clientResponse = RestHandler.postRequest(client, administratorServerURI+"/robot/add", commonCleaningRobot);
        if (clientResponse.getStatus() != 200) {
            throw new RuntimeException("Failed [" +clientResponse.getStatus()+ "]: There is another Cleaning Robot with this ID ");
        }
        return clientResponse;
    }

}