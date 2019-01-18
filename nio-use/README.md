### NIO编程    
    
* [网络IO的介绍](#network)

* [nio的概述](description)

* [channel通道](#channel)

* [buffer缓冲区](#buffer)

* [selector选择器](#selector)

* [nio读取文件](#file)

* [nio实例](#example)


#### 网络IO
<div id="network"></div>
讲网络IO前，我们先对同步、异步、阻塞、非阻塞

#### 同步与异步
同步和异步关注的是消息通信机制 (synchronous communication/ asynchronous communication)。所谓同步，就是在发出一个*调用*时，在没有得到结果之前，该*调用*就不返回。但是一旦调用返回，就得到返回值了。换句话说，就是由*调用者*主动等待这个*调用*的结果。而异步则是相反，*调用*在发出之后，这个调用就直接返回了，所以没有返回结果。换句话说，当一个异步过程调用发出后，调用者不会立刻得到结果。而是在*调用*发出后，*被调用者*通过状态、通知来通知调用者，或通过回调函数处理这个调用。

典型的异步编程模型比如Node.js

举个通俗的例子：你打电话问书店老板有没有《分布式系统》这本书，如果是同步通信机制，书店老板会说，你稍等，"我查一下"，然后开始查啊查，等查好了（可能是5秒，也可能是一天）告诉你结果（返回结果）。而异步通信机制，书店老板直接告诉你我查一下啊，查好了打电话给你，然后直接挂电话了（不返回结果）。然后查好了，他会主动打电话给你。在这里老板通过"回电"这种方式来回调。

#### 阻塞与非阻塞
阻塞和非阻塞关注的是程序在等待调用结果（消息，返回值）时的状态.阻塞调用是指调用结果返回之前，当前线程会被挂起。调用线程只有在得到结果之后才会返回。非阻塞调用指在不能立刻得到结果之前，该调用不会阻塞当前线程。

还是上面的例子，你打电话问书店老板有没有《分布式系统》这本书，你如果是阻塞式调用，你会一直把自己"挂起"，直到得到这本书有没有的结果，如果是非阻塞式调用，你不管老板有没有告诉你，你自己先一边去玩了， 当然你也要偶尔过几分钟check一下老板有没有返回结果。在这里阻塞与非阻塞与是否同步异步无关。跟老板通过什么方式回答你结果无关。
#### 网络IO分为五大模型
出自《UNIX网络编程》，I/O模型一共有阻塞式I/O，非阻塞式I/O，I/O复用(select/poll/epoll)，信号驱动式I/O和异步I/O。这篇文章讲的是I/O复用。
- blocking IO - 阻塞IO
- nonblocking IO - 非阻塞IO
- IO multiplexing - IO多路复用
- signal driven IO - 信号驱动IO
- asynchronous IO - 异步IO
#### nio的概述
<div id="description"></div>
 nio是jdk1.4提出的新特性，nio全名为non-blocking IO(非阻塞IO)
Java NIO 由以下几个核心部分组成： 
- Channels
- Buffers
- Selectors

#### 通道
<div id="channel"></div>
　通道：类似于流，但是可以异步读写数据（流只能同步读写），通道是双向的，（流是单向的），通道的数据总是要先读到一个buffer 或者 从一个buffer写入，即通道与buffer进行数据交互。这样的优点就是我们可以在读取的时候回退，对数据的操作更加灵活。

![image.png](https://upload-images.jianshu.io/upload_images/15204062-9b83f73b613429dc.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
通道类型：
- FileChannel：从文件中读写数据。　　
- DatagramChannel：能通过UDP读写网络中的数据。　　
- SocketChannel：能通过TCP读写网络中的数据。　　
- ServerSocketChannel：可以监听新进来的TCP连接，像Web服务器那样。对每一个新进来的连接都会创建一个SocketChannel。　　
- FileChannel比较特殊，它可以与通道进行数据交互， 不能切换到非阻塞模式，套接字通道可以切换到非阻塞模式；

#### 缓冲区
<div id="buffer"></div>
缓冲区 - 本质上是一块可以存储数据的内存，被封装成了buffer对象而已！

##### 1、缓冲区类型：
- ByteBuffer　　
- MappedByteBuffer　　
- CharBuffer　　
- DoubleBuffer　　
- FloatBuffer　　
- IntBuffer　　
- LongBuffer　　
- ShortBuffer　　
##### 2、常用方法：
- allocate() - 分配一块缓冲区　　
- put() -  向缓冲区写数据
- get() - 向缓冲区读数据　　
- filp() - 将缓冲区从写模式切换到读模式　　
- clear() - 从读模式切换到写模式，不会清空数据，但后续写数据会覆盖原来的数据，即使有部分数据没有读，也会被遗忘；　　
- compact() - 从读数据切换到写模式，数据不会被清空，会将所有未读的数据copy到缓冲区头部，后续写数据不会覆盖，而是在这些数据之后写数据
- mark() - 对position做出标记，配合reset使用
-  reset() - 将position置为标记值
　　　　
##### 3、缓冲区的一些属性：
- capacity - 缓冲区大小，无论是读模式还是写模式，此属性值不会变；
- position - 写数据时，position表示当前写的位置，每写一个数据，会向下移动一个数据单元，初始为0；最大为capacity - 1，切换到读模式时，position会被置为0，表示当前读的位置
- limit - 写模式下，limit 相当于capacity 表示最多可以写多少数据，切换到读模式时，limit 等于原先的position，表示最多可以读多少数据。
#### 选择器
<div id="selector"></div>
选择器：相当于一个观察者，用来监听通道感兴趣的事件，一个选择器可以绑定多个通道。

![image.png](https://upload-images.jianshu.io/upload_images/15204062-cee15bf85fd22d79.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
通道向选择器注册时，需要指定感兴趣的事件，选择器支持以下事件：
SelectionKey.OP_CONNECT
SelectionKey.OP_ACCEPT
SelectionKey.OP_READ
SelectionKey.OP_WRITE　　
如果你对不止一种事件感兴趣，那么可以用“位或”操作符将常量连接起来，如下：
 int interestSet = SelectionKey.OP_READ | SelectionKey.OP_WRITE;

要使用Selector，得向Selector注册Channel，然后调用它的select()方法。这个方法会一直阻塞到某个注册的通道有事件就绪。一旦这个方法返回，线程就可以处理这些事件，事件的例子有如新连接进来，数据接收等。
#### nio读取文件
<div id="file"></div>

```java
RandomAccessFile aFile = new RandomAccessFile("data/nio-data.txt", "rw");  
FileChannel inChannel = aFile.getChannel();  
  
ByteBuffer buf = ByteBuffer.allocate(48);  
  
int bytesRead = inChannel.read(buf);  
while (bytesRead != -1) {  
  
System.out.println("Read " + bytesRead);  
buf.flip();  
  
while(buf.hasRemaining()){  
System.out.print((char) buf.get());  
}  
  
buf.clear();  
bytesRead = inChannel.read(buf);  
}  
aFile.close();  
```
### nio的简单使用
<div id="example"></div>

#### 服务端

```java
 /*
         *启动服务器端，配置为非阻塞，绑定端口，注册accept事件
         *ACCEPT事件：当服务端收到客户端连接请求时，触发该事件
         */
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(port));

        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
```
#### 客户端

```java
  try {
            /*
             * 客户端向服务器端发起建立连接请求
             */
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(SERVER);
            /*
             * 轮询监听客户端上注册事件的发生
             */
            while (true) {
                selector.select();
                Set<SelectionKey> keySet = selector.selectedKeys();
                for(final SelectionKey key : keySet){
                    handle(key);
                }
                keySet.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
```