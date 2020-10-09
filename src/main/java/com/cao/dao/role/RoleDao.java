package com.cao.dao.role;

import com.cao.pojo.Role;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author admin_cg
 * @data 2020/10/9 10:42
 */
public interface RoleDao {
    public List<Role> getRoleList(Connection connection) throws SQLException;
}
