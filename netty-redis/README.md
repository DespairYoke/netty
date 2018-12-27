netty内置有封装好的reids供我们来使用，我们只需对netty进行客户端和服务端的数据处理，再加一封装就能实现简单的redis功能，这里我只封装了string类型的支持，其他原理一样。
### 先看客户端如何对数据进行处理？
- 首先我们从命令行的输入下手
```java 
BufferedReader in = new BufferedReader(new InputStreamReader((System.in)));
            for (;;) {
                final String input = in.readLine();
                final String line = input != null? input.trim() : null;
                if (line == null || "quit".equalsIgnoreCase(line)) {
                    ch.close().sync();
                    break;
                }else if (line.isEmpty()) {
                    continue;
                }
                lastWriteFuture = ch.writeAndFlush(line);
                lastWriteFuture.addListener(new GenericFutureListener<ChannelFuture>() {

                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            System.err.print("write failed: ");
                            future.cause().printStackTrace(System.err);
                        }
                    }
                });
            }
```
- 上面只是把数据写入缓存，我们必须在数据发出前对数据进行处理。这里我们使用`ChannelDuplexHandler`
```java
public class RedisClientHandler extends ChannelDuplexHandler{

    //数据发出前调用此方法
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        String[] commands = ((String)msg).split("\\s+");
        List<RedisMessage> children = new ArrayList<RedisMessage>(commands.length);
        for (String cmdString : commands) {
            children.add(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), cmdString)));
            }
        RedisMessage request = new ArrayRedisMessage(children);
        ctx.write(request, promise);
        }
}
```
可以看出在数据发出前，进行了截取并封装成RedisMessage
- 使用Redis加密和解密
```java
                      protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline p = socketChannel.pipeline();
                            p.addLast(new RedisDecoder());
                            p.addLast(new RedisBulkStringAggregator());
                            p.addLast(new RedisArrayAggregator());
                            p.addLast(new RedisEncoder());
                            p.addLast(new RedisClientHandler());
                        }
```
- 数据返回进行处理
```java
    private static void printAggregatedRedisResponse(RedisMessage msg) {
        if (msg instanceof SimpleStringRedisMessage) {
            System.out.println(((SimpleStringRedisMessage) msg).content());
        } else if (msg instanceof ErrorRedisMessage) {
        System.out.println(((ErrorRedisMessage) msg).content());
        } else if (msg instanceof IntegerRedisMessage) {
            System.out.println(((IntegerRedisMessage) msg).value());
        } else if (msg instanceof FullBulkStringRedisMessage) {
            System.out.println(getString((FullBulkStringRedisMessage) msg));
        } else if (msg instanceof ArrayRedisMessage) {
            for (RedisMessage child : ((ArrayRedisMessage) msg).children()) {
                printAggregatedRedisResponse(child);
            }
        } else {
            throw new CodecException("unknown message type: " + msg);
        }
    }
```
### 再看下服务端如何对数据进行处理？
- 首先我们定义一个`Map`用于存放数据
```java
 private Map<Object,Object> stringMap = new HashMap<>();
```
- 首先服务端的数据接收还是和原来不变，使用`channelRead`
```java
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
```
- 数据类型判断
```java
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
```
项目地址 https://github.com/DespairYoke/netty/tree/master/netty-redis