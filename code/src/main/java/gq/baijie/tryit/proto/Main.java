package gq.baijie.tryit.proto;

import gq.baijie.tryit.proto.message.Request;

public class Main {

  public static void main(String[] args) {
    System.out.println("Hello World!");
    Request.SearchRequest request = Request.SearchRequest.getDefaultInstance();
    print(request);
    request = request.toBuilder()
        .setQuery("test query")
        .setPageNumber(10)
        .setResultPerPage(5)
        .build();
    print(request);
  }

  private static void print(Object object) {
    System.out.println(object.getClass().getName());
    System.out.println(object);
  }

}
