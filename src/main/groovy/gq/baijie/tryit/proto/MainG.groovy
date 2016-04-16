package gq.baijie.tryit.proto

import gq.baijie.tryit.proto.message.Request

class MainG {

  public static void main(String[] args) {
    def request = Request.SearchRequest.newBuilder()
        .setQuery("test query")
        .setPageNumber(10)
        .setResultPerPage(5)
        .build()
    println request
  }

}
