package com.naveenl.learning.grpc.greeting.client;


import com.proto.greet.*;
import io.grpc.*;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    private void run () {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",50051)
                .usePlaintext()
                .build();

//        doUnary(channel);
//        doServerStreaming(channel);
//        doClientStreaming(channel);

//        doBidirectionalStreaming(channel);
        doUnaryWithDeadline(channel);
        System.out.println("Shutting down channel");
        channel.shutdown();
    }

    private void doUnary(ManagedChannel channel) {
        // Created a greet service client (blocking - synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        // Usecase 1: Unary
        //
        // Created a protocol buffere greet message
        //
        Greeting greet = Greeting.newBuilder()
                .setFirstName("Naveen")
                .setLastName("Kumar")
                .build();

        // Create GreetRequest
        //
        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greet)
                .build();

        // Call RPC method and get result
        //
        GreetResponse greetResponse = greetClient.greet(greetRequest);
        System.out.println("Nav: Response = [" + greetResponse.getResult() + "]");

    }

    private void doServerStreaming(ManagedChannel channel) {
        // Created a greet service client (blocking - synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        // Usecase 2: Server streaming
        //
        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder()
                .setGreeting(Greeting.newBuilder().setFirstName("Naveen"))
                .build();

        greetClient.greetManyTimes(greetManyTimesRequest)
                .forEachRemaining(greetManyTimesResponse -> {
                    System.out.println(greetManyTimesResponse.getResult());
                });
    }

    private void doClientStreaming(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<LongGreetRequest> requestObserver = asyncClient.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse value) {
                // We get response from server
                System.out.println("Received response from server");
                System.out.println(value.getResult());
                // onNext is called only once
            }

            @Override
            public void onError(Throwable t) {
                // We get error from server
                //

            }

            @Override
            public void onCompleted() {

                // The server is done sending us data
                // onCompleted() will be called right after onNext()
                System.out.println("Server has completed sending us something");
                latch.countDown();
            }
        });

        // First message
        System.out.println("Sending message #1");
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Naveen")
                        .build())
                .build());

        // Second message
        System.out.println("Sending message #2");
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Ammu")
                        .build())
                .build());

        // Third message
        System.out.println("Sending message #3");
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Chinnu")
                        .build())
                .build());

        // We tell server that the client is done sending data
        //
        requestObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private void doBidirectionalStreaming(ManagedChannel channel) {

        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<GreetEveryOneRequest> requestObserver = asyncClient.greetEveryOne(new StreamObserver<GreetEveryOneResponse>() {
            @Override
            public void onNext(GreetEveryOneResponse value) {
                System.out.println("Response from Server - " + value.getResult());

            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();

            }

            @Override
            public void onCompleted() {
                System.out.println("Server is done sending data");
                latch.countDown();

            }
        });

        Arrays.asList("Naveen", "Ammu", "Sanu", "Sathish", "Arun", "Maamu").forEach(
                name -> {
                    System.out.println("Sending: " + name);
                    requestObserver.onNext(GreetEveryOneRequest.newBuilder()
                            .setGreeting(Greeting.newBuilder()
                                    .setFirstName(name))
                            .build());
                    // to see that the request & response are really asynchronous, introduce a sleep
                    // to see out of order request and responses..
                    // To do that uncomment below lines. Another alternative is to send lots of names
                    //
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
        );

        requestObserver.onCompleted();
        try {
            latch.await(3L,TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doUnaryWithDeadline(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceBlockingStub stub = GreetServiceGrpc.newBlockingStub(channel);

        // First call with 3000ms deadline
        try {
            GreetWithDeadlineResponse response = stub.withDeadline(Deadline.after(3000, TimeUnit.MILLISECONDS)).greetWithDeadline(GreetWithDeadlineRequest.newBuilder()
                    .setGreeting(Greeting.newBuilder().setFirstName("Naveen").build())
                    .build());
            System.out.println("Response is: " + response.getResult());
        } catch (StatusRuntimeException e) {
            if (e.getStatus() == Status.DEADLINE_EXCEEDED) {
                System.out.println("Deadline has been exceeded. We don't want response");
            } else {
                e.printStackTrace();
            }
        }


        // First call with 100ms deadline
        try {
            GreetWithDeadlineResponse response = stub.withDeadline(Deadline.after(100, TimeUnit.MILLISECONDS)).greetWithDeadline(GreetWithDeadlineRequest.newBuilder()
                    .setGreeting(Greeting.newBuilder().setFirstName("Naveen").build())
                    .build());
            System.out.println("Response is: " + response.getResult());
        } catch (StatusRuntimeException e) {
            if (e.getStatus() == Status.DEADLINE_EXCEEDED) {
                System.out.println("Deadline has been exceeded. We don't want response");
            } else {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Hello!! Am a grpc client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",50051)
                .usePlaintext()
                .build();

        GreetingClient main = new GreetingClient();
        main.run();


//        System.out.println("Creating stub");
//        DummyServiceGrpc.DummyServiceBlockingStub syncClient = DummyServiceGrpc.newBlockingStub(channel);

        // Created a greet service client (blocking - synchronous)
//        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        // Create a greeting service client (async - non blocking)
        // GreetServiceGrpc.GreetServiceFutureStub asyncGreetClient = GreetServiceGrpc.newFutureStub(channel);

        // Usecase 1: Unary
        //
        // Created a protocol buffere greet message
        //
//        Greeting greet = Greeting.newBuilder()
//                .setFirstName("Naveen")
//                .setLastName("Kumar")
//                .build();
//
//        // Create GreetRequest
//        //
//        GreetRequest greetRequest = GreetRequest.newBuilder()
//                .setGreeting(greet)
//                .build();
//
//        // Call RPC method and get result
//        //
//        GreetResponse greetResponse = greetClient.greet(greetRequest);
//        System.out.println("Nav: Response = [" + greetResponse.getResult() + "]");

        // Usecase 2: Server streaming
        //
//        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder()
//                .setGreeting(Greeting.newBuilder().setFirstName("Naveen"))
//                .build();
//
//        greetClient.greetManyTimes(greetManyTimesRequest)
//                .forEachRemaining(greetManyTimesResponse -> {
//                    System.out.println(greetManyTimesResponse.getResult());
//                });

//
//        System.out.println("Shutting down channel");
//        channel.shutdown();


    }
}
