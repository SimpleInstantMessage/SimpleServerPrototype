package io.grpc.examples;

import io.grpc.stub.StreamObserver;

public class GreeterImpl implements GreeterGrpc.Greeter {

  @Override
  public void sayHello(SampleService.HelloRequest request,
                       StreamObserver<SampleService.HelloReply> responseObserver) {
    SampleService.HelloReply reply = SampleService.HelloReply.newBuilder()
        .setMessage("Hello " + request.getName())
        .build();
    responseObserver.onNext(reply);
    responseObserver.onCompleted();
  }
}
