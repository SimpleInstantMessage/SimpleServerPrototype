package gq.baijie.tryit.proto.business.router;

public interface Router {

  void addReceiver(String address, Port port);

  void removeReceiver(Port port);

  void removeReceiver(String address);

  void send(String sender, String receiver, int sessionId, Object message);

}
