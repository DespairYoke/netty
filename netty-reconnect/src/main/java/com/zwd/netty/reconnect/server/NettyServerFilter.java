package com.zwd.netty.reconnect.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;


public class NettyServerFilter extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline ph = ch.pipeline();
        // 以("\n")为结尾分割的 解码器
//        ph.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        // 解码和编码，应和客户端一致
        ph.addLast("decoder", new StringDecoder());
        ph.addLast("encoder", new StringEncoder());
        //1）readerIdleTime：为读超时时间（即测试端一定时间内未接受到被测试端消息）;
        //2）writerIdleTime：为写超时时间（即测试端一定时间内未向被测试端发送消息）
        //3）allIdleTime：所有类型的超时时间;
        ph.addLast(new IdleStateHandler(5,0,0));
        ph.addLast("handler", new NettyServerHandler());// 服务端业务逻辑
    }
}