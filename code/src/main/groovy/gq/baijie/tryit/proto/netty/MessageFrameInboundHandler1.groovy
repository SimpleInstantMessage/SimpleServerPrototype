package gq.baijie.tryit.proto.netty

import gq.baijie.tryit.proto.message.AccountMessage
import gq.baijie.tryit.proto.message.MessageFrameOuterClass
import gq.baijie.tryit.proto.message.Request
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class MessageFrameInboundHandler1 extends ChannelInboundHandlerAdapter {

  @Override
  void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    def message = msg as MessageFrameOuterClass.MessageFrame
    println "at eventLoop: ${ctx.channel().eventLoop()}"
    println "at executor: ${ctx.executor()}"
    println "[${Thread.currentThread()}]client read:"
    println message

    [Request.SearchRequest, AccountMessage.CreateAccountResponse]
        .find { message.message.is(it) }
        .with {
          println message.message.unpack(it)
        }

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

}
