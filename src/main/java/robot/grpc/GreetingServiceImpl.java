package robot.grpc;

import proto.grpc.GreetingServiceGrpc.GreetingServiceImplBase;
import proto.grpc.GreetingServiceOuterClass.*;
import io.grpc.stub.StreamObserver;

import robot.CleaningRobot;

public class GreetingServiceImpl extends GreetingServiceImplBase {
    
    private CleaningRobot cleaningRobot;

    public GreetingServiceImpl(CleaningRobot cleaningRobot){
        this.cleaningRobot = cleaningRobot;
    }

    @Override
    public void streamGreeting(GreetingRequest request, StreamObserver<GreetingResponse> responseObserver){

        System.out.println("Metodo stream chiamato!");
        System.out.println(request);

        // this.cleaningRobot.add(request.getId());

        GreetingResponse response = GreetingResponse.newBuilder().setMessage("Add: " + request.getId()).build();

        responseObserver.onNext(response);

        responseObserver.onCompleted();

    }

}
