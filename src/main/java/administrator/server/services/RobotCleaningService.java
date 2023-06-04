package administrator.server.services;

import common.CommonCleaningRobot;
import administrator.server.beans.robot.CommonCleaningRobots;
import common.response.RobotAddResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * This class is the REST service for the cleaning robots.<br>
 * It provides the following services:<br>
 * - add a new cleaning robot to the system<br>
 * - remove a cleaning robot from the system<br>
 */
@Path("robot")
public class RobotCleaningService {

    /**
     * Add a new cleaning robot to the system.<br>
     * Path: /robot/add<br>
     * Method: POST<br>
     * Consumes: application/json, application/xml<br>
     * Produces: application/json, application/xml<br>
     *
     * @param cleaningRobot the cleaning robot to add
     * @return a rest response if the cleaning robot is added or not.
     */
    @Path("add")
    @POST
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    public Response addCleaningRobot(CommonCleaningRobot cleaningRobot){
        CommonCleaningRobot newCleaningRobot = CommonCleaningRobots.getInstance().add(cleaningRobot);
        if (newCleaningRobot == null) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        RobotAddResponse robotAddResponse = new RobotAddResponse(CommonCleaningRobots.getInstance().getCleaningRobotsWithout(cleaningRobot), 
                                                                 newCleaningRobot.getDistrict());
        System.out.println("/robot/add " + cleaningRobot);
        return Response.ok(robotAddResponse).build();
    }
   
    /**
     * Remove a cleaning robot from the system.<br>
     * Path: /robot/remove<br>
     * Method: DELETE<br>
     * Consumes: application/json, application/xml<br>
     *
     * @param cleaningRobot the cleaning robot to remove 
     * @return a rest response if the cleaning robot is removed or not.
     */
    @Path("remove")
    @DELETE
    @Consumes({"application/json", "application/xml"})
    public Response removeCleaningRobot(CommonCleaningRobot cleaningRobot){
        if (CommonCleaningRobots.getInstance().remove(cleaningRobot)) {
            System.out.println("/robot/remove " + cleaningRobot);
            return Response.ok().build(); 
        }
        else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
