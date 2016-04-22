package gq.baijie.tryit.proto.dagger2.inject;

import dagger.Component;
import gq.baijie.tryit.proto.dagger2.service.Account;
import gq.baijie.tryit.proto.dagger2.service.AccountModule;

@Component(modules = AccountModule.class)
public interface MainComponent {

  Account account();

}
