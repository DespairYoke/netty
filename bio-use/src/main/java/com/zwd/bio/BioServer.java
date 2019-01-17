package com.zwd.bio;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * @author zwd
 * @date 2019/1/17 11:14
 * @Email stephen.zwd@gmail.com
 */
public class BioServer {

    private final int port = 8889;



    private ServerSocket serverSocket;

    public static void main(String[] args) throws IOException {
        new BioServer();
    }

    public BioServer() throws IOException {

        init();
        listener();
    }

    private void listener() throws IOException {

        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(new ServerHander(socket)).start();
        }
    }

    private void init() throws IOException {

            serverSocket = new ServerSocket(port);
    }
}

class ServerHander implements Runnable {


    private StringBuffer stringBuffer = new StringBuffer();

    private Socket socket;

    private PrintWriter out;
    public ServerHander(Socket socket) {
        this.socket = socket;
    }

    public void run() {

        try {
            System.out.println(Thread.currentThread()+"====");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(),true);

            String tmp;
            while (true) {
                if ((tmp=bufferedReader.readLine())!=null) {
                    System.out.println("本次读取内容为："+ tmp);
                    out.println("已收到你的消息");
                }else {
                    break;
                }
            }

        } catch (IOException e) {


        }finally {
            try {
                out.close();
                socket.close();

                out=null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
