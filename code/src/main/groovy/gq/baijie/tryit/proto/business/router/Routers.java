package gq.baijie.tryit.proto.business.router;

public class Routers {

  private static final Router DEFAULT_ROUTER = new SyncRouter();

  public static Router getDefaultRouter() {
    return DEFAULT_ROUTER;
  }

}
