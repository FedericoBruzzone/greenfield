package robot.grpc;

import proto.grpc.HeartbeatServiceGrpc.HeartbeatServiceImplBase;
import proto.grpc.HeartbeatServiceOuterClass.*;
import io.grpc.stub.StreamObserver;

import robot.CleaningRobot;
import robot.CleaningRobotInfo;

public class HeartbeatServiceImpl extends HeartbeatServiceImplBase {
    private CleaningRobot cleaningRobot;

    public HeartbeatServiceImpl(CleaningRobot cleaningRobot){
        this.cleaningRobot = cleaningRobot;
    }

    @Override
    public void streamHeartbeat(HeartbeatRequest request, StreamObserver<HeartbeatResponse> responseObserver) {
        // System.out.println("Received heartbeat from " + request.getId());
        // HeartbeatResponse response = HeartbeatResponse.newBuilder()
        //                                             .setId(this.cleaningRobot.getId())
        //                                             .build();
        // responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void streamCrash(CrashRequest request, StreamObserver<CrashResponse> responseObserver) {
        int cleaningRobotToRemove = request.getId();
        this.cleaningRobot.removeUnactiveCleaningRobot(cleaningRobotToRemove);
        
        if (cleaningRobot.getIsBroken()) {
            CleaningRobotInfo robotKey = this.cleaningRobot.getResponseCleaningRobotsISentThatImBroken()
                                                           .keySet()
                                                           .stream()
                                                           .filter(robot -> robot.id == cleaningRobotToRemove)
                                                           .findFirst()
                                                           .orElse(null);
            if (robotKey != null) {
                this.cleaningRobot.setResponseCleaningRobotsISentThatImBroken(robotKey, true);
            }
        }

        // responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
     
}
