package gq.baijie.tryit.proto.business.service;

public interface SessionService {

  /**
   * login
   * @param name
   * @param password
   * @return token
   */
  String create(String name, String password);

  /**
   * authenticate token
   * @param token
   * @return success of failure
   */
  boolean authenticate(String token);

  /**
   * logout
   * @param token
   */
  void destroy(String token);

}
