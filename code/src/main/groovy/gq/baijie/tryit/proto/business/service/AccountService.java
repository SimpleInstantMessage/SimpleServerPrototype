package gq.baijie.tryit.proto.business.service;

public interface AccountService {


  static final String ADDRESS = "service:user";

  void create(String name, String password);

  boolean authenticate(String name, String password);

  void destroy(String name, String password);

}
