//package com.zapp;
//
//import io.netty.bootstrap.ServerBootstrap;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioServerSocketChannel;
//
///**
// * @author zwd
// * @date 2018/1/8 15:58
// */
//public class DiscardServer {
//    private int port;
//
//    public DiscardServer(int port) {
//        this.port = port;
//    }
//    public void run() throws Exception{
//        EventLoopGroup bossGroup=new NioEventLoopGroup();
//        EventLoopGroup workerGroup=new NioEventLoopGroup();
//        try{
//            ServerBootstrap b=new ServerBootstrap();
//            b.group(bossGroup,workerGroup)
//                    .channel(NioServerSocketChannel.class)
//                    .childHandler(new ChannelInitializer<SocketChannel>() {
//                        @Override
//                        protected void initChannel(SocketChannel socketChannel) {
//                            socketChannel.pipeline().addLast(new DiscardServerHandler());
//                        }
//                    }).option(ChannelOption.SO_BACKLOG,128)
//                        .childOption(ChannelOption.SO_KEEPALIVE,true);
//            ChannelFuture f=b.bind(port).sync();
//            f.channel().closeFuture().sync();
//        }finally {
//            workerGroup.shutdownGracefully();
//            bossGroup.shutdownGracefully();
//        }
//    }
//    public static void main(String[] args) throws Exception {
//        int port;
//        if (args.length > 0) {
//            port = Integer.parseInt(args[0]);
//        } else {
//            port = 8080;
//        }
//        new DiscardServer(port).run();
//    }
//}
