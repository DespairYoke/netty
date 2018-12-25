通过学习了解到netty可以像tomcat一样搭建一个httpServer服务器，这里简单的实现一下。
首先，我们介绍一下httpRequest
### 认识Http请求

在动手写Netty框架之前，我们先要了解http请求的组成，如下图：

![image.png](https://upload-images.jianshu.io/upload_images/15204062-49ee4ed2281e90d5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
1.  HTTP Request 第一部分是包含的头信息
2.  HttpContent 里面包含的是数据，可以后续有多个 HttpContent 部分
3.  LastHttpContent 标记是 HTTP request 的结束，同时可能包含头的尾部信息
4.  完整的 HTTP request，由1，2，3组成

![image.png](https://upload-images.jianshu.io/upload_images/15204062-eca9b401a086ca4d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
1.  HTTP response 第一部分是包含的头信息
2.  HttpContent 里面包含的是数据，可以后续有多个 HttpContent 部分
3.  LastHttpContent 标记是 HTTP response 的结束，同时可能包含头的尾部信息
4.  完整的 HTTP response，由1，2，3组成
从request的介绍我们可以看出来，一次http请求并不是通过一次对话完成的，他中间可能有很次的连接。netty每一次对话都会建立一个channel，并且一个ChannelInboundHandler一般是不会同时去处理多个Channel的。
如何在一个Channel里面处理一次完整的Http请求？这就要用到我们上图提到的FullHttpRequest，我们只需要在使用netty处理channel的时候，只处理消息是FullHttpRequest的Channel，这样我们就能在一个ChannelHandler中处理一个完整的Http请求了。
```java
ph.addLast("aggregator", new HttpObjectAggregator(10*1024*1024));//把单个http请求转为FullHttpReuest或FullHttpResponse
```
### 开始搭建
#### Server
```java
public class NettyServer {
    private static final int port = 6789; //设置服务端端口
    private static  EventLoopGroup group = new NioEventLoopGroup();   // 通过nio方式来接收连接和处理连接
    private static  ServerBootstrap b = new ServerBootstrap();

    /**
     * Netty创建全部都是实现自AbstractBootstrap。
     * 客户端的是Bootstrap，服务端的则是    ServerBootstrap。
     **/
    public static void main(String[] args) throws InterruptedException {
        try {
            b.group(group);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new NettyServerFilter()); //设置过滤器
            // 服务器绑定端口监听
            ChannelFuture f = b.bind(port).sync();
            System.out.println("服务端启动成功...");
            // 监听服务器关闭监听
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully(); ////关闭EventLoopGroup，释放掉所有资源包括创建的线程
        }
    }
}
```
### ChannelInitializer
```java
public class NettyServerFilter extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline ph = ch.pipeline();
        ph.addLast("encoder",new HttpResponseEncoder());
        ph.addLast("decoder",new HttpRequestDecoder());
        ph.addLast("aggregator", new HttpObjectAggregator(10*1024*1024));//把单个http请求转为FullHttpReuest或FullHttpResponse
        ph.addLast("handler", new NettyServerHandler());// 服务端业务逻辑
    }
}
```
#### ChannelInboundHandlerAdapter
```java
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private String result="";
    /*
     * 收到消息时，返回信息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(! (msg instanceof FullHttpRequest)){
            result="未知请求!";
            send(ctx,result,HttpResponseStatus.BAD_REQUEST);
            return;
        }
        FullHttpRequest httpRequest = (FullHttpRequest)msg;
        try{
            String path=httpRequest.uri();          //获取路径
            String body = getBody(httpRequest);     //获取参数
            HttpMethod method=httpRequest.method();//获取请求方法
            //如果不是这个路径，就直接返回错误
            if(!"/test".equalsIgnoreCase(path)){
                result="非法请求!";
                send(ctx,result,HttpResponseStatus.BAD_REQUEST);
                return;
            }
            System.out.println("接收到:"+method+" 请求");
            //如果是GET请求
            if(HttpMethod.GET.equals(method)){
                //接受到的消息，做业务逻辑处理...
                System.out.println("body:"+body);
                result="GET请求";
                send(ctx,result,HttpResponseStatus.OK);
                return;
            }
            //如果是POST请求
            if(HttpMethod.POST.equals(method)){
                //接受到的消息，做业务逻辑处理...
                System.out.println("body:"+body);
                result="POST请求";
                send(ctx,result,HttpResponseStatus.OK);
                return;
            }

            //如果是PUT请求
            if(HttpMethod.PUT.equals(method)){
                //接受到的消息，做业务逻辑处理...
                System.out.println("body:"+body);
                result="PUT请求";
                send(ctx,result,HttpResponseStatus.OK);
                return;
            }
            //如果是DELETE请求
            if(HttpMethod.DELETE.equals(method)){
                //接受到的消息，做业务逻辑处理...
                System.out.println("body:"+body);
                result="DELETE请求";
                send(ctx,result,HttpResponseStatus.OK);
                return;
            }
        }catch(Exception e){
            System.out.println("处理请求失败!");
            e.printStackTrace();
        }finally{
            //释放请求
            httpRequest.release();
        }
    }
    /**
     * 获取body参数
     * @param request
     * @return
     */
    private String getBody(FullHttpRequest request){
        ByteBuf buf = request.content();
        return buf.toString(CharsetUtil.UTF_8);
    }

    /**
     * 发送的返回值
     * @param ctx     返回
     * @param context 消息
     * @param status 状态
     */
    private void send(ChannelHandlerContext ctx, String context,HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(context, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /*
     * 建立连接时，返回消息
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接的客户端地址:" + ctx.channel().remoteAddress());
        ctx.writeAndFlush("客户端"+ InetAddress.getLocalHost().getHostName() + "成功与服务端建立连接！ ");
        super.channelActive(ctx);
    }

}
```
### 最终效果
![image.png](https://upload-images.jianshu.io/upload_images/15204062-6fb77133ff87187e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![](https://upload-images.jianshu.io/upload_images/15204062-bd38ea70ef79d50b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
项目地址 https://github.com/DespairYoke/netty/tree/master/netty-httpserver
