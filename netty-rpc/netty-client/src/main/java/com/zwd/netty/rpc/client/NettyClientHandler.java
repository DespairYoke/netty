package com.zwd.netty.rpc.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

/**
 * @author zwd
 * @date 2018/12/25 20:09
 * @Email stephen.zwd@gmail.com
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter implements Callable{

    private ChannelHandlerContext context;

    private String result;

    private String para; //接口名

    public void setPara(String para) {
        this.para = para;
    }



    public void channelActive(ChannelHandlerContext ctx) throws Exception {
       context = ctx;
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("断开连接");
        ctx.fireChannelInactive();
    }

    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg){
        result = msg.toString();
        System.out.println("收到服务发送的消息："+result);
        notify();

    }
    @Override
    public synchronized Object call() throws InterruptedException {
        context.writeAndFlush(para);
        wait();
        return result;
    }

}
