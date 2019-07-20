package com.naveenl.learning.grpc.calculator.server;

import com.proto.calculator.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {

        // Extract the field from request
        int first_num = request.getFirstNum();
        int sec_num = request.getSecondNum();

        int result = first_num + sec_num;

        SumResponse sumResponse = SumResponse.newBuilder()
                .setSumRes(result)
                .build();

        responseObserver.onNext(sumResponse);

        responseObserver.onCompleted();
    }

    @Override
    public void primeNumberDecomposition(PrimeNumberDecompositionRequest request, StreamObserver<PrimeNumberDecompositionResponse> responseObserver) {
        Integer number = request.getNumber();
        Integer divisor = 2;

        while (number > 1) {
            if (number % divisor == 0) {
                number = number / divisor;
                responseObserver.onNext(PrimeNumberDecompositionResponse.newBuilder().setPrimeFactor(divisor).build());
            } else {
                divisor = divisor + 1;
            }
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<ComputeAverageRequest> computeAverage(StreamObserver<ComputeAverageResponse> responseObserver) {
        StreamObserver<ComputeAverageRequest> requestObserver = new StreamObserver<ComputeAverageRequest>() {
            int sum = 0;
            int count = 0;
            @Override
            public void onNext(ComputeAverageRequest value) {
                count++;
                sum+= value.getNumber();

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                double avg = (double)sum / count;

                responseObserver.onNext(
                        ComputeAverageResponse.newBuilder()
                                .setAvg(avg)
                                .build()
                );
                responseObserver.onCompleted();

            }
        };

        return requestObserver;
    }

    @Override
    public StreamObserver<FindMaxRequest> findMax(StreamObserver<FindMaxResponse> responseObserver) {
        return new StreamObserver<FindMaxRequest>() {

            int curMax = 0;
            @Override
            public void onNext(FindMaxRequest value) {
                int num = value.getNumber();
                if (num > curMax) {
                    curMax = num;
                    responseObserver.onNext(
                            FindMaxResponse.newBuilder()
                                    .setMaximum(curMax)
                                    .build()
                    );
                }
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onCompleted();
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(
                        FindMaxResponse.newBuilder()
                                .setMaximum(curMax)
                                .build()
                );

                 // server is done s4nding data
                //
                 responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void squareRoot(SquareRootRequest request, StreamObserver<SquareRootResponse> responseObserver) {

        Integer number = request.getNumber();

        if (number > 0) {
            double root = Math.sqrt(number);
            responseObserver.onNext(SquareRootResponse.newBuilder()
                    .setSquareRoot(root)
                    .build()
            );
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                    .withDescription("The number sent is not positive")
                            .augmentDescription("Number sent = " + number)
                    .asRuntimeException()
            );
        }
    }
}
