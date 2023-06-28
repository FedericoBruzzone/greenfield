package robot.grpc; 

import proto.grpc.FixedServiceGrpc;
import proto.grpc.FixedServiceGrpc.*;
import proto.grpc.FixedServiceOuterClass.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

import robot.CleaningRobot;
import robot.CleaningRobotInfo;

public class FixedServiceClient {
    public static void asynchronousStreamCall(final CleaningRobotInfo cleaningRobotInfo, CleaningRobot cleaningRobot) throws InterruptedException {

        final ManagedChannel channel = ManagedChannelBuilder.forTarget(cleaningRobotInfo.host + ":" + cleaningRobotInfo.port)
                                                            .usePlaintext()
                                                            .build();
        FixedServiceStub stub = FixedServiceGrpc.newStub(channel);
    
        FixedRequest request = FixedRequest.newBuilder()
                                             .setId(cleaningRobot.getId())
                                             .setHost(cleaningRobot.getHost())
                                             .setPort(cleaningRobot.getPort())
                                             .setDistrict(cleaningRobot.getDistrict())
                                             .setMessage("OK")
                                             .build();
        
        stub.streamFixed(request, new StreamObserver<FixedResponse>() {
            public void onNext(FixedResponse brokenResponse) {
            }

            public void onError(Throwable throwable) {
                System.out.println("Fixed Error! " + throwable.getMessage());
            }

            public void onCompleted() {
                
                channel.shutdown();
            }
        });

        channel.awaitTermination(5, TimeUnit.SECONDS);
    }

}

