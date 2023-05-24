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
        HeartbeatResponse response = HeartbeatResponse.newBuilder()
                                                    .setId(this.cleaningRobot.getId())
                                                    .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
