package com.zwd.netty.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;


import java.lang.reflect.Proxy;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zwd
 * @date 2018/12/25 19:56
 * @Email stephen.zwd@gmail.com
 */
public class NettyClient {

    private static ExecutorService executor = Executors
            .newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private  static  NettyClientHandler ch;
    public Object getBean(final Class<?> serviceClass, final String providerName) {

        return Proxy.newProxyInstance(serviceClass.getClassLoader(),serviceClass.getInterfaces(),
                (proxy, method,args) -> {
            if (ch == null) {
                initClient();
            }
            ch.setPara(providerName+args[0]);
            return executor.submit(ch).get();
                });
    }

    private void initClient() {
        ch = new NettyClientHandler();
        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new NettyClientFilter(ch));
    }


}
