package administrator.server.services;

import administrator.server.beans.robot.CleaningRobots;
import administrator.server.beans.robot.ICleaningRobots;
import administrator.server.beans.robot.CleaningRobot;
import administrator.server.beans.robot.ICleaningRobot;
import administrator.server.beans.robot.response.RobotAddResponse;

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
    public Response addCleaningRobot(ICleaningRobot cleaningRobot){
        CleaningRobots.getInstance().add(cleaningRobot);
        List<ICleaningRobot> listCleaningRobot = CleaningRobots.getInstance().getCleaningRobots();
        RobotAddResponse robotAddResponse = new RobotAddResponse(listCleaningRobot);
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
