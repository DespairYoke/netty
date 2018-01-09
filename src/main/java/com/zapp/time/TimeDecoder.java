package com.zapp.time;

import com.zapp.time.entity.UnixTime;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * @author zwd
 * @date 2018/1/9 09:49
 */
public class TimeDecoder extends ReplayingDecoder{
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("zzzz");
        if (in.readableBytes()<4) {
           return;
      }
        System.out.println(in.readInt());
        out.add(new UnixTime((int) in.readUnsignedInt()));
    }
}
