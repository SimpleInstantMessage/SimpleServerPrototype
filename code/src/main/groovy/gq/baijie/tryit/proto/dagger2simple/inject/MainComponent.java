package gq.baijie.tryit.proto.dagger2simple.inject;

import dagger.Component;
import gq.baijie.tryit.proto.dagger2simple.service.Account;
import gq.baijie.tryit.proto.dagger2simple.service.AccountModule;

@Component(modules = AccountModule.class)
public interface MainComponent {

  Account account();

}
