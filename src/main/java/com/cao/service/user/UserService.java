package com.cao.service.user;

import com.cao.pojo.Role;
import com.cao.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author admin_cg
 * @data 2020/10/8 14:07
 */
public interface UserService {
    public User login(String userCode, String password);

    // 修改当前用户密码
    public boolean updatePwd(int id, String password);

    public int getUserCount(String username, int userRole);

    public List<User> getUserList(String username, int userRole, int currentPageNo, int pageSize);

    public boolean addUser(User user);

    public boolean checkExist(String userCode);

    public User getUserById(int id);

    public boolean updateUser(User user);

    public boolean delUserById(int id);
}
