package robot.grpc; 

import proto.grpc.HeartbeatServiceGrpc;
import proto.grpc.HeartbeatServiceGrpc.*;
import proto.grpc.HeartbeatServiceOuterClass.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

import robot.CleaningRobot;
import robot.CleaningRobotInfo;

public class HeartbeatServiceClient {

    public static void asynchronousStreamCall(final CleaningRobotInfo cleaningRobotInfo, CleaningRobot cleaningRobot) throws InterruptedException {
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(cleaningRobotInfo.host + ":" + cleaningRobotInfo.port)
                                                            .usePlaintext()
                                                            .build();
        HeartbeatServiceStub stub = HeartbeatServiceGrpc.newStub(channel);

        HeartbeatRequest request = HeartbeatRequest.newBuilder()
                                                   .setId(cleaningRobot.getId())
                                                   .build();
        
        // System.out.println("Send: " + request);
        stub.streamHeartbeat(request, new StreamObserver<HeartbeatResponse>() {
            public void onNext(HeartbeatResponse heartbeatResponse) {
                // System.out.println("onNext");
            }
            public void onError(Throwable throwable) {
                System.out.println("Error! " + throwable.getMessage());
                cleaningRobot.removeUnactiveCleaningRobot(cleaningRobotInfo); 
                cleaningRobot.removeCleaningRobotFromAdministratorServer(cleaningRobotInfo);
            }
            public void onCompleted() {
                // System.out.println("onCompleted");
                channel.shutdownNow();
            }
        });
        channel.awaitTermination(5, TimeUnit.SECONDS);
    }
}

