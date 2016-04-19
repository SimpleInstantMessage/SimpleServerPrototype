package gq.baijie.tryit.proto.business.router.service

import gq.baijie.tryit.proto.business.router.Message
import gq.baijie.tryit.proto.business.router.Port
import gq.baijie.tryit.proto.business.router.Routers
import gq.baijie.tryit.proto.message.Request

class EchoService implements Port {

  static final String ADDRESS = 'service:echo'

  @Override
  void onReceive(Message message) {
    println "[${Thread.currentThread()}]EchoService onReceive:"
    println message
    println message.message
    message.message.with {
      def isSearchRequest = it.is Request.SearchRequest
      println isSearchRequest
      if (isSearchRequest) {
        println unpack(Request.SearchRequest)
      }
    }
    Routers.defaultRouter.send(ADDRESS, message.sender, message.sessionId, message.message)
  }

}
