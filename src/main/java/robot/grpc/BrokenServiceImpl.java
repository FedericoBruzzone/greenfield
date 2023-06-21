package robot.grpc;

import proto.grpc.BrokenServiceGrpc.BrokenServiceImplBase;
import proto.grpc.BrokenServiceOuterClass.*;
import io.grpc.stub.StreamObserver;

import robot.CleaningRobot;
import robot.CleaningRobotInfo;

public class BrokenServiceImpl extends BrokenServiceImplBase {
    private CleaningRobot cleaningRobot;

    public BrokenServiceImpl(CleaningRobot cleaningRobot){
        this.cleaningRobot = cleaningRobot;
    }

    @Override
    public void streamBroken(BrokenRequest request, StreamObserver<BrokenResponse> responseObserver){
        CleaningRobotInfo cleaningRobotInfo = new CleaningRobotInfo(request.getId(), 
                                                                    request.getHost(), 
                                                                    request.getPort(),
                                                                    request.getDistrict(),
                                                                    request.getTimestamp());
      
        BrokenResponse response = null;
        if(this.cleaningRobot.getMyTimestampRequestImBroken() == -1) {
            response = BrokenResponse.newBuilder().setMessage("OK").build();
        } else if(!this.cleaningRobot.getIsBroken()) {
            response = BrokenResponse.newBuilder().setMessage("OK").build();
        } else if(this.cleaningRobot.getIsBroken() && 
                  this.cleaningRobot.getMyTimestampRequestImBroken() > cleaningRobotInfo.timestamp) {
            response = BrokenResponse.newBuilder().setMessage("OK").build();
        } else if(this.cleaningRobot.getIsBroken() && 
                  this.cleaningRobot.getMyTimestampRequestImBroken() < cleaningRobotInfo.timestamp) {
            this.cleaningRobot.addCleaningRobotsWithTimestampGreaterThanMine(cleaningRobotInfo);            
            response = BrokenResponse.newBuilder().setMessage("NO OK").build();
        }          

        // Se sei this é dal meccanico add cleaningRobotInfo to brokenRobotList
        responseObserver.onNext(response);
        // responseObserver.onCompleted();
    }
}
