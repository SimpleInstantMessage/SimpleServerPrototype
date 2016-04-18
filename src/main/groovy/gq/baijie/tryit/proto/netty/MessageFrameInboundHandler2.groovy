package gq.baijie.tryit.proto.netty

import com.google.protobuf.Any
import gq.baijie.tryit.proto.business.router.Message
import gq.baijie.tryit.proto.business.router.Port
import gq.baijie.tryit.proto.business.router.Routers
import gq.baijie.tryit.proto.message.MessageFrameOuterClass
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

import java.util.concurrent.atomic.AtomicLong

class MessageFrameInboundHandler2 extends ChannelInboundHandlerAdapter implements Port {

  private static AtomicLong counter = new AtomicLong()

  private final long id = counter.incrementAndGet()

  private final String address = "client:$id"

  private ChannelHandlerContext ctx

  @Override
  void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    this.ctx = ctx
    Routers.defaultRouter.addReceiver(address, this)
  }

  @Override
  void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    Routers.defaultRouter.removeReceiver(address)
    this.ctx = null
  }

  @Override
  void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    def message = msg as MessageFrameOuterClass.MessageFrame
    Routers.defaultRouter.send(address, "service:${message.serviceId}", message.sessionId, message.message)

//    println message

    new Thread(){
      @Override
      void run() {
        sleep(2000)
        ctx.executor().submit{close(ctx)}
      }
    }.start();

  }

  private static void close(ChannelHandlerContext ctx) {
    ctx.close()
    ctx.channel().parent().close()
  }

  @Override
  void onReceive(Message businessMessage) {
    ctx?.executor()?.submit{
      def message = MessageFrameOuterClass.MessageFrame.newBuilder()
          .setServiceId(toServiceId(businessMessage.sender))
          .setSessionId(businessMessage.sessionId)
          .setMessage(businessMessage.message as Any)
          .build()
      ctx?.writeAndFlush(message)
    }
  }

  private static String toServiceId(String serviceAddress) {
    serviceAddress.replace('service:', '')
  }

}
