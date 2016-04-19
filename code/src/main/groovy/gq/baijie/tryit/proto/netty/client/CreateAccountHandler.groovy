package gq.baijie.tryit.proto.netty.client

import com.google.protobuf.Any
import gq.baijie.tryit.proto.message.AccountMessage
import gq.baijie.tryit.proto.message.MessageFrameOuterClass
import gq.baijie.tryit.proto.message.Request
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import okio.ByteString

import java.util.concurrent.TimeUnit

class CreateAccountHandler extends ChannelInboundHandlerAdapter {

  @Override
  void channelActive(ChannelHandlerContext ctx) throws Exception {
    def request = AccountMessage.CreateAccountRequest.newBuilder().with {
      name = 'baijie'
      password = ByteString.encodeUtf8(name).sha256().hex()
      build()
    }
    def message = MessageFrameOuterClass.MessageFrame.newBuilder().with {
      sessionId = 66
      serviceId = 'account'
      message = Any.pack(request)
      build()
    }
    ctx.write(message)
    ctx.writeAndFlush(message).addListener({ChannelFuture future ->
      if (!future.success) {
        future.cause().printStackTrace();
      }
      ctx.executor().schedule({ctx.close()}, 2, TimeUnit.SECONDS)
//      future.channel().close()
    } as ChannelFutureListener)
  }

}
