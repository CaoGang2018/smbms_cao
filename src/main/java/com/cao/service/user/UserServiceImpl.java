package com.cao.service.user;

import com.cao.dao.BaseDao;
import com.cao.dao.user.UserDao;
import com.cao.dao.user.UserDaoImpl;
import com.cao.pojo.User;
import com.cao.util.Constants;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author admin_cg
 * @data 2020/10/8 14:07
 */
public class UserServiceImpl implements UserService {

    private UserDao userDao;

    public UserServiceImpl() {
        this.userDao = new UserDaoImpl();
    }

    @Override
    public User login(String userCode, String password) {
        Connection connection = null;
        User user = null;

        try {
            connection = BaseDao.getConnection();
            user = userDao.getLoginUser(connection, userCode);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return user;
    }

    @Override
    public boolean updatePwd(int id, String password) {
        Connection connection = null;
        connection = BaseDao.getConnection();
        boolean flag = false;

        try {
            if (userDao.updatePwd(connection, id, password) > 0){
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }

    @Override
    public int getUserCount(String username, int userRole) {
        Connection connection = null;
        int count = 0;

        try {
            connection = BaseDao.getConnection();
            count = userDao.getUserCount(connection, username, userRole);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return count;
    }

    @Override
    public List<User> getUserList(String username, int userRole, int currentPageNo, int pageSize) {
        Connection connection = null;
        List<User> userList = new ArrayList<>();

        System.out.println("queryUserName --->" + username);
        System.out.println("queryUserRole --->" + userRole);
        System.out.println("currentPageNo --->" + currentPageNo);
        System.out.println("pageSize --->" + pageSize);

        try {
            connection = BaseDao.getConnection();
            userList = userDao.getUserList(connection, username, userRole, currentPageNo, pageSize);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection, null, null);
        }
        return userList;
    }

    @Override
    public boolean addUser(User user) {
        Connection connection = null;
        boolean flag = false;

        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);
            int updateRow = userDao.addUser(connection, user);
            connection.commit();

            if (updateRow > 0){
                flag = true;
                System.out.println("add success");
            } else {
                System.out.println("add failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                System.out.println("rollback=========");
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }

    @Override
    public boolean checkExist(String userCode) {
        Connection connection = null;
        boolean flag = false;

        try {
            connection = BaseDao.getConnection();
            if (userDao.checkExist(connection, userCode) > 0){
                flag = true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Test
    public void test(){
        UserServiceImpl userService = new UserServiceImpl();
        // User admin = userService.login("admin", "1234567");
        boolean admin = userService.checkExist("admin");
        System.out.println(admin);
    }
}
