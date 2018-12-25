package com.zwd.netty.pack.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;

/**
 * @author zwd
 * @date 2018/12/24 15:30
 * @Email stephen.zwd@gmail.com
 */
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
        star(3);
    }

    public static void star(int i) throws IOException{
        String str="春江潮水连海平，海上明月共潮生。"
                +"  滟滟随波千万里，何处春江无月明! "
                +"  江流宛转绕芳甸，月照花林皆似霰;"
                +"  空里流霜不觉飞，汀上白沙看不见。"
                +"  江天一色无纤尘，皎皎空中孤月轮。"
                +"  江畔何人初见月？江月何年初照人？"
                +"  人生代代无穷已，江月年年望相似。"
                +"  不知江月待何人，但见长江送流水。"
                +"  白云一片去悠悠，青枫浦上不胜愁。"
                +"  谁家今夜扁舟子？何处相思明月楼？"
                +"  可怜楼上月徘徊，应照离人妆镜台。"
                +"  玉户帘中卷不去，捣衣砧上拂还来。"
                +"  此时相望不相闻，愿逐月华流照君。"
                +"  鸿雁长飞光不度，鱼龙潜跃水成文。"
                +"  昨夜闲潭梦落花，可怜春半不还家。"
                +"  江水流春去欲尽，江潭落月复西斜。"
                +"  斜月沉沉藏海雾，碣石潇湘无限路。"
                +"  不知乘月几人归，落月摇情满江树。"
                +" 噫吁嚱，危乎高哉！蜀道之难，难于上青天！蚕丛及鱼凫，开国何茫然！尔来四万八千岁，不与秦塞通人烟。"
                +" 西当太白有鸟道，可以横绝峨眉巅。地崩山摧壮士死，然后天梯石栈相钩连。上有六龙回日之高标，下有冲波逆折之回川。"
                +" 黄鹤之飞尚不得过，猿猱欲度愁攀援。青泥何盘盘，百步九折萦岩峦。扪参历井仰胁息，以手抚膺坐长叹。"
                +" 问君西游何时还？畏途巉岩不可攀。但见悲鸟号古木，雄飞雌从绕林间。又闻子规啼夜月，愁空山。"
                +" 蜀道之难，难于上青天，使人听此凋朱颜！连峰去天不盈尺，枯松倒挂倚绝壁。飞湍瀑流争喧豗，砯崖转石万壑雷。"
                +" 其险也如此，嗟尔远道之人胡为乎来哉！剑阁峥嵘而崔嵬，一夫当关，万夫莫开。"
                +" 所守或匪亲，化为狼与豺。朝避猛虎，夕避长蛇；磨牙吮血，杀人如麻。锦城虽云乐，不如早还家。"
                +" 蜀道之难，难于上青天，侧身西望长咨嗟！";
        if(i==1){
            for(int j=0;j<100;j++){
                str="Hello Netty";
                ch.writeAndFlush(str);
            }
        }else if(i==2){
            str+=str;
            ch.writeAndFlush(str);
        }else if(i==3){
            //System.getProperty("line.separator") 结束标记
            byte [] bt=(str+System.getProperty("line.separator")).getBytes();
            ByteBuf message = Unpooled.buffer(bt.length);
            message.writeBytes(bt);
            ch.writeAndFlush(message);
        }


        System.out.println("客户端发送数据:"+str+",发送数据的长度:"+str.length());
    }

}