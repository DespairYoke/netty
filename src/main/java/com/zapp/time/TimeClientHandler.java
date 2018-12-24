//package com.zapp.time;
//
//import com.zapp.time.entity.UnixTime;
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.ChannelHandlerAdapter;
//import io.netty.channel.ChannelHandlerContext;
//
//import java.util.Date;
//
///**
// * @author zwd
// * @date 2018/1/8 19:56
// */
//public class TimeClientHandler extends ChannelHandlerAdapter {
//
//    private ByteBuf buf;
//
//    @Override
//    public void handlerAdded(ChannelHandlerContext ctx) {
//        buf = ctx.alloc().buffer(4); // (1)
//    }
//
//    @Override
//    public void handlerRemoved(ChannelHandlerContext ctx) {
//        buf.release(); // (1)
//        buf = null;
//    }
//
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) {
//
//        System.out.println("wwww");
//        UnixTime m = (UnixTime) msg;
//        System.out.println(m);
//        ctx.close();
////        buf.writeBytes(m); // (2)
////        try{
////            long currentTime=(buf.readUnsignedInt()-2208988800L)*1000L;
////            System.out.println(new Date(currentTime));
////            ctx.close();
////        }finally {
////            m.release();
////        }
////
////        if (buf.readableBytes() >= 4) { // (3)
////            long currentTimeMillis = (buf.readUnsignedInt() - 2208988800L) * 1000L;
////            System.out.println(new Date(currentTimeMillis));
////            ctx.close();
////        }
//    }
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//        cause.printStackTrace();
//        ctx.close();
//    }
//}
