package gq.baijie.tryit.proto.business.service

import gq.baijie.tryit.proto.business.router.Message
import gq.baijie.tryit.proto.business.router.Port
import gq.baijie.tryit.proto.message.Request

class DropService implements Port {

  @Override
  void onReceive(Message message) {
    println "[${Thread.currentThread()}]DropService onReceive:"
    println message
    println message.message
    message.message.with {
      def isSearchRequest = it.is Request.SearchRequest
      println isSearchRequest
      if (isSearchRequest) {
        println unpack(Request.SearchRequest)
      }
    }
  }

}
