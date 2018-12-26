一提到netty实现rpc，就让人情不自禁的想起Dubbo服务间的调用。下面就根据问题实现rpc。

#### dubbo服务调用时，service是没有实现类的，service层调用的方法如何被处理？
- service没有实现类，真正的实现类在调用的服务端，service调用方法的时候其实是被代理对象调用的。
```java
        NettyClient client = new NettyClient();

        HelloService service =  (HelloService)client.getBean(HelloService.class);

        service.hello("are you ok?");
```
```java
    public Object getBean(final Class<?> serviceClass) {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{serviceClass}, (proxy, method, args) -> {
                    if (client == null) {
                        initClient();
                    }
                    // 设置参数
                    client.setPara( method.getName() +"#"+ args[0]);
                    return executor.submit(client).get();
                });
    }
```
#### 服务端如何得知是哪个类调用的哪个方法？
- service在调用代理类时，能获取被代理的接口名和方法名，再与参数进行加密处理等；在服务端则进行解密操作，根据内容进行service判断。
```java
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{serviceClass}, (proxy, method, args) -> {
                    if (client == null) {
                        initClient();
                    }
                    // 设置参数，用于调用方法判断
                    client.setPara( method.getName() +"#"+ args[0]);
                    return executor.submit(client).get();
                });
```
```java
 if (msg.toString().startsWith(StringConfig.providerName)) {

            HelloService helloService = new HelloServiceImpl();
            String substring = msg.toString().substring(msg.toString().lastIndexOf("#"));
            String hello = helloService.hello(substring);
            ctx.writeAndFlush(hello);
            System.out.println("服务端发送成功！");
        }
```
#### 客户端如何获取返回的结果？
- 当调用服务端时，我们让线程进入休眠状态，当我们客户端接受到服务端的信息后，进行线程唤醒，返回获取后的结果。
```java
 public synchronized void channelRead(ChannelHandlerContext ctx, Object msg){
        result = msg.toString();
        System.out.println("收到服务发送的消息："+result);
        notify();

    }
    @Override
    public synchronized Object call() throws InterruptedException {
        context.writeAndFlush(para);
        wait();
        return result;
    }
```
项目地址 https://github.com/DespairYoke/netty/tree/master/netty-rpc