package gq.baijie.tryit.proto.dagger2.service;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import rx.Observable;

public class AccountImpl implements Account {

  ConcurrentMap<String, String> accounts = new ConcurrentHashMap<>();

  @Override
  public Observable<Void> create(String name, String password) {
    return Observable.create(subscriber -> {
      if (subscriber.isUnsubscribed()) {
        return;
      }
      try {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(password, "password");
      } catch (Throwable cause) {
        subscriber.onError(cause);
      }

      if (subscriber.isUnsubscribed()) {
        return;
      }
      if (accounts.putIfAbsent(name, password) == null) {
        subscriber.onCompleted();
      } else {
        subscriber.onError(new IllegalStateException("account already created"));
      }
    });
  }

}
