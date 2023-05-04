package administrator.server.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/helloworld")
public class TestService {
    @GET
    @Produces("text/plain")
    public String helloWorld(){
        return "Hello world!";
    }
}
