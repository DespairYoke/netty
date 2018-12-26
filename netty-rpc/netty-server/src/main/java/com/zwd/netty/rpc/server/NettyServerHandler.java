package com.zwd.netty.rpc.server;

import com.zwd.netty.rpc.server.config.StringConfig;
import com.zwd.netty.rpc.server.service.HelloService;
import com.zwd.netty.rpc.server.service.HelloServiceImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetAddress;


public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    /*
     * 收到消息时，返回信息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        // 收到消息直接打印输出
        System.out.println("服务端接受的消息 : " + msg);
        if (msg.toString().startsWith(StringConfig.providerName)) {

            HelloService helloService = new HelloServiceImpl();
            String substring = msg.toString().substring(msg.toString().lastIndexOf("#"));
            String hello = helloService.hello(substring);
            ctx.writeAndFlush(hello);
            System.out.println("服务端发送成功！");
        }
    }

    /*
     * 建立连接时，返回消息
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接的客户端地址:" + ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }
}