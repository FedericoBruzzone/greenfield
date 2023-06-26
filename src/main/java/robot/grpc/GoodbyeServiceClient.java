package robot.grpc; 

import proto.grpc.GoodbyeServiceGrpc;
import proto.grpc.GoodbyeServiceGrpc.*;
import proto.grpc.GoodbyeServiceOuterClass.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

import robot.CleaningRobot;
import robot.CleaningRobotInfo;

public class GoodbyeServiceClient {
    public static void asynchronousStreamCall(final CleaningRobotInfo cleaningRobotInfo, CleaningRobot cleaningRobot) throws InterruptedException {
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(cleaningRobotInfo.host + ":" + cleaningRobotInfo.port)
                                                            .usePlaintext()
                                                            .build();
        GoodbyeServiceStub stub = GoodbyeServiceGrpc.newStub(channel);

        GoodbyeRequest request = GoodbyeRequest.newBuilder()
                                               .setId(cleaningRobot.getId())
                                               .setHost(cleaningRobot.getHost())
                                               .setPort(cleaningRobot.getPort())
                                               .setDistrict(cleaningRobot.getDistrict())
                                               .build();
        
        stub.streamGoodbye(request, new StreamObserver<GoodbyeResponse>() {
            public void onNext(GoodbyeResponse goodbyeResponse) {
                System.out.println(goodbyeResponse.getMessage());
            }
            public void onError(Throwable throwable) {
                System.out.println("Error! " + throwable.getMessage());
            }
            public void onCompleted() {
                channel.shutdown();
            }
        });
        channel.awaitTermination(10, TimeUnit.SECONDS);
    }
}

