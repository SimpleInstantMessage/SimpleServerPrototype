package gq.baijie.tryit.proto.dagger2simple.service;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class AccountImpl implements Account {

  private ConcurrentMap<String, String> accounts = new ConcurrentHashMap<>();

  @Override
  public void create(String name, String password) {
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(password, "password");
    if (accounts.putIfAbsent(name, password) != null) {
      throw new IllegalStateException("account already created");
    }
  }

}
