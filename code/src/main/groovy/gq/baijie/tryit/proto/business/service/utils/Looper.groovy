// copy from http://stackoverflow.com/a/20925228
package gq.baijie.tryit.proto.business.service.utils

import groovy.transform.CompileStatic

@CompileStatic
class Looper {
  private Closure code

  static Looper loop( Closure code ) {
    new Looper(code:code)
  }

  void until( Closure test ) {
    code()
    while (!test()) {
      code()
    }
  }
}
