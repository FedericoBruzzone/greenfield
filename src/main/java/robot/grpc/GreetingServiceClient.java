package robot.grpc; 

import proto.grpc.GreetingServiceGrpc;
import proto.grpc.GreetingServiceGrpc.*;
import proto.grpc.GreetingServiceOuterClass.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

import robot.CleaningRobot;
import robot.CleaningRobotInfo;

public class GreetingServiceClient {
    public static void asynchronousStreamCall(final CleaningRobotInfo cleaningRobotInfo, CleaningRobot cleaningRobot) throws InterruptedException {
        // final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8080").usePlaintext().build();
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(cleaningRobotInfo.host + ":" + cleaningRobotInfo.port)
                                                            .usePlaintext()
                                                            .build();
        GreetingServiceStub stub = GreetingServiceGrpc.newStub(channel);

        GreetingRequest request = GreetingRequest.newBuilder()
                                                 .setId(cleaningRobot.getId())
                                                 .setHost(cleaningRobot.getHost())
                                                 .setPort(cleaningRobot.getPort())
                                                 .setDistrict(cleaningRobot.getDistrict())
                                                 .build();
        
        stub.streamGreeting(request, new StreamObserver<GreetingResponse>() {
            public void onNext(GreetingResponse greetingResponse) {
                // System.out.println(greetingResponse.getMessage());
            }
            public void onError(Throwable throwable) {
                System.out.println("Greeting Error! " + throwable.getMessage());
            }
            public void onCompleted() {
                channel.shutdown();
            }
        });
        channel.awaitTermination(10, TimeUnit.SECONDS);
    }
}

