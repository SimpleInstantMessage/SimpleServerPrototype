package gq.baijie.tryit.proto

import gq.baijie.tryit.proto.business.router.Routers
import gq.baijie.tryit.proto.business.router.service.AccountService
import gq.baijie.tryit.proto.business.router.service.DropService
import gq.baijie.tryit.proto.business.router.service.EchoService
import gq.baijie.tryit.proto.dagger2.inject.DaggerMainComponent;
import gq.baijie.tryit.proto.dagger2.inject.MainComponent
import gq.baijie.tryit.proto.dagger2.service.Account
import gq.baijie.tryit.proto.grpc.account.AccountGrpc
import gq.baijie.tryit.proto.grpc.account.AccountServer
import gq.baijie.tryit.proto.message.AccountMessage
import gq.baijie.tryit.proto.message.Request
import gq.baijie.tryit.proto.netty.FrameToMessageFrameInboundHandler
import gq.baijie.tryit.proto.netty.MessageFrameInboundHandler1
import gq.baijie.tryit.proto.netty.MessageFrameInboundHandler2
import gq.baijie.tryit.proto.netty.MessageFrameToFrameOutboundHandler
import gq.baijie.tryit.proto.netty.client.CreateAccountHandler
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.examples.GreeterGrpc
import io.grpc.examples.HelloWorldServer
import io.grpc.examples.SampleService
import io.netty.bootstrap.Bootstrap
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender
import io.netty.handler.logging.LoggingHandler
import io.netty.util.internal.logging.InternalLoggerFactory
import io.netty.util.internal.logging.Slf4JLoggerFactory
import io.vertx.groovy.core.Vertx
import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.parsetools.RecordParser
import okhttp3.OkHttpClient
import okio.ByteString
import org.slf4j.LoggerFactory
import rx.Observable
import rx.Subscriber

class MainG {

  public static void main(String[] args) {
//    tryProto()
//    tryVertx()
//    tryRecordParser()
//    tryRecordParser2()
//    tryOkHttp()
//    tryNetty()
//    tryGrpc()
//    tryGrpcAccount()
    tryDagger2Impl()
  }

  private static void tryProto() {
    def request = Request.SearchRequest.newBuilder()
        .setQuery("test query")
        .setPageNumber(10)
        .setResultPerPage(5)
        .build()
    println request
  }

  private static void tryVertx() {
    def vertx = Vertx.vertx()
    vertx.executeBlocking(
        { future ->
          log "before Thread.sleep(10000)"
          log 0
          10.times {
            log it + 1
            Thread.sleep(1000)
          }
          log "after Thread.sleep(10000)"
          future.complete()
        },
        { res ->
          log 'completed'
          log "The result is: ${res.result()}"
          vertx.close()
        })
  }

  private static void log(Object object) {
    println "[${Thread.currentThread()}]$object"
  }

  private static void tryRecordParser() {
    def parser = RecordParser.newDelimited("\n", {h ->
      println(h.toString())
    })

    parser.handle(Buffer.buffer("HELLO\nHOW ARE Y"))
    parser.handle(Buffer.buffer("OU?\nI AM"))
    parser.handle(Buffer.buffer(" DOING OK"))
    parser.handle(Buffer.buffer("\n"))
  }
  private static void tryRecordParser2() {
    def parser = RecordParser.newDelimited("\r\n", {h ->
      println(h.toString())
    })

    parser.handle(Buffer.buffer("HELLO\r\nHOW ARE Y"))
    parser.handle(Buffer.buffer("OU?\r\nI AM"))
    parser.handle(Buffer.buffer(" DOING OK\r"))
    parser.handle(Buffer.buffer("\ntest\r\ntes"))
    parser.handle(Buffer.buffer("\r\n"))
  }

  private static void tryOkHttp() {
/*
    def trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.defaultAlgorithm)
    trustManagerFactory.init(KeyStore.getInstance('Windows-ROOT'))
    def keyStore = KeyStore.getInstance('Windows-ROOT')
    println keyStore
    println trustManagerFactory.trustManagers
    def sslContext = SSLContext.getInstance('TLS')
    sslContext.init(null, trustManagerFactory.trustManagers, null)
    //SSLContext.default.init(null, trustManagerFactory.trustManagers, null)

    def client = new OkHttpClient.Builder().sslSocketFactory(sslContext.socketFactory).build()
*/
    def client = new OkHttpClient()
    def request = new okhttp3.Request.Builder()
        .url("http://www.baidu.com")
        .url("https://baijie.gq")
        .url("http://baijie.gq")
        .build()
    def response = client.newCall(request).execute()
//    println response.body()
    println response.protocol()
    println response.code()
    println response.message()
    println response.headers().toString()
    println response.body().contentLength()
    println response.body().contentType()
    println response.body().string()
  }

  private static void tryNetty() {
//    System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
    System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'debug')
    System.setProperty('org.slf4j.simpleLogger.logFile', 'build/log.txt')
    InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
//    InternalLoggerFactory.setDefaultFactory(new JdkLoggerFactory());
    LoggerFactory.getLogger(MainG).warn('Hello from slf4j loger')

    tryNettyServer(56789)
    tryNettyClient("localhost", 56789)
  }

  private static void tryNettyServer(int port) {
    Routers.defaultRouter.addReceiver('service:drop', new DropService())
    Routers.defaultRouter.addReceiver('service:echo', new EchoService())
    Routers.defaultRouter.addReceiver('service:account', new AccountService())

    EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      ServerBootstrap b = new ServerBootstrap(); // (2)
      b.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class) // (3)
          .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
    //          ch.pipeline().addLast(new DiscardServerHandler());
              ch.pipeline().with {
                addLast(new LoggingHandler('server'))
                // outbound
                addLast(new ProtobufVarint32LengthFieldPrepender())
                addLast(new MessageFrameToFrameOutboundHandler())
                // inbound
                addLast(new ProtobufVarint32FrameDecoder())
                addLast(new FrameToMessageFrameInboundHandler())
                // business
                addLast(new MessageFrameInboundHandler2())
              }
            }
          })
          .option(ChannelOption.SO_BACKLOG, 128)          // (5)
          .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

      // Bind and start to accept incoming connections.
      ChannelFuture f = b.bind(port).sync(); // (7)

      // Wait until the server socket is closed.
      // In this example, this does not happen, but you can do that to gracefully
      // shut down your server.
//      f.channel().closeFuture().sync();
      f.channel().closeFuture().addListener({
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
      })
    } finally {
//      workerGroup.shutdownGracefully();
//      bossGroup.shutdownGracefully();
    }
  }

  private static void tryNettyClient(String host, int port) {
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    try {
      Bootstrap b = new Bootstrap(); // (1)
      b.group(workerGroup); // (2)
      b.channel(NioSocketChannel.class); // (3)
      b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
      b.handler(new ChannelInitializer<SocketChannel>() {
        @Override
        public void initChannel(SocketChannel ch) throws Exception {
//          ch.pipeline().addLast(new TimeClientHandler());
          ch.pipeline().with {
            addLast(new LoggingHandler('client'))
            // outbound
            addLast(new ProtobufVarint32LengthFieldPrepender())
            addLast(new MessageFrameToFrameOutboundHandler())
            // inbound
            addLast(new ProtobufVarint32FrameDecoder())
            addLast(new FrameToMessageFrameInboundHandler())
            // business
            addLast(new MessageFrameInboundHandler1())
//            addLast(new ClientHandler())
            addLast(new CreateAccountHandler())
          }
        }
      });

      // Start the client.
      ChannelFuture f = b.connect(host, port).sync(); // (5)

      // Wait until the connection is closed.
//      f.channel().closeFuture().sync();
      f.channel().closeFuture().addListener{
        workerGroup.shutdownGracefully()
      }
    } finally {
//      workerGroup.shutdownGracefully();
    }
  }

  private static void tryGrpc() {
    def host = 'localhost'
    def port = 56789

    HelloWorldServer server = new HelloWorldServer(port)
    server.start()

    ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext(true)
        .build();
    def blockingStub = GreeterGrpc.newBlockingStub(channel);
    SampleService.HelloRequest req = SampleService.HelloRequest.newBuilder().setName('baijie').build();
    SampleService.HelloReply reply = blockingStub.sayHello(req);
    println 'received reply:'
    println reply

    server.stop()
    server.blockUntilShutdown()
  }

  private static void tryGrpcAccount() {
    def host = 'localhost'
    def port = 56789

    def server = new AccountServer(port)
    server.start()

    ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext(true)
        .build();
    def blockingStub = AccountGrpc.newBlockingStub(channel);
    def request = AccountMessage.CreateAccountRequest.newBuilder().with {
      name = 'baijie'
      password = ByteString.encodeUtf8(name).sha256().hex()
      build()
    }
    def reply = blockingStub.createAccount(request);
    println 'received reply:'
    println reply

    server.stop()
    server.blockUntilShutdown()
  }

  private static void tryDagger2Impl() {
    MainComponent main = DaggerMainComponent.create();
    final Account account = main.account();

    Observable.merge(
        account.create("user1", "password1"),
        account.create("user2", "password2"),
//        account.create("user1", "password1"),
        account.create("user3", "password3")
    ).subscribe(new Subscriber<Void>() {
      @Override
      public void onCompleted() {
        System.out.println("onCompleted");
      }

      @Override
      public void onError(Throwable e) {
        System.out.println("onError:");
        e.printStackTrace();
      }

      @Override
      public void onNext(Void aVoid) {
        System.out.println("onNext");
      }
    });
    System.out.println(account);

    /*account.create("baijie", "password").subscribe(new Subscriber<Void>() {
      @Override
      public void onCompleted() {
        System.out.println("onCompleted");
      }

      @Override
      public void onError(Throwable e) {
        System.out.println("onError:");
        e.printStackTrace();
      }

      @Override
      public void onNext(Void aVoid) {
        System.out.println("onNext");
      }
    });

    account.create("baijie", "password").subscribe(new Subscriber<Void>() {
      @Override
      public void onCompleted() {
        System.out.println("onCompleted");
      }

      @Override
      public void onError(Throwable e) {
        System.out.println("onError:");
        e.printStackTrace();
      }

      @Override
      public void onNext(Void aVoid) {
        System.out.println("onNext");
      }
    });*/
  }


}
