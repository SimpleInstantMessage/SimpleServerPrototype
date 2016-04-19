package gq.baijie.tryit.proto.business.router;

public interface ClientHandlerRouter {

  void addHandlerReceiver(Port port);

  void removeHandlerReceiver(Port port);

  void send(String receiver, int sessionId, Object message);

}
