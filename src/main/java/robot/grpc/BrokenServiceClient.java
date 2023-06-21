package robot.grpc; 

import proto.grpc.BrokenServiceGrpc;
import proto.grpc.BrokenServiceGrpc.*;
import proto.grpc.BrokenServiceOuterClass.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

import robot.CleaningRobot;
import robot.CleaningRobotInfo;

public class BrokenServiceClient {
    public static void asynchronousStreamCall(final CleaningRobotInfo cleaningRobotInfo, CleaningRobot cleaningRobot, CleaningRobotInfo myTimestampRequestThatImBroken) throws InterruptedException {

        final ManagedChannel channel = ManagedChannelBuilder.forTarget(cleaningRobotInfo.host + ":" + cleaningRobotInfo.port)
                                                            .usePlaintext()
                                                            .build();
        BrokenServiceStub stub = BrokenServiceGrpc.newStub(channel);
    
        BrokenRequest request = BrokenRequest.newBuilder()
                                             .setId(cleaningRobot.getId())
                                             .setHost(cleaningRobot.getHost())
                                             .setPort(cleaningRobot.getPort())
                                             .setDistrict(cleaningRobot.getDistrict())
                                             .setTimestamp(myTimestampRequestThatImBroken.timestamp)
                                             .build();
        
          
        // System.out.println("Send: " + request);
        stub.streamBroken(request, new StreamObserver<BrokenResponse>() {
            public void onNext(BrokenResponse brokenResponse) {
                if(brokenResponse.getMessage().equals("OK")) {
                    System.out.println("We are OK");
                    cleaningRobot.setResponseCleaningRobotsISentThatImBroken(cleaningRobotInfo, true); 
                }
                Boolean allTrue = cleaningRobot.getResponseCleaningRobotsISentThatImBroken()
                                               .values()
                                               .stream()
                                               .allMatch(Boolean::booleanValue); 
                System.out.println("All true? : " + allTrue);
            }

            public void onError(Throwable throwable) {
                System.out.println("Error! " + throwable.getMessage());
            }
            public void onCompleted() {
                // System.out.println("onCompleted");
                channel.shutdownNow();
            }
        });
        channel.awaitTermination(5, TimeUnit.SECONDS);
    }

}

