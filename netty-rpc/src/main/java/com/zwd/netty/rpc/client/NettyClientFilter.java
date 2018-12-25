package com.zwd.netty.rpc.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;


/**
 * @author zwd
 * @date 2018/12/25 20:07
 * @Email stephen.zwd@gmail.com
 */
public class NettyClientFilter extends ChannelInitializer<SocketChannel> {

    private NettyClientHandler ch;

    public NettyClientFilter(NettyClientHandler nettyClientHandler) {
        ch = nettyClientHandler;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new StringEncoder());
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(ch);
    }
}
