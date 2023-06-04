package administrator.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import administrator.IAdministratorClient;
import util.ConfigurationHandler;
import util.RestHandler;

/**
 * The Class AdministratorClient.
 *
 */
public final class AdministratorClient implements IAdministratorClient  {

    private Client client;
    private String baseUri;
    
    /**
     * Constructs a new administrator client.
     */
    public AdministratorClient() {
        this.client = new Client();
        this.baseUri = ConfigurationHandler.getInstance().getEndpointAdministratorServer();
    }

    /**
     * This method is used to get the list of the cleaning robots currently located in Greenfield.
     */
    private void getCleaningRobotsList() {
        String getPath = "/administratorclient/cleaningrobotslist";
        ClientResponse clientResponse = RestHandler.getRequest(this.client, this.baseUri + getPath);
        String string = clientResponse.getEntity(String.class);
        System.out.println(string);
    }

    /**
     * This method is used to get the average of the last n air pollution levels sent to the server by a given robot.
     *
     * @param robotId the robot id
     * @param n the number of last n air pollution levels
     */
    private void getAverageOfLastNAirPollutionLevelsOfRobot( int robotId, int n) {
        String getPath = "/administratorclient/averageoflastnairpollutionlevelsofrobot?robotId="+robotId+"&n="+n;
        ClientResponse clientResponse = RestHandler.getRequest(this.client, this.baseUri + getPath);
        String string = clientResponse.getEntity(String.class);
        System.out.println(string);
    }
  
    /**
     * This method is used to get the average of the air pollution levels sent by all the robots to the server and occurred from timestamps t1 and t2.
     *
     * @param from the first timestamp
     * @param to the second timestamp
     */
    private void getAverageOfAirPollutionLevelsOfAllRobotsBetween(long from, long to) {
        String getPath = "/administratorclient/averageofairpollutionlevelsofallrobotsbetween?from="+from+"&to="+to;
        ClientResponse clientResponse = RestHandler.getRequest(this.client, this.baseUri + getPath);
        String string = clientResponse.getEntity(String.class);
        System.out.println(string);
    }

    /**
     * Prints the menu.
     */
    private static void printMenu() {
            System.out.println("Type:\n" +
                                   "\t- 0 The list of the cleaning robots currently located in Greenfield\n" +
                                   "\t- 1 The average of the last n air pollution levels sent to the server by a given robot\n" +
                                   "\t- 2 The average of the air pollution levels sent by all the robots to the server and occurred from timestamps t1 and t2\n");
    }

    public static void main(String[] args) {
        // Try to use a ClientConfig object to configure the client.
        
        AdministratorClient administratorClient = new AdministratorClient();

        int choice = 0; 

        printMenu();
        do {
            System.out.println("Insert your choice: ");
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            try {
                choice = Integer.parseInt(inFromUser.readLine());
                switch (choice) {
                    case 0:
                        administratorClient.getCleaningRobotsList(); 
                        break;
                    case 1:
                        System.out.print("\tInsert the robot id: ");
                        int robotId = Integer.parseInt(inFromUser.readLine());
                        System.out.print("\tInsert the number of mesurement: ");
                        int numberOfLast = Integer.parseInt(inFromUser.readLine());
                        administratorClient.getAverageOfLastNAirPollutionLevelsOfRobot(robotId, numberOfLast);
                        break;
                    case 2:
                        // "2023/06/04 18:10:45"
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        System.out.print("\tInsert the first timestamp (yyyy/MM/dd HH:mm:ss) [from]: ");
                        long from = sdf.parse(inFromUser.readLine()).getTime();
                        System.out.print("\tInsert the second timestamp (yyyy/MM/dd HH:mm:ss) [to]: ");
                        long to = sdf.parse(inFromUser.readLine()).getTime();
                        administratorClient.getAverageOfAirPollutionLevelsOfAllRobotsBetween(from, to);
                        break;
                    default:
                        System.out.println("\tThis choice is not available.");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        } while (true);

    }
        


}
