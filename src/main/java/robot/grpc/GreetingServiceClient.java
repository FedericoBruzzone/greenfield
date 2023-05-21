package robot.grpc; 

import proto.grpc.GreetingServiceGrpc;
import proto.grpc.GreetingServiceGrpc.*;
import proto.grpc.GreetingServiceOuterClass.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

import robot.CleaningRobot;

public class GreetingServiceClient {
    public void asynchronousStreamCall(final String host, final String port, CleaningRobot cleaningRobot) throws InterruptedException {
        // final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8080").usePlaintext().build();
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(host + ":" + port)
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
                System.out.println("Error! " + throwable.getMessage());
            }
            public void onCompleted() {
                channel.shutdownNow();
            }
        });
        channel.awaitTermination(10, TimeUnit.SECONDS);
    }
}

