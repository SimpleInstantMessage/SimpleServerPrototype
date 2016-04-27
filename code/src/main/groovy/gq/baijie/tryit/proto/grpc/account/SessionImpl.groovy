package gq.baijie.tryit.proto.grpc.account

import com.google.protobuf.Any;
import gq.baijie.tryit.proto.business.service.SessionService
import gq.baijie.tryit.proto.message.AccountMessage
import io.grpc.stub.StreamObserver

public class SessionImpl implements SessionGrpc.Session {

  private final SessionService sessionService

  public SessionImpl(SessionService sessionService) {
    this.sessionService = sessionService
  }

  @Override
  public void create(AccountMessage.AccountCredential request,
                     StreamObserver<AccountMessage.Response> responseObserver) {
    try{
      final String token = sessionService.create(request.getName(), request.getPassword());
      def response = AccountMessage.Response.newBuilder().with {
        success = true
        value = Any.pack(AccountMessage.Token.newBuilder().setToken(token).build())
        build()
      }
      responseObserver.onNext(response)
      responseObserver.onCompleted()
    }catch (Throwable cause) {
      def response = AccountMessage.Response.newBuilder()
          .setSuccess(false)
          .setCause(cause.message)
          .build()
      responseObserver.onNext(response)
      responseObserver.onCompleted()
    }
  }

  @Override
  public void authenticate(AccountMessage.Token request,
                           StreamObserver<AccountMessage.Response> responseObserver) {
    def response
    if (sessionService.authenticate(request.token)) {
      response = AccountMessage.Response.newBuilder()
          .setSuccess(true)
          .build()
    } else {
      response = AccountMessage.Response.newBuilder()
          .setSuccess(false)
          .setCause('invalid token')
          .build()
    }
    responseObserver.onNext(response)
    responseObserver.onCompleted()
  }
}
