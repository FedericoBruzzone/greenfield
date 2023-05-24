package robot.grpc; 

import proto.grpc.HeartbeatServiceGrpc;
import proto.grpc.HeartbeatServiceGrpc.*;
import proto.grpc.HeartbeatServiceOuterClass.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

import robot.CleaningRobot;

public class HeartbeatServiceClient {

    public static void asynchronousStreamCall(final String host, final String port, CleaningRobot cleaningRobot) throws InterruptedException {
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(host + ":" + port)
                                                            .usePlaintext()
                                                            .build();
        HeartbeatServiceStub stub = HeartbeatServiceGrpc.newStub(channel);

        HeartbeatRequest request = HeartbeatRequest.newBuilder()
                                                   .setId(cleaningRobot.getId())
                                                   .build();
        
        System.out.println("Send: " + request);
        stub.streamHeartbeat(request, new StreamObserver<HeartbeatResponse>() {
            public void onNext(HeartbeatResponse heartbeatResponse) {
                System.out.println("onNext");
                System.out.println(heartbeatResponse.getId());
            }
            public void onError(Throwable throwable) {
                System.out.println("Error! " + throwable.getMessage());
            }
            public void onCompleted() {
                System.out.println("onCompleted");
                channel.shutdownNow();
            }
        });
        channel.awaitTermination(10, TimeUnit.SECONDS);
        // stub.streamGreeting(request, new StreamObserver<GreetingResponse>() {
        //     public void onNext(GreetingResponse greetingResponse) {
        //         // System.out.println(greetingResponse.getMessage());
        //     }
        //     public void onError(Throwable throwable) {
        //         System.out.println("Error! " + throwable.getMessage());
        //     }
        //     public void onCompleted() {
        //         channel.shutdownNow();
        //     }
        // });
        // channel.awaitTermination(10, TimeUnit.SECONDS);
    }
}

