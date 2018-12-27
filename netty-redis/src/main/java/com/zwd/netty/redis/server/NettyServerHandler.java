package com.zwd.netty.redis.server;


import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.redis.*;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NettyServerHandler extends ChannelDuplexHandler {

    private Map<Object,Object> stringMap = new HashMap<>();
    /*
     * 收到消息时，返回信息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        // 收到消息直接打印输出
        System.out.println("服务端接受的消息 : " + msg);
        ArrayRedisMessage message = (ArrayRedisMessage)msg;
        Object response = printAggregatedRedisResponseRequest(message);
        if (response != null) {
            FullBulkStringRedisMessage fullBulkStringRedisMessage = new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), (String) response));
            ctx.writeAndFlush(fullBulkStringRedisMessage);
        }
    }

    private Object printAggregatedRedisResponseRequest(ArrayRedisMessage message) {
        String type = ((FullBulkStringRedisMessage) message.children().get(0)).content().toString(CharsetUtil.UTF_8);
        if (type.equalsIgnoreCase("set")) {
            stringMap.put( ((FullBulkStringRedisMessage) message.children().get(1)).content().toString(CharsetUtil.UTF_8),
            ((FullBulkStringRedisMessage) message.children().get(2)).content().toString(CharsetUtil.UTF_8));
            return "ok";
        }
        if (type.equalsIgnoreCase("del")) {
            stringMap.remove(((FullBulkStringRedisMessage) message.children().get(1)).content().toString(CharsetUtil.UTF_8));
            return "ok";
        }
        if (type.equalsIgnoreCase("get")) {
            Object o = stringMap.get(((FullBulkStringRedisMessage) message.children().get(1)).content().toString(CharsetUtil.UTF_8));
            if (o == null) {
                return "(null)";
            }else {
                return o;
            }
        }
        return "(null)";
    }



    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
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