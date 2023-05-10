package administrator.server.services;

import common.CommonCleaningRobot;
import administrator.server.beans.robot.CleaningRobots;
import common.CommonICleaningRobot;
import common.response.IResponse;
import common.response.RobotAddResponse;
import administrator.server.beans.robot.ICleaningRobots;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;


@Path("robot")
public class RobotCleaningService {

    @GET
    @Produces({"application/json", "application/xml"})
    public Response getCleaningRobotsList(){
        return Response.ok(CleaningRobots.getInstance()).build();
    }

    @Path("add")
    @POST
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    public Response addCleaningRobot(CommonCleaningRobot cleaningRobot){
        CommonCleaningRobot newCleaningRobot = CleaningRobots.getInstance().add(cleaningRobot);
        RobotAddResponse robotAddResponse = new RobotAddResponse(CleaningRobots.getInstance().getCleaningRobots(), newCleaningRobot.getDistrict());
        System.out.println("/robot/add " + CleaningRobots.getInstance().getCleaningRobots());
        return Response.ok(robotAddResponse).build();
    }

    ////permette di prelevare un utente con un determinato nome
    //@Path("get/{name}")
    //@GET
    //@Produces({"application/json", "application/xml"})
    //public Response getByName(@PathParam("name") String name){
    //    User u = Users.getInstance().getByName(name);
    //    if(u!=null)
    //        return Response.ok(u).build();
    //    else
    //        return Response.status(Response.Status.NOT_FOUND).build();
    //}


}
