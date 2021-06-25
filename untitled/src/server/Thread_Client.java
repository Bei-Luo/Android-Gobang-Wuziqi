package server;


import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Thread_Client extends Thread implements CallBack {
    private BufferedReader bufferedReader;
    private PrintStream printStream;
    public Socket socket;
    private final Mysql mysql;
    private final player_data data;
    private String user_name;
    private final CallBack callBack;
    private player_data.Data basic_self;
    private player_data.Data basic_rival;

    public Thread_Client(Socket socket, Mysql mysql, player_data data) {
        this.socket = socket;
        this.mysql = mysql;
        this.data = data;
        callBack = this;
    }

    @Override
    public void run() {
        super.run();
        try {
            Get_set(socket);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("异常1");
        }
        System.out.println("断开连接");
    }

    private void Get_set(Socket socket) {
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printStream = new PrintStream(socket.getOutputStream(), true);
            boolean flag = true;
            while (flag) {
                String string = bufferedReader.readLine();
                if ("".equals(string) || string == null) {
                    flag = false;
                } else {
                    //客户端发回关闭信息
                    if ("end".equals(string)) {
                        flag = false;
                    } else {
                        //信息处理函数
                        System.out.println(string);
                        Message_processing(JSONObject.parseObject(string));
                    }
                }
            }

        } catch (IOException e) {
            //客户端掉线也可以导致异常
            System.out.println("异常2");
        }
    }

    /**
     * 函数路由器
     *
     * @param object
     */
    private void Message_processing(JSONObject object) {
        String string;
        switch (object.getString("function")) {
            case "login" -> {
                string = login(object.getString("username"), object.getString("password"));
                printStream.println(string);
            }
            case "registered" -> {
                string = registered(object.getString("username"), object.getString("password"));
                printStream.println(string);
            }
            case "match" -> jinrupipei();
            case "xiaqi" -> {
                int x = object.getInteger("x");
                int y = object.getInteger("y");
                basic_rival.callBack.xiaqi(x, y);
            }
        }

    }

    /**
     * 注册处理函数
     *
     * @param username
     * @param password
     * @return
     */
    private String registered(String username, String password) {
        JSONObject object = new JSONObject();
        boolean user_exist = false;
        try {
            Statement stmt = mysql.conn.createStatement();
            String sql = "SELECT username, password FROM login";
            ResultSet rs = stmt.executeQuery(sql);
            //遍历数据集
            while (rs.next()) {
                //判断用户名,判断密码
                if (username.equals(rs.getString(1))) {
                    user_exist = true;
                    break;
                }
            }
            if (!user_exist) {
                sql = "INSERT INTO login (username, `password`) VALUES ('" + username + "', '" + password + "')";
                stmt.executeUpdate(sql);
            }
            rs.close();
            stmt.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        if (user_exist)
            //用户名已经存在
            object.put("return", "false");
        else {
            //注册成功
            object.put("return", "true");
        }
        return object.toString();
    }

    /**
     * 登录处理函数
     *
     * @param username
     * @param password
     * @return
     */
    private String login(String username, String password) {
        JSONObject object = new JSONObject();
        boolean user_make = false;
        boolean pass_make = false;

        try {
            Statement stmt = mysql.conn.createStatement();
            String sql = "SELECT username, password FROM login";
            ResultSet rs = stmt.executeQuery(sql);
            //遍历数据集
            while (rs.next()) {
                //判断用户名,判断密码
                if (username.equals(rs.getString(1))) {
                    user_make = true;
                    if (password.equals(rs.getString(2))) {
                        pass_make = true;
                        break;
                    }
                }
            }
            rs.close();
            stmt.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        if (pass_make) {
            //密码正确
            object.put("return", "true");
            user_name = username;
        } else {
            object.put("return", "false");
            if (user_make)
                //密码错误
                object.put("error", "2");
            else
                //用户名不存在
                object.put("error", "1");
        }

        return object.toString();
    }

    /**
     * 进入匹配队列
     */
    private void jinrupipei() {
        player_data.Data basic = new player_data.Data(user_name, callBack);
        data.Match_queue.add(basic);
    }

    @Override
    public void jinruyouxi(player_data.Data basic_rival) {
        //初始化自己的元数据
        basic_self = new player_data.Data(user_name, callBack);
        basic_self.id = basic_rival.id;
        basic_self.color = basic_rival.color == 1 ? 2 : 1;
        this.basic_rival = basic_rival;
        //并且更新计分板
        JSONObject object = new JSONObject();
        object.put("function", "join");
        object.put("name_self", basic_self.name);
        object.put("name_rival", basic_rival.name);
        //传入棋子颜色
        object.put("color", basic_self.color);
        printStream.println(object.toString());
        System.out.println(object.toString());
    }

    @Override
    public void xiaqi(int x, int y) {

        //构建数据包 对手下棋位置|自己下棋
        JSONObject object = new JSONObject();
        object.put("x", x);
        object.put("y", y);
        object.put("function", "chess");
        data.board[basic_rival.id][x][y] = basic_self.color;
        //判断对手是否胜利
        if (is_win(data.board[basic_rival.id], x, y)) {
            gameover(basic_rival.color, x, y);
            basic_rival.callBack.gameover(basic_rival.color, x, y);
        }
        //发送数据包
        printStream.println(object.toString());
        System.out.println(object.toString());
    }

    @Override
    public void gameover(int color, int x, int y) {
        JSONObject object = new JSONObject();
        object.put("function", "gameover");
        object.put("color", color);
        object.put("x", x);
        object.put("y", y);
        printStream.println(object.toString());
    }

    public boolean is_win(int[][] board, int x, int y) {
        int color = board[x][y];
        int count = 0;
        int i, j, k;
        int xx, yy;
        int[][] mode = {{1, 0}, {0, 1}, {1, 1}, {1, -1}};
        for (i = 0; i < 4; ++i) {
            count = 1;
            for (j = -1; j <= 1; j += 2)
                for (k = 1; k < 5; ++k) {
                    xx = x + (mode[i][0] * j * k);
                    yy = y + (mode[i][1] * j * k);
                    if (xx < 0 || yy < 0 || xx > 14 || yy > 14)
                        break;
                    if (board[xx][yy] == color)
                        count++;
                    else
                        break;
                }
            if (count == 5)
                return true;
        }
        return false;
    }
}