package com.zwd.netty.rpc.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;


/**
 * @author zwd
 * @date 2018/12/25 19:40
 * @Email stephen.zwd@gmail.com
 */
public class NettyServerFilter extends ChannelInitializer<SocketChannel>{
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline p = socketChannel.pipeline();

        p.addLast(new StringDecoder());
        p.addLast(new StringEncoder());
        p.addLast(new NettyServerHandler());

    }
}
