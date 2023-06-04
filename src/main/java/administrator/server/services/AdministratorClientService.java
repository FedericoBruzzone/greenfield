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
   
    /**
     * This method returns the list of cleaning robots.<br>
     * Path: administratorclient/cleaningrobotslist<br>
     * Method: GET<br>
     * Produces: application/json, application/xml<br>
     *
     * @return a rest response containing the list of cleaning robots.
     */
    @Path("/cleaningrobotslist")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getCleaningRobotsList(){
        return Response.ok(CommonCleaningRobots.getInstance().getCleaningRobots()).build();
    }

    /**
     * This method returns the average of last n air pollution levels of a specific robot.<br>
     * Path: administratorclient/lastnairpollutionlevelsofrobot<br>
     * Method: GET<br>
     * Produces: application/json, application/xml<br>
     *
     * @param robotId the id of the robot.
     * @param n the number of air pollution levels to return.
     * @return a rest response containing the list of last n air pollution levels of a robot.
     */ 
    @Path("/averageoflastnairpollutionlevelsofrobot")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getAverageOfLastNAirPollutionLevelsOfRobot(@QueryParam("robotId") int robotId, @QueryParam("n") int n){
        return Response.ok(CommonCleaningRobots.getInstance().getAverageOfLastNAirPollutionLevelsOfRobot(robotId, n)).build();
    }
    
    /**
     * This method returns the average of air pollution levels of all robots between two timestamps.<br>
     * Path: administratorclient/averageofairpollutionlevelsofallrobotsbetween<br>
     * Method: GET<br>
     * Produces: application/json, application/xml<br>
     *
     * @param from the timestamp from which to start the average.
     * @param to the timestamp to which to end the average.
     * @return a rest response containing the average of air pollution levels of all robots between two timestamps.
     */ 
    @Path("/averageofairpollutionlevelsofallrobotsbetween")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getAverageOfAirPollutionLevelsOfAllRobotsBetween(@QueryParam("from") long from, @QueryParam("to") long to){
        return Response.ok(CommonCleaningRobots.getInstance().getAverageOfAirPollutionLevelsOfAllRobotsBetween(from, to)).build();
    }

}
