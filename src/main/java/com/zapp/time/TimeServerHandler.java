package com.zapp.time;

import com.zapp.time.entity.UnixTime;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;

/**
 * @author zwd
 * @date 2018/1/8 19:51
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter{
    /**
     * 启动端口监听服务,当有client来访问这个端口时,执行此方法,此方法不接受client传递过来的
     * 消息，只负责传递消息给client并立即关闭请求
     * @param ctx
     */
    @Override
   public void channelActive(final ChannelHandlerContext ctx) { // (1)
//        final ByteBuf time = ctx.alloc().buffer(4); //
//        time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));
//        System.out.println(time);
//        final ChannelFuture f = ctx.writeAndFlush(time); // (3)
//        f.addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture future) {
//                assert f == future;
//                ctx.close();
//            }
//        }); // (4)
    /**
     *对象的使用
     *
     */
        System.out.println("aaaa");
        UnixTime unixTime=new UnixTime();
        System.out.println(unixTime);
        final ChannelFuture f=ctx.writeAndFlush(new UnixTime());
    f.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
