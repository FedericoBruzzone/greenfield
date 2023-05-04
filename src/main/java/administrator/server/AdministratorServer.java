package administrator.server;

import administrator.IAdministratorServer;
import util.IConfigurationHandler; 
import util.ConfigurationHandler; 

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;

public final class AdministratorServer implements IAdministratorServer  {
public static void main(String[] args) {
        IConfigurationHandler configurationHandler = ConfigurationHandler.instance();
        String serverURI = configurationHandler.getEndpointAdministratorServer();
        try {
            final HttpServer httpServer = HttpServerFactory.create(serverURI + "/");

            httpServer.start();

            System.out.println("Server running!");
            System.out.println("Server started on: " + serverURI);

            System.out.println("Hit return to stop...");
            System.in.read();
            System.out.println("Stopping server");
            httpServer.stop(0);
            System.out.println("Server stopped");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
