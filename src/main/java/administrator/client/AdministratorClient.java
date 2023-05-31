package administrator.client;

import administrator.IAdministratorClient;
import util.ConfigurationHandler;
import util.RestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.WebResource;

public final class AdministratorClient implements IAdministratorClient  {

    private static void getCleaningRobotsList(Client client, String baseUri) {
        String getPath = "/administratorclient/cleaningrobotslist";
        ClientResponse clientResponse = RestHandler.getRequest(client, baseUri + getPath);
        String string = clientResponse.getEntity(String.class);
        System.out.println(string);
    }
    
    private static void getAverageOfLastNAirPollutionLevelsOfRobot(Client client, String baseUri, int robotId, int numberOfLast) {
        String getPath = "/administratorclient/averageoflastnairpollutionlevelsofrobot?robotId="+robotId+"&numberOfLast="+numberOfLast;
        ClientResponse clientResponse = RestHandler.getRequest(client, baseUri + getPath);
        String string = clientResponse.getEntity(String.class);
        System.out.println(string);
    }
    
    private static void getHelloWorld(Client client, String baseUri) {
        String getPath = "/administratorclient";
        ClientResponse clientResponse = RestHandler.getRequest(client, baseUri + getPath);
        System.out.println(clientResponse.toString());
        String string = clientResponse.getEntity(String.class);
        System.out.println(string);
    }
    
    private static void printMenu() {
            System.out.println("Type:\n" +
                                   "\t- 0 The list of the cleaning robots currently located in Greenfield\n" +
                                   "\t- 1 The average of the last n air pollution levels sent to the server by a given robot\n" +
                                   "\t- 2 The average of the air pollution levels sent by all the robots to the server and occurred from timestamps t1 and t2\n");
    }

    public static void main(String[] args) {
        // Try to use a ClientConfig object to configure the client.
        Client client = new Client();
        ConfigurationHandler configurationHandler = ConfigurationHandler.getInstance(); 
        String baseUri = configurationHandler.getEndpointAdministratorServer();
        
        int choice = 0; 

        printMenu();
        do {
            System.out.println("Insert your choice: ");
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            try {
                choice = Integer.parseInt(inFromUser.readLine());
                switch (choice) {
                    case 0:
                        getCleaningRobotsList(client, baseUri); 
                        break;
                    case 1:
                        System.out.println("\tInsert the robot id: ");
                        int robotId = Integer.parseInt(inFromUser.readLine());
                        System.out.println("\tInsert the number of mesurement: ");
                        int numberOfLast = Integer.parseInt(inFromUser.readLine());
                        getAverageOfLastNAirPollutionLevelsOfRobot(client, baseUri, robotId, numberOfLast);
                        break;
                    case 2:
                        System.out.println("2");
                        ksdjfhsdkjfh
                        break;
                    default:
                        System.out.println("\tThis choice is not available.");
                        getHelloWorld(client, baseUri);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (true);

    }
        


}
