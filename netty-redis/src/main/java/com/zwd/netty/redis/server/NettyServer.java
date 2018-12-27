package com.zwd.netty.redis.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.redis.RedisArrayAggregator;
import io.netty.handler.codec.redis.RedisBulkStringAggregator;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.handler.codec.redis.RedisEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author zwd
 * @date 2018/12/25 19:28
 * @Email stephen.zwd@gmail.com
 */
public class NettyServer {

    private Integer port = 6379;
    private static NettyServer nettyServer = new NettyServer();
    public static void main(String[] args) throws InterruptedException {
        nettyServer.run();
    }

    public  void run() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        try {


        serverBootstrap.group(group);
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline p = socketChannel.pipeline();
                p.addLast(new RedisDecoder());
                p.addLast(new RedisBulkStringAggregator());
                p.addLast(new RedisArrayAggregator());
                p.addLast(new RedisEncoder());
                p.addLast(new NettyServerHandler());
            }
        });
        serverBootstrap.channel(NioServerSocketChannel.class);
        ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
        System.out.println("服务端启动成功！");
        channelFuture.channel().closeFuture().sync();

        }finally {
            group.shutdownGracefully();
        }
    }
}
