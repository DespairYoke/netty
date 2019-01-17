package com.zwd.bio;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author zwd
 * @date 2019/1/17 11:34
 * @Email stephen.zwd@gmail.com
 */
public class BioClient {

    private final String ip = "127.0.0.1";

    private final int port = 8889;

    private StringBuffer stringBuffer ;

    public static void main(String[] args) throws IOException, InterruptedException {
        new BioClient();
    }

    public BioClient() throws IOException, InterruptedException {

        while (true) {
            new Thread(new HandlerClient()).start();
            Thread.sleep(2000);
        }

    }

    private void listener() throws IOException {


        PrintWriter printWriter =null;
        BufferedReader bufferedReader=null;
        Socket socket = null;

        stringBuffer = new StringBuffer();
        try {
            socket = new Socket(ip,port);
            printWriter = new PrintWriter(socket.getOutputStream(),true);
            printWriter.println("你好我是客户端");
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String tmp;
            while (true) {
                if ((tmp = bufferedReader.readLine()) !=null) {
                    System.out.println(tmp);
                }else {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {

           bufferedReader.close();
           printWriter.close();
           socket.close();
        }


    }


    class HandlerClient implements Runnable{


        public void run() {
            try {
                listener();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
