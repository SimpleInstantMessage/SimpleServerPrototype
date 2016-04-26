package gq.baijie.tryit.proto.grpc.account

import gq.baijie.tryit.proto.business.service.SyncAccountService
import gq.baijie.tryit.proto.business.service.AccountService as BusinessAccountService
import gq.baijie.tryit.proto.message.AccountMessage
import io.grpc.stub.StreamObserver

class AccountImpl implements AccountGrpc.Account {

  private final BusinessAccountService accountService = new SyncAccountService()

  @Override
  void create(AccountMessage.AccountCredential request,
                     StreamObserver<AccountMessage.Response> responseObserver) {
    try {
//      throw new RuntimeException()
      accountService.create(request.name, request.password)
      def response = AccountMessage.Response.newBuilder()
          .setSuccess(true)
          .build()
      responseObserver.onNext(response)
      responseObserver.onCompleted()
    } catch (Throwable cause) {
      def response = AccountMessage.Response.newBuilder()
          .setSuccess(false)
          .setCause(cause.message)
          .build()
      responseObserver.onNext(response)
      responseObserver.onCompleted()
//      responseObserver.onError(cause)
    }
  }

}
