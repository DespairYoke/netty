### 服务端如何得知客户端的存活状态？
服务端想知道客户端是否挂掉，就必须间断获得客户端相关的信息，这种每个一段时间，客户端就会向服务端发送存活状态的机制，我们成为心跳。当然如果客户端正常向服务端发送请求时，并不需要再执行心跳请求，只在空闲时间发送心跳请求，这样也就提高了发送效率。
### 客户端如何得知服务端的存活状态？
显然我们不可能再像客户端向服务端发送心跳请求一样，发送给客户端，毕竟在一个高并发的状态，服务端还要做这种多余的操作，就显得很力不从心了。那如何得知服务端挂掉了呢？netty为我们提供了一个接口`ChannelInboundHandler`，并实现这个方法`channelInactive`，当服务端挂掉，客户端会异步调用这个方法。

### 参数介绍
netty为我们提供IdleStateHandler 类，可以实现对三种心跳的检测，分别是readerIdleTime、writerIdleTime和allIdleTime。 
* readerIdleTime：为读超时时间（即测试端一定时间内未接受到被测试端消息）; 
* writerIdleTime：为写超时时间（即测试端一定时间内未向被测试端发送消息） 
* allIdleTime：所有类型的超时时间;

### 服务端搭建
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
#### ChannelInitializer
```java
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
```
#### ChannelInboundHandlerAdapter
```java
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /** 空闲次数 */
    private int idle_count =1;
    /** 发送次数 */
    private int count = 1;

    /**
     * 超时处理
     * 如果5秒没有接受客户端的心跳，就触发;
     * 如果超过两次，则直接关闭;
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object obj) throws Exception {
        if (obj instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) obj;
            if (IdleState.READER_IDLE.equals(event.state())) {  //如果读通道处于空闲状态，说明没有接收到心跳命令
                System.out.println("已经5秒没有接收到客户端的信息了");
                if (idle_count > 2) {
                    System.out.println("关闭这个不活跃的channel");
                    ctx.channel().close();
                }
                idle_count++;
            }
        } else {
            super.userEventTriggered(ctx, obj);
        }
    }

    /**
     * 业务逻辑处理
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("第"+count+"次"+",服务端接受的消息:"+msg);
        String message = (String) msg;
        if ("hb_request".equals(message)) {  //如果是心跳命令，则发送给客户端;否则什么都不做
            ctx.write("服务端成功收到心跳信息");
            ctx.flush();
        }
        count++;
    }

    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
```
### 客户端
#### Client
```java
public class NettyClient {

    public static String host = "127.0.0.1";  //ip地址
    public static int port = 6789;          //端口
    /// 通过nio方式来接收连接和处理连接
    private static EventLoopGroup group = new NioEventLoopGroup();
    private static Bootstrap b = new Bootstrap();
    private static Channel ch;

    /**
     * Netty创建全部都是实现自AbstractBootstrap。
     * 客户端的是Bootstrap，服务端的则是    ServerBootstrap。
     **/
    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("客户端成功启动...");
        b.group(group);
        b.channel(NioSocketChannel.class);
        b.handler(new NettyClientFilter());
        // 连接服务端
        ch = b.connect(host, port).sync().channel();
        star();
    }

    public static void star() throws IOException{
        String str="Hello Netty";
        ch.writeAndFlush(str+ "\r\n");
        System.out.println("客户端发送数据:"+str);
    }

}
```
#### ChannelInitializer
```java
public class NettyClientFilter extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline ph = ch.pipeline();
        /*
         * 解码和编码，应和服务端一致
         * */
//        ph.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        ph.addLast("decoder", new StringDecoder());
        ph.addLast("encoder", new StringEncoder());
        ph.addLast( new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS));
        ph.addLast("handler", new NettyClientHandler()); //客户端的逻辑
    }
}
```
#### ChannelInboundHandlerAdapter
```java
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    /** 客户端请求的心跳命令  */
    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("hb_request",
            CharsetUtil.UTF_8));

    /** 空闲次数 */
    private int idle_count = 1;

    /** 发送次数 */
    private int count = 1;

    /**循环次数 */
    private int fcount = 1;

    /**
     * 建立连接时
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("建立连接时："+new Date());
        ctx.fireChannelActive();
    }

    /**
     * 关闭连接时
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("关闭连接时："+new Date());
    }

    /**
     * 心跳请求处理
     * 每4秒发送一次心跳请求;
     *
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object obj) throws Exception {
        System.out.println("循环请求的时间："+new Date()+"，次数"+fcount);
        if (obj instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) obj;
            if (IdleState.WRITER_IDLE.equals(event.state())) {  //如果写通道处于空闲状态,就发送心跳命令
                if(idle_count <= 3){   //设置发送次数
                    idle_count++;
                    ctx.channel().writeAndFlush(HEARTBEAT_SEQUENCE.duplicate());
                }else{
                    System.out.println("不再发送心跳请求了！");
                }
                fcount++;
            }
        }
    }

    /**
     * 业务逻辑处理
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("第"+count+"次"+",客户端接受的消息:"+msg);
        count++;
    }
}
```
### 最终效果
#### 服务端
![image.png](https://upload-images.jianshu.io/upload_images/15204062-982e50babe46cd02.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
#### 客户端
![image.png](https://upload-images.jianshu.io/upload_images/15204062-f99e2119bde07ac1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
项目地址https://github.com/DespairYoke/netty/tree/master/netty-heartbeat
