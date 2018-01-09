package com.zapp.time;

import com.zapp.time.entity.UnixTime;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * @author zwd
 * @date 2018/1/9 10:44
 */
public class TimeEncoder extends ChannelHandlerAdapter{

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("1111");
        UnixTime m= (UnixTime) msg;
        ByteBuf encoded=ctx.alloc().buffer(4);
        encoded.writeLong(m.value());
        ctx.write(encoded,promise);
    }
}
