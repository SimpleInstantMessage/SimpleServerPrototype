package gq.baijie.tryit.proto.business.router

class SyncRouter implements Router {

  Map<String, Port> table = [:]

  @Override
  synchronized void addReceiver(String address, Port port) {
    if (table[address] != null) {
      throw new IllegalStateException('duplication receiver')
    }
    table[address] = port
  }

  @Override
  synchronized void removeReceiver(String address) {
    table.remove(address)
  }

  @Override
  synchronized void removeReceiver(Port port) {
    table.each { key, value ->
      if (value == port) {
        table.remove(key)
      }
    }
  }

  @Override
  synchronized void send(String sender, String receiverAddress, int sessionId, Object message) {
    def receiver = table[receiverAddress]
    if (receiver) {
      receiver.onReceive(
          new Message(sender: sender, receiver: receiverAddress, sessionId: sessionId, message: message))
    }
  }

}
