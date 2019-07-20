package com.naveenl.learning.grpc.calculator.client;

import com.proto.calculator.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {

    private void run() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",50052)
                .usePlaintext()
                .build();

//        doUnary(channel);
//        doServerStreaming(channel);
//        doClientStreaming(channel);
//        doBiDiStreaming(channel);
        doSquareErrorCall(channel);
        channel.shutdown();
    }

    private void doUnary(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);

        SumRequest sumRequest = SumRequest.newBuilder()
                .setFirstNum(10)
                .setSecondNum(34)
                .build();

        SumResponse response = stub.sum(sumRequest);
        System.out.println(sumRequest.getFirstNum() + " + " + sumRequest.getSecondNum() + " = " + response.getSumRes());

    }

    private void doServerStreaming(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);

        Integer number = 967890169;
        stub.primeNumberDecomposition(PrimeNumberDecompositionRequest
                .newBuilder().setNumber(number).build())
                .forEachRemaining(primeNumberDecompositionResponse -> {
                    System.out.println(primeNumberDecompositionResponse.getPrimeFactor());
                });
    }

    private void doClientStreaming(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceStub asyncStub = CalculatorServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<ComputeAverageRequest> requestObserver = asyncStub.computeAverage(new StreamObserver<ComputeAverageResponse>() {
            @Override
            public void onNext(ComputeAverageResponse value) {
                System.out.println("Received a response from server");
                System.out.println("Avg = " + value.getAvg());

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Server has completed sending us data");
                latch.countDown();

            }
        });

        for (int i = 0; i < 100000; i++) {
            requestObserver.onNext(ComputeAverageRequest.newBuilder().setNumber(i).build());
        }
//        requestObserver.onNext(ComputeAverageRequest.newBuilder().setNumber(1).build());
//        requestObserver.onNext(ComputeAverageRequest.newBuilder().setNumber(2).build());
//        requestObserver.onNext(ComputeAverageRequest.newBuilder().setNumber(3).build());
//        requestObserver.onNext(ComputeAverageRequest.newBuilder().setNumber(4).build());
//        requestObserver.onNext(ComputeAverageRequest.newBuilder().setNumber(5).build());

        requestObserver.onCompleted();
        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doBiDiStreaming(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceStub asyncStub = CalculatorServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<FindMaxRequest> requestObserver = asyncStub.findMax(new StreamObserver<FindMaxResponse>() {
            @Override
            public void onNext(FindMaxResponse value) {
                System.out.println("Got new max from server - " + value.getMaximum());
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Server is done sending messages.");
            }
        });

        Arrays.asList(1,4,12,6,33,66,18,9).forEach(
                number -> {
                    System.out.println("Sending number - " + number);
                    requestObserver.onNext(FindMaxRequest.newBuilder().setNumber(number).build());
                }
        );

        requestObserver.onCompleted();
        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doSquareErrorCall (ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);

        int num = -1;
        try {
            stub.squareRoot(SquareRootRequest.newBuilder()
                    .setNumber(num)
                    .build()
            );
        } catch (StatusRuntimeException e) {
            System.out.println("Got an exception for square root");
            e.printStackTrace();
        }



    }
    public static void main(String[] args) {

        CalculatorClient main = new CalculatorClient();
        main.run();
//        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",50052)
//                .usePlaintext()
//                .build();
//
//        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);

//        Scenario 1: Unary
//        SumRequest sumRequest = SumRequest.newBuilder()
//                .setFirstNum(10)
//                .setSecondNum(34)
//                .build();
//
//        SumResponse response = stub.sum(sumRequest);
//        System.out.println(sumRequest.getFirstNum() + " + " + sumRequest.getSecondNum() + " = " + response.getSumRes());

//        // Scenario 2: Streaming Server
//        //
//        Integer number = 967890169;
//        stub.primeNumberDecomposition(PrimeNumberDecompositionRequest
//                .newBuilder().setNumber(number).build())
//                .forEachRemaining(primeNumberDecompositionResponse -> {
//                    System.out.println(primeNumberDecompositionResponse.getPrimeFactor());
//                });

        // Scenario 3: Client Streaming
        //


//        channel.shutdown();
    }
}
