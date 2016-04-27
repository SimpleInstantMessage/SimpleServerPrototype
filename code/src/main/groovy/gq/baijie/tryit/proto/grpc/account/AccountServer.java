package gq.baijie.tryit.proto.grpc.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gq.baijie.tryit.proto.business.service.SyncAccountService;
import gq.baijie.tryit.proto.business.service.SyncSessionService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class AccountServer {

  private final Logger logger = LoggerFactory.getLogger(AccountServer.class);

  /* The port on which the server should run */
  private int port = 50051;
  private Server server;

  public AccountServer(int port) {
    this.port = port;
  }

  public void start() throws Exception {
    final SyncAccountService accountService = new SyncAccountService();
    final SyncSessionService sessionService = new SyncSessionService(accountService);
    server = ServerBuilder.forPort(port)
        .addService(AccountGrpc.bindService(new AccountImpl(accountService)))
        .addService(SessionGrpc.bindService(new SessionImpl(sessionService)))
        .build()
        .start();
    logger.info("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may has been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        AccountServer.this.stop();
        System.err.println("*** server shut down");
      }
    });
  }

  public void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon threads.
   */
  public void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

}
