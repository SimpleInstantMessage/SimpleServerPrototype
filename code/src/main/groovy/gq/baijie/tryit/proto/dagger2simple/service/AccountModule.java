package gq.baijie.tryit.proto.dagger2simple.service;

import dagger.Module;
import dagger.Provides;

@Module
public class AccountModule {

  @Provides
  static Account provideAccount() {
    return new AccountImpl();
  }

}
