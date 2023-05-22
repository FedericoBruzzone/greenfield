package robot.grpc;

import proto.grpc.GreetingServiceGrpc.GreetingServiceImplBase;
import proto.grpc.GreetingServiceOuterClass.*;
import io.grpc.stub.StreamObserver;

import robot.CleaningRobot;
import robot.CleaningRobotInfo;

public class GreetingServiceImpl extends GreetingServiceImplBase {
    
    private CleaningRobot cleaningRobot;

    public GreetingServiceImpl(CleaningRobot cleaningRobot){
        this.cleaningRobot = cleaningRobot;
    }

    @Override
    public void streamGreeting(GreetingRequest request, StreamObserver<GreetingResponse> responseObserver){

        // System.out.println(request);
        CleaningRobotInfo cleaningRobotInfo = new CleaningRobotInfo(request.getId(), 
                                                                    request.getHost(), 
                                                                    request.getPort(),
                                                                    request.getDistrict());
        ksjdfhfkdjshf
        // Check if alreay exists
        this.cleaningRobot.addActiveCleaningRobot(cleaningRobotInfo);

        // GreetingResponse response = GreetingResponse.newBuilder().setMessage(cleaningRobotInfo.toString()).build();

        // responseObserver.onNext(response);

        responseObserver.onCompleted();

    }

}
