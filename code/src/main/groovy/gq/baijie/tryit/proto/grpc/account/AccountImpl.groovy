package gq.baijie.tryit.proto.grpc.account

import gq.baijie.tryit.proto.business.service.SyncAccountService
import gq.baijie.tryit.proto.business.service.AccountService as BusinessAccountService
import gq.baijie.tryit.proto.message.AccountMessage
import io.grpc.stub.StreamObserver

class AccountImpl implements AccountGrpc.Account {

  private final BusinessAccountService accountService = new SyncAccountService()

  @Override
  void createAccount(AccountMessage.CreateAccountRequest request,
                     StreamObserver<AccountMessage.CreateAccountResponse> responseObserver) {
    try {
//      throw new RuntimeException()
      accountService.create(request.name, request.password)
      def response = AccountMessage.CreateAccountResponse.newBuilder()
          .setSuccess(true)
          .build()
      responseObserver.onNext(response)
      responseObserver.onCompleted()
    } catch (Throwable cause) {
      responseObserver.onError(cause)
    }
  }

}
