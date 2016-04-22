package gq.baijie.tryit.proto.dagger2.service;

import rx.Observable;

public interface Account {

  Observable<Void> create(String name, String password);

}
