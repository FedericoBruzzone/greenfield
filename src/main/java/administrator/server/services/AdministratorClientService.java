package administrator.server.services;

import administrator.server.beans.robot.CommonCleaningRobots;
import administrator.server.beans.robot.ICommonCleaningRobots;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/administratorclient")
public class AdministratorClientService {
    
    @Path("/cleaningrobotslist")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getCleaningRobotsList(){
        return Response.ok(CommonCleaningRobots.getInstance().getCleaningRobots()).build();
    }

    @Path("/averageoflastnairpollutionlevelsofrobot")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getAverageOfLastNAirPollutionLevelsOfRobot(@QueryParam("robotId") int robotId, @QueryParam("numberOfLast") int numberOfLast){
        return Response.ok(CommonCleaningRobots.getInstance().getAverageOfLastNAirPollutionLevelsOfRobot(robotId, numberOfLast)).build();
    }

    @GET
    @Produces("text/plain")
    public String helloWorld(){
        return "Hello world!";
    }
}
