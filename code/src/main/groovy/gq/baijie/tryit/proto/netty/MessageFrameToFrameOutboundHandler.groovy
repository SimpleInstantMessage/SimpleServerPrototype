package gq.baijie.tryit.proto.netty

import gq.baijie.tryit.proto.message.MessageFrameOuterClass
import io.netty.buffer.ByteBufOutputStream
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelOutboundHandlerAdapter
import io.netty.channel.ChannelPromise

class MessageFrameToFrameOutboundHandler extends ChannelOutboundHandlerAdapter  {

  @Override
  void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    //super.write(ctx, msg, promise)
    MessageFrameOuterClass.MessageFrame message = msg as MessageFrameOuterClass.MessageFrame
    msg = ctx.alloc().buffer(message.serializedSize)
    message.writeTo(new ByteBufOutputStream(msg))
    ctx.write(msg, promise)
  }

}
