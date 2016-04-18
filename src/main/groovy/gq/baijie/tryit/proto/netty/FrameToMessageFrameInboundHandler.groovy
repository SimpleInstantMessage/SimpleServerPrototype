package gq.baijie.tryit.proto.netty

import gq.baijie.tryit.proto.message.MessageFrameOuterClass
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class FrameToMessageFrameInboundHandler extends ChannelInboundHandlerAdapter  {

  @Override
  void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    def buffer = msg as ByteBuf
    msg = MessageFrameOuterClass.MessageFrame.parseFrom(new ByteBufInputStream(buffer))
    buffer.release()
    ctx.fireChannelRead(msg)
  }

}
