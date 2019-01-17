### BIO
* [概述](#description)
* [服务端](#service)
* [客户端](#client)
* [bio的优点](#good)
* [bio的缺点](#bad)
#### 概述
<div id = "description"></div>
Java BIO (blocking I/O)： 同步并阻塞，服务器实现模式为一个连接一个线程，即客户端有连接请求时服务器端就需要启动一个线程进行处理，如果这个连接不做任何事情会造成不必要的线程开销，当然可以通过线程池机制改善。
![bio.png](https://upload-images.jianshu.io/upload_images/15204062-96520346a1a3ac9c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
当一个客户端请求服务端，服务端接受到一个请求后会创建一个socket并开启一个线程处理这个socket，再处理的同时，客户端必须等待服务端的响应。

<div id = "service"></div>
#### 服务端
代码演示：
```java
     while (true) {
            Socket socket = serverSocket.accept();
            new Thread(new ServerHander(socket)).start();
        }
```
这里accept方法会阻塞，当有客户端发起请求才会往下执行，进行数据读取，读取完毕通过socket中的outStream返回给客户端处理后的结果。
```java
            while (true) {
                if ((tmp=bufferedReader.readLine())!=null) {
                    System.out.println("本次读取内容为："+ tmp);
                    out.println("已收到你的消息");
                }else {
                    break;
                }
            }
```
<div id = "client"></div>
### 客户端
前面说过客户端发出消息后会一直阻塞等待服务端的响应，所以客户端必须有一个阻塞方法，下面来看下客户端的阻塞方法。
```java
            while (true) {
                if ((tmp = bufferedReader.readLine()) !=null) {
                    System.out.println(tmp);
                }else {
                    break;
                }
            }
```
bufferedReader.readLine()在服务端没有返回信息前一直阻塞,当有数据进入时，会向下执行。
<div id = "good"></div>
#### nio的优点
* 1.模型简单
* 2.编码简单
<div id = "bad"></div>
#### nio的缺点
* 1.每次客户端来都必须开启一个线程做处理。
* 2.客户端线程必须被阻塞，浪费大量资源。
* 3.线程之间大量上下文的切换。