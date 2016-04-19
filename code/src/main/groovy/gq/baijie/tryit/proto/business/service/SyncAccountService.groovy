package gq.baijie.tryit.proto.business.service

class SyncAccountService implements AccountService {

  // account name -> password
  Map<String, String> accounts = [:]

  @Override
  synchronized void create(String name, String password) {
    if(accounts[name] != null) {
      throw new IllegalStateException('exists')
    }
    accounts[name] = password
  }

  @Override
  synchronized boolean authenticate(String name, String password) {
    Objects.requireNonNull(password, 'password')
    return password == accounts[name]
  }

  @Override
  synchronized void destroy(String name, String password) {
    if(!authenticate(name, password)) {
      throw new IllegalStateException('authenticate failed')
    }
    accounts.remove(name)
  }

}
