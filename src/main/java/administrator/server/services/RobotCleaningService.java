package administrator.server.services;

import common.CommonCleaningRobot;
import administrator.server.beans.robot.CommonCleaningRobots;
import common.ICommonCleaningRobot;
import common.response.IResponse;
import common.response.RobotAddResponse;
import administrator.server.beans.robot.ICommonCleaningRobots;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;


@Path("robot")
public class RobotCleaningService {

    @GET
    @Produces({"application/json", "application/xml"})
    public Response getCleaningRobotsList(){
        return Response.ok(CommonCleaningRobots.getInstance()).build();
    }

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
        System.out.println("/robot/add " + CommonCleaningRobots.getInstance().getCleaningRobots());
        return Response.ok(robotAddResponse).build();
    }
    
    @Path("remove")
    @DELETE
    @Consumes({"application/json", "application/xml"})
    public Response removeCleaningRobot(CommonCleaningRobot cleaningRobot){
        if (CommonCleaningRobots.getInstance().remove(cleaningRobot)) {
            System.out.println("/robot/remove " + CommonCleaningRobots.getInstance().getCleaningRobots());
            return Response.ok().build(); 
        }
        else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
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
