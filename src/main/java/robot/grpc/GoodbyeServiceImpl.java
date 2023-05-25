package robot.grpc;

import proto.grpc.GoodbyeServiceGrpc.GoodbyeServiceImplBase;
import proto.grpc.GoodbyeServiceOuterClass.*;
import io.grpc.stub.StreamObserver;

import robot.CleaningRobot;
import robot.CleaningRobotInfo;

public class GoodbyeServiceImpl extends GoodbyeServiceImplBase {
    
    private CleaningRobot cleaningRobot;

    public GoodbyeServiceImpl(CleaningRobot cleaningRobot){
        this.cleaningRobot = cleaningRobot;
    }

    @Override
    public void streamGoodbye(GoodbyeRequest request, StreamObserver<GoodbyeResponse> responseObserver){
        CleaningRobotInfo cleaningRobotInfo = new CleaningRobotInfo(request.getId(), 
                                                                    request.getHost(), 
                                                                    request.getPort(),
                                                                    request.getDistrict());
        this.cleaningRobot.removeUnactiveCleaningRobot(cleaningRobotInfo);
        // GoodbyeResponse response = GoodbyeResponse.newBuilder().setMessage(cleaningRobotInfo.toString()).build();
        // responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
