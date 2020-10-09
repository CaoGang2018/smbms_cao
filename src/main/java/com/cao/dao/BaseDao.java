package com.cao.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @author admin_cg
 * @data 2020/10/7 20:43
 */
// 操作数据库的公共类
public class BaseDao {
    private static String driver;
    private static String url;
    private static String username;
    private static String password;

    // 静态代码块，类加载的时候就初始化
    static {
        Properties properties = new Properties();
        InputStream is = BaseDao.class.getClassLoader().getResourceAsStream("db.properties");
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        driver = properties.getProperty("driver");
        url = properties.getProperty("url");
        username = properties.getProperty("username");
        password = properties.getProperty("password");
    }

    // 获取数据库连接
    public static Connection getConnection(){
        Connection connection = null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
    //查询公共类
    public static ResultSet execute(Connection connection, String sql, Object[] params, ResultSet resultSet, PreparedStatement preparedStatement) throws SQLException {
        //预编译sql直接执行
        preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i+1, params[i]);
        }
        resultSet = preparedStatement.executeQuery();
        return resultSet;
    }

    // 编写增删改
    public static int execute(Connection connection, String sql, Object[] params, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i+1, params[i]);
        }
        return preparedStatement.executeUpdate();
    }

    // 释放资源
    public static boolean closeResource(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet){
        boolean flag = true;

        if(resultSet != null){
            try {
                resultSet.close();
                resultSet = null;
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }
        if(preparedStatement != null){
            try {
                preparedStatement.close();
                preparedStatement = null;
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }
        if(connection != null){
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }
        return flag;
    }

}
