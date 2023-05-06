package administrator.client;

import administrator.IAdministratorClient;
import util.ConfigurationHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.WebResource;

public final class AdministratorClient implements IAdministratorClient  {
    public static void main(String[] args) {
        // Try to use a ClientConfig object to configure the client.
        Client client = new Client();
        ConfigurationHandler configurationHandler = ConfigurationHandler.getInstance(); 
        String baseUri = configurationHandler.getEndpointAdministratorServer();
        
        int choice = 0; 

        do {
            printMenu();

            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            try {
                choice = Integer.parseInt(inFromUser.readLine());
                switch (choice) {
                    case 1:
                        System.out.println("1");
                        break;
                    case 2:
                        System.out.println("2");
                        break;
                    default:
                        System.out.println("This choice is not available.");
                        getHelloWorld(client, baseUri);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (true);

    }
    
    private static void printMenu() {
        System.out.println("Print menu");
    }
    
    private static void getHelloWorld(Client client, String baseUri) {
        String getPath = "/helloworld";
        ClientResponse clientResponse = getRequest(client, baseUri + getPath);
        System.out.println(clientResponse.toString());
        String string = clientResponse.getEntity(String.class);
        System.out.println(string);
    }

    public static ClientResponse getRequest(Client client, String url){
        WebResource webResource = client.resource(url);
        try {
            return webResource.type("application/json").get(ClientResponse.class);
        } catch (ClientHandlerException e) {
            System.out.println("Server non disponibile");
            return null;
        }
    }
}
