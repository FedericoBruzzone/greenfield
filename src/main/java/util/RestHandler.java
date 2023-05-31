 package util;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class RestHandler {
    public static <T> ClientResponse postRequest(Client client, String url, T u) {
        WebResource webResource = client.resource(url);
        String input = new Gson().toJson(u);
        try {
            return webResource.type("application/json").post(ClientResponse.class, input);
        } catch (ClientHandlerException e) {
            System.out.println("Server is not available.");
            // e.printStackTrace();
            return null;
        }
    }

    public static <T> ClientResponse getRequest(Client client, String url) {
        WebResource webResource = client.resource(url);
        try {
            return webResource.type("application/json").get(ClientResponse.class);
        } catch (ClientHandlerException e) {
            System.out.println("Server is not available.");
            // e.printStackTrace();
            return null;
        }
    }

    public static <T> ClientResponse deleteRequest(Client client, String url, T u){
        WebResource webResource = client.resource(url);
        String input = new Gson().toJson(u);
        try {
            return webResource.type("application/json").delete(ClientResponse.class, input);
        } catch (ClientHandlerException e) {
            System.out.println("Server is not available.");
            // e.printStackTrace();
            return null;
        }
    } 

}
