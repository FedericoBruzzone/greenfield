package robot;

import util.ConfigurationHandler;
import util.RestHandler;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class CleaningRobot implements ICleaningRobot {
    
    public static void main(String[] args) {
        ConfigurationHandler configurationHandler = ConfigurationHandler.getInstance();
        String serverURI = configurationHandler.getEndpointAdministratorServer();
        
        Client client = Client.create();
        
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
