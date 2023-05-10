package robot;

import util.ConfigurationHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import common.response.IResponse;
import common.response.RobotAddResponse;

public class CleaningRobot implements ICleaningRobot {
    private int ID;
    private int district; 
    private String administratorServerURI;
    
    private AdministratorServerHandler administratorServerHandler;
    private Client client;

    public CleaningRobot() {}

    public CleaningRobot(int ID) {
        this.ID = ID;
        this.district = -1;
        this.administratorServerURI = configureAdministratorServerURI(); 
        this.client = Client.create();
        this.administratorServerHandler = new AdministratorServerHandler(this.client, this.administratorServerURI);
    }

    public int getID() {
        return this.ID;
    }
    
    public String getServerURI() {
        return this.administratorServerURI;
    }

    public int getDistrict() {
        return this.district;
    }
    
    public void registerToAdministratorServer() {
        ClientResponse clientResponse = administratorServerHandler.registerCleaningRobot(this);
        RobotAddResponse robotAddResponse = clientResponse.getEntity(RobotAddResponse.class);
        System.out.println("Response: " + robotAddResponse);
        // set fields
    }

    private String configureAdministratorServerURI() {
        ConfigurationHandler configurationHandler = ConfigurationHandler.getInstance();
        return configurationHandler.getEndpointAdministratorServer();
    }

    public void setAdministratorServerHandler(Client client, String administratorServerURI) {
        this.administratorServerHandler = new AdministratorServerHandler(client, administratorServerURI);
    }
    
    public void setAdministratorServerHandler(AdministratorServerHandler administratorServerHandler) {
        this.administratorServerHandler = administratorServerHandler;
    }

    public static void main(String[] args) {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Insert ID: ");
        try {
            int ID = Integer.parseInt(inFromUser.readLine());  
            ICleaningRobot cleaningRobot = new CleaningRobot(ID);
            cleaningRobot.registerToAdministratorServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
         
        
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
