package com.cao.dao.user;

import com.cao.pojo.Role;
import com.cao.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author admin_cg
 * @data 2020/10/8 13:45
 */
public interface UserDao {

    //得到登录用户
    public User getLoginUser(Connection connection, String userCode) throws SQLException;

    // 修改当前用户密码
    public int updatePwd(Connection connection, int id, String password) throws SQLException;

    // 查询用户总数
    public int getUserCount(Connection connection, String username, int userRole) throws SQLException;

    public List<User> getUserList(Connection connection, String username, int userRole, int currentPageNo,
                                  int pageSize)  throws SQLException;

    public int addUser(Connection connection, User user) throws SQLException;

    public User getUserById(Connection connection, int id) throws SQLException;

    public int updateUser(Connection connection, User user) throws SQLException;

    public int delUserById(Connection connection, int id) throws SQLException;
}
