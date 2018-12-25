package com.zwd.netty.rpc.service;

/**
 * @author zwd
 * @date 2018/12/25 19:15
 * @Email stephen.zwd@gmail.com
 */
public class HelloServiceImpl implements HelloService {
    public String hello(String msg) {
        return msg!=null?msg+"netty":"hello";
    }
}
