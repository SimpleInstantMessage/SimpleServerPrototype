package gq.baijie.tryit.proto.business.service

import static gq.baijie.tryit.proto.business.service.utils.Looper.loop
import okio.ByteString

import java.security.SecureRandom

class SyncSessionService implements SessionService {

  private static final Random RANDOM = SecureRandom.instanceStrong

  private final AccountService accountService

  // account token -> name
  private final Map<ByteString, String> tokens = [:]

  SyncSessionService(AccountService accountService) {
    this.accountService = accountService
  }

  @Override
  synchronized String create(String name, String password) {
    if (accountService.authenticate(name, password)) {
      return newToken(name).base64Url()
    } else {
      throw new IllegalStateException('authenticate failed')
    }
  }

  @Override
  synchronized boolean authenticate(String token) {
    return tokens.containsKey(ByteString.decodeBase64(token))
  }

  @Override
  synchronized void destroy(String token) {
    //TODO authenticate first?
    tokens.remove(ByteString.decodeBase64(token))
  }

  synchronized private ByteString newToken(String name) {
    ByteString newToken
    loop {
      // 33 = 24 * 11 / 8 where 24 is Least Common Multiple of 6 and 8
      // so this token no base64 padding
      byte[] raw = new byte[33]
      RANDOM.nextBytes(raw)
      newToken = ByteString.of(raw, 0, raw.length)
    } until {!tokens.containsKey(newToken)}
    tokens.put(newToken, name)
    return newToken
  }

}
