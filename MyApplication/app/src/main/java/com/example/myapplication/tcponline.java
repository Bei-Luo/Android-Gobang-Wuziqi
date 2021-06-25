package com.example.myapplication;

import android.app.Application;
import android.os.Looper;

import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.online_game.rival_CallBack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class tcponline extends Application {
    private Socket socket;
    private String url = "192.168.55.2";
    private int port = 8886;
    private BufferedReader bufferedReader;
    private PrintStream printStream;
    boolean flag = false;
    public rival_CallBack callback;
    private boolean return_flag=true;
    /**
     * 打开套接字 并且启动in线程
     *
     * @return true=连接成功 false=连接失败
     */
    public boolean Connect_start() {
        flag = false;
        Connect_Thread connect_thread = new Connect_Thread();
        connect_thread.start();
        try {
            connect_thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 关闭套接字
     */
    public void Connect_clear() {
        try {
            socket.close();
            socket = null;
        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    /**
     * 发送消息函数
     *
     * @param Msg
     * @return
     */
    public String Msg_return(String Msg) {
        StringBuffer getString = new StringBuffer();
        Msg_return_Thread Msg_Thread = new Msg_return_Thread(Msg, getString);
        Msg_Thread.start();
        try {
            Msg_Thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return getString.toString();
    }

    public void Msg_send(String Msg) {
        Msg_send Msg_Thread = new Msg_send(Msg);
        Msg_Thread.start();
        if(return_flag) {
            Return_Thread return_thread = new Return_Thread();
            return_thread.start();
        }
        return_flag=false;
    }

    /**
     * 启动线程
     */
    class Connect_Thread extends Thread {
        @Override
        public void run() {
            super.run();
            if (socket == null) {
                try {
                    socket = new Socket(url, port);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    printStream = new PrintStream(socket.getOutputStream(), true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                flag = true;
            }
        }
    }

    /**
     * 消息线程
     */
    class Msg_return_Thread extends Thread {
        private String Msg;
        private StringBuffer getString;

        Msg_return_Thread(String Msg, StringBuffer getstring) {
            this.Msg = Msg;
            this.getString = getstring;
        }

        @Override
        public void run() {
            super.run();
            try {
                printStream.println(Msg);
                getString.append(bufferedReader.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Msg_send extends Thread {
        private String Msg;

        Msg_send(String Msg) {
            this.Msg = Msg;
        }

        @Override
        public void run() {
            super.run();
            printStream.println(Msg);
        }
    }

    class Return_Thread extends Thread {
        public boolean flag=true;
        @Override
        public void run() {
            super.run();
            Looper.prepare();
            while (flag) {
                try {
                    final String string = bufferedReader.readLine();
                    JSONObject object = JSONObject.parseObject(string);
                    switch (object.getString("function")) {
                        case "join":
                            callback.init_chess(object);
                            break;
                        case "chess":
                            callback.rival_chess(object.getInteger("x"), object.getInteger("y"));
                            break;
                        case "gameover":
                            flag=false;
                            callback.gameover(object);
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Looper.loop();
        }
    }
}
