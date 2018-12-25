package com.zwd.netty.rpc.server;

import com.sun.corba.se.internal.CosNaming.BootstrapServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author zwd
 * @date 2018/12/25 19:28
 * @Email stephen.zwd@gmail.com
 */
public class NettyServer {

    private Integer port = 6789;
    private static NettyServer nettyServer = new NettyServer();
    public static void main(String[] args) throws InterruptedException {
        nettyServer.run();
    }

    public  void run() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        try {


        serverBootstrap.group(group);
        serverBootstrap.childHandler(new NettyServerFilter());
        serverBootstrap.channel(NioServerSocketChannel.class);
        ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
        System.out.println("服务端启动成功！");
        channelFuture.channel().closeFuture().sync();

        }finally {
            group.shutdownGracefully();
        }
    }
}
