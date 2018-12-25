package com.zwd.netty.rpc.config;

import com.zwd.netty.rpc.client.NettyClient;
import com.zwd.netty.rpc.service.HelloService;
import com.zwd.netty.rpc.service.HelloServiceImpl;

/**
 * @author zwd
 * @date 2018/12/25 19:54
 * @Email stephen.zwd@gmail.com
 */
public class ClientBootstrap {

    public static String providerName = "HelloService#hello#";

    public static void main(String[] args) throws InterruptedException {

        NettyClient consumer = new NettyClient();

        HelloService service = (HelloService) consumer.getBean(HelloService.class,providerName);
        while (true) {

            System.out.println(service.hello("are you ok?"));
            Thread.sleep(1000);
        }


    }
}
