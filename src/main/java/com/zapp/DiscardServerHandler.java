//package com.zapp;
//
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.ChannelHandlerAdapter;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.util.ReferenceCountUtil;
//
///**
// * @author zwd
// * @date 2018/1/8 15:41
// */
//public class DiscardServerHandler extends ChannelHandlerAdapter{
//
////    @Override
////    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
//////        // Discard the received data silently.
//////        ByteBuf in=(ByteBuf) msg; // (3)
//////        try{
//////            while (in.isReadable()){
//////                System.out.println((char) in.readByte());
//////                System.out.flush();
//////            }
//////        }finally {
//////            ReferenceCountUtil.release(msg);
//////        }
////        ctx.write(msg);
////        ctx.flush();
////    }
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
//        // Close the connection when an exception is raised.
//        cause.printStackTrace();
//        ctx.close();
//    }
//
//}
