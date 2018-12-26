package com.zwd.netty.rpc.client;

import com.zwd.netty.rpc.client.service.HelloService;

/**
 * @author zwd
 * @date 2018/12/25 19:54
 * @Email stephen.zwd@gmail.com
 */
public class StartApp {

    public static String providerName = "HelloService#hello#";

    public static void main(String[] args) throws InterruptedException {

        NettyClient client = new NettyClient();

        HelloService service =  (HelloService)client.getBean(HelloService.class);

        String content = service.hello("are you ok?");

        System.out.println(content);


    }
}
