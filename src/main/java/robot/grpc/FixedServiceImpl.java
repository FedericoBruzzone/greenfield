package robot.grpc;

import proto.grpc.FixedServiceGrpc.FixedServiceImplBase;
import proto.grpc.FixedServiceOuterClass.*;
import io.grpc.stub.StreamObserver;

import robot.CleaningRobot;
import robot.CleaningRobotInfo;

public class FixedServiceImpl extends FixedServiceImplBase {
    private CleaningRobot cleaningRobot;

    public FixedServiceImpl(CleaningRobot cleaningRobot){
        this.cleaningRobot = cleaningRobot;
    }

    @Override
    public void streamFixed(FixedRequest request, StreamObserver<FixedResponse> responseObserver){
        CleaningRobotInfo cleaningRobotInfo = new CleaningRobotInfo(request.getId(), 
                                                                    request.getHost(), 
                                                                    request.getPort(),
                                                                    request.getDistrict());
        
        if(request.getMessage().equals("OK")) {
            cleaningRobot.setResponseCleaningRobotsISentThatImBroken(cleaningRobotInfo, true);
            cleaningRobot.notifyMalfunctionsThread();
        }
        responseObserver.onCompleted();
    }
}
