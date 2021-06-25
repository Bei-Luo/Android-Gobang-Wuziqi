package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server {
    private ServerSocket serverSocket;
    private Socket socket;
    private static final int PORT = 8886;
    private final Mysql mysql;
    private final player_data data;


    public Server() {
        data = new player_data();
        mysql = new Mysql();
        System.out.println("服务器开始运行");
        try {
            serverSocket = new ServerSocket(PORT); // 建立服务端，ip为本机ip,端口为8886
            Match_Thread match_thread = new Match_Thread(data);
            match_thread.start();
            while (true) {
                socket = serverSocket.accept(); // 监听客户端的连接，一旦有客户端连接，则会返回客户端对应的accept
                Thread_Client thread_Client = new Thread_Client(socket, mysql, data);   // 启动线程
                thread_Client.start();
                System.out.println("已连接！");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
    }

    class Match_Thread extends Thread {
        final long timeInterval = 1000;
        private final player_data data;
        boolean[] flag = new boolean[15];

        public Match_Thread(player_data data) {
            this.data = data;
            for (int i = 0; i < 15; ++i)
                flag[i] = false;
        }

        @Override
        public void run() {
            super.run();
            while (true) {
                if (data.Match_queue.size() >= 2) {
                    Random r = new Random();
                    player_data.Data basic_1 = data.Match_queue.remove();
                    player_data.Data basic_2 = data.Match_queue.remove();
                    for (int i = 0; i < 15; ++i)
                        if (!flag[i]) {
                            basic_1.id = i;
                            basic_2.id = i;
                            break;
                        }
                    if (r.nextInt(2)==1) {
                        basic_1.color = 1;
                        basic_2.color = 2;
                    } else {
                        basic_1.color = 2;
                        basic_2.color = 1;
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            basic_1.callBack.jinruyouxi(basic_2);
                        }
                    }).start();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            basic_2.callBack.jinruyouxi(basic_1);
                        }
                    }).start();
                }
                try {
                    Thread.sleep(timeInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}