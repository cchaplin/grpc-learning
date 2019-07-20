package com.naveenl.learning.grpc.greeting.server;


import com.proto.greet.*;
import com.proto.greet.GreetServiceGrpc.GreetServiceImplBase;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceImplBase {
    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {

        // Extract the fields from request
        Greeting greeting = request.getGreeting();
        String fname = greeting.getFirstName();
        String result = "Hello " + fname + "!";

        GreetResponse response = GreetResponse.newBuilder().setResult(result).build();

        // send the response
        //
        responseObserver.onNext(response);

        // complete the RPC call
        //
        responseObserver.onCompleted();
    }

    @Override
    public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {

        String fname = request.getGreeting().getFirstName();

        for (int i = 0; i < 10; i++) {
            String result = "Hello " + fname + ", Response number : " + i ;
            GreetManyTimesResponse response = GreetManyTimesResponse.newBuilder()
                    .setResult(result)
                    .build();
            responseObserver.onNext(response);
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {

        StreamObserver<LongGreetRequest> requestObserver = new StreamObserver<LongGreetRequest>() {
            String result = "";
            @Override
            public void onNext(LongGreetRequest value) {

                // Client sends a message
                //
                result += "Hello " + value.getGreeting().getFirstName() + "!";

            }

            @Override
            public void onError(Throwable t) {
                // Client sends an error
                //
            }

            @Override
            public void onCompleted() {
                // Client is done.
                // This is when we want to send the response to client (responseObserver)
                //
                responseObserver.onNext(LongGreetResponse.newBuilder().setResult(result).build());
                responseObserver.onCompleted();
            }
        };
        return requestObserver;
    }

    @Override
    public StreamObserver<GreetEveryOneRequest> greetEveryOne(StreamObserver<GreetEveryOneResponse> responseObserver) {
        StreamObserver<GreetEveryOneRequest> requestObserver = new StreamObserver<GreetEveryOneRequest>() {
            @Override
            public void onNext(GreetEveryOneRequest value) {
                System.out.println("Received from client - " + value.getGreeting().getFirstName());
                String result = "Hello " + value.getGreeting().getFirstName();
                GreetEveryOneResponse greetEveryOneResponse = GreetEveryOneResponse.newBuilder()
                        .setResult(result)
                        .build();

                responseObserver.onNext(greetEveryOneResponse);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();

            }
        };
        return requestObserver;
    }

    @Override
    public void greetWithDeadline(GreetWithDeadlineRequest request, StreamObserver<GreetWithDeadlineResponse> responseObserver) {

        Context current = Context.current();

        try {
            for (int i = 0; i < 3; i++) {
                if (!current.isCancelled()) {
                    System.out.println("Sleep for 100ms");
                    Thread.sleep(100);
                } else {
                    return;
                }

            }

            System.out.println("Sending response");
            responseObserver.onNext(
                    GreetWithDeadlineResponse.newBuilder()
                            .setResult("Hello " + request.getGreeting().getFirstName())
                            .build()
            );
            responseObserver.onCompleted();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
