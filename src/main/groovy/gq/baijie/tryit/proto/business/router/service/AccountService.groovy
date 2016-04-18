package gq.baijie.tryit.proto.business.router.service

import com.google.protobuf.Any
import gq.baijie.tryit.proto.business.router.Message
import gq.baijie.tryit.proto.business.router.Port
import gq.baijie.tryit.proto.business.router.Routers
import gq.baijie.tryit.proto.business.service.AccountService as BusinessAccountService
import gq.baijie.tryit.proto.business.service.SyncAccountService
import gq.baijie.tryit.proto.message.AccountMessage

class AccountService implements Port {

  static final String ADDRESS = 'service:account'

  /*private static final Map<Class, Closure> HANDLERS = [
      (AccountMessage.CreateAccountRequest): {createAccount(it)}
  ]*/
  private final Map<String, Closure> HANDLERS = [
      'type.googleapis.com/message.CreateAccountRequest': {createAccount(it)}
  ]

  private final BusinessAccountService accountService = new SyncAccountService()

  @Override
  void onReceive(Message msg) {
    def message = msg.message
    if(message instanceof Any) {
      HANDLERS[message.typeUrl]?.call(msg)//TODO unknown request type
      /*switch (message.typeUrl) {
        CreateAcc
      }*/
    }
  }

  private void createAccount(Message msg) {
    def message = (msg.message as Any).unpack(AccountMessage.CreateAccountRequest)
    def response
    try {
      accountService.create(message.name, message.password)
      response = AccountMessage.CreateAccountResponse.newBuilder()
          .setSuccess(true)
          .build()
    } catch (Throwable cause) {
      response = AccountMessage.CreateAccountResponse.newBuilder()
          .setSuccess(false)
          .setCause(cause.message)
          .build()
    }
    Routers.defaultRouter.send(ADDRESS, msg.sender, msg.sessionId, Any.pack(response))
  }


}
