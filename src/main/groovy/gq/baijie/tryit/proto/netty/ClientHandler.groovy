package gq.baijie.tryit.proto.netty

import com.google.protobuf.Any
import gq.baijie.tryit.proto.message.MessageFrameOuterClass
import gq.baijie.tryit.proto.message.Request
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

import java.util.concurrent.TimeUnit

class ClientHandler extends ChannelInboundHandlerAdapter {

  @Override
  void channelActive(ChannelHandlerContext ctx) throws Exception {
    Request.SearchRequest request = Request.SearchRequest.newBuilder().with {
      query = 'test query'
      pageNumber = 17
      resultPerPage = 7
      build()
    }
    def message = MessageFrameOuterClass.MessageFrame.newBuilder().with {
      sessionId = 66
      serviceId = 'drop'
      message = Any.pack(request)
      build()
    }
    ctx.write(message)
    message = message.toBuilder().setServiceId('echo').build()
    ctx.writeAndFlush(message).addListener({ChannelFuture future ->
      if (!future.success) {
        future.cause().printStackTrace();
      }
      ctx.executor().schedule({ctx.close()}, 2, TimeUnit.SECONDS)
//      future.channel().close()
//      ctx.writeAndFlush(message)/*.addListener{it.channel().close()}*/
    } as ChannelFutureListener)
  }

}
