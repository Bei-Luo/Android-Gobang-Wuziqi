package server;
import java.sql.*;

public class Mysql {

    // MySQL 8.0 以上版本 - JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/TESTDB?serverTimezone=Asia/Shanghai";


    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "root";
    static final String PASS = "";

    public Connection conn;

    public Mysql() {
        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            //连接数据库
            System.out.println("连接数据库...");
            this.conn = DriverManager.getConnection(DB_URL, USER, PASS);

            //检查login表是否存在
            Statement stmt = conn.createStatement();
            String sql;
            sql = "select count(*)  from information_schema.TABLES t where t.TABLE_NAME ='login'";
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            if(rs.getInt(1)!=2){
                System.out.println("检测到login表不存在");
                sql="CREATE TABLE login(" +
                        "id INT NOT NULL AUTO_INCREMENT," +
                        "username VARCHAR(20) NOT NULL," +
                        "password VARCHAR(20) NOT NULL," +
                        "PRIMARY KEY (id))";
                stmt.executeUpdate(sql);
                System.out.println("login表创建完成");
            }
            System.out.println("检测的login表存在");
            rs.close();
            stmt.close();
            System.out.println("连接成功");
        } catch (ClassNotFoundException e) {
            // JDBC 错误
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
