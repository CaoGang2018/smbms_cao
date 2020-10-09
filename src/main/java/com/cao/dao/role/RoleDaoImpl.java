package com.cao.dao.role;

import com.cao.dao.BaseDao;
import com.cao.pojo.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author admin_cg
 * @data 2020/10/9 10:43
 */
public class RoleDaoImpl implements RoleDao {
    @Override
    public List<Role> getRoleList(Connection connection) throws SQLException {

        PreparedStatement pstm = null;
        ResultSet resultSet = null;
        List<Role> roleList = new ArrayList<>();

        if (connection != null){
            String sql = "select * from smbms_role";
            Object[] params = {};
            resultSet = BaseDao.execute(connection, sql, params, resultSet, pstm);

            while (resultSet.next()){
                Role _role = new Role();
                _role.setId(resultSet.getInt("id"));
                _role.setRoleCode(resultSet.getString("roleCode"));
                _role.setRoleName(resultSet.getString("roleName"));
                roleList.add(_role);
            }
            BaseDao.closeResource(null, pstm, resultSet);
        }
        return roleList;
    }
}
