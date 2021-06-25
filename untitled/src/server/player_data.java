package server;

import java.util.LinkedList;
import java.util.Queue;

public class player_data {
    public Queue<Data> Match_queue =new LinkedList<Data>();
    public int[][][] board = new int[15][15][15];
    static class Data{
        //棋盘id
        int id;
        //执子颜色
        int color;
        //用户名
        public String name;
        //服务器线程的回调
        public CallBack callBack;
        public Data(String name,CallBack callBack) {
            this.name = name;
            this.callBack=callBack;
        }
    }
}

