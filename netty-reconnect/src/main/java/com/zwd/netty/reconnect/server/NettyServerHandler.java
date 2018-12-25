package com.zwd.netty.reconnect.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;


public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /** 发送次数 */
    private int count = 1;

    /**
     * 超时处理
     * 如果5秒没有接受客户端的心跳，就触发;
     * 如果超过两次，则直接关闭;
     */
//    @Override
//    public void userEventTriggered(ChannelHandlerContext ctx, Object obj) throws Exception {
//        if (obj instanceof IdleStateEvent) {
//            IdleStateEvent event = (IdleStateEvent) obj;
//            if (IdleState.READER_IDLE.equals(event.state())) {  //如果读通道处于空闲状态，说明没有接收到心跳命令
//                System.out.println("已经5秒没有接收到客户端的信息了");
//                if (idle_count > 2) {
//                    System.out.println("关闭这个不活跃的channel");
//                    ctx.channel().close();
//                }
//                idle_count++;
//            }
//        } else {
//            super.userEventTriggered(ctx, obj);
//        }
//    }

    /**
     * 业务逻辑处理
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("第"+count+"次"+",服务端接受的消息:"+msg);
        count++;
    }

    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}