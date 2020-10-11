package com.cao.service.role;

import com.alibaba.fastjson.JSONArray;
import com.cao.dao.BaseDao;
import com.cao.dao.role.RoleDao;
import com.cao.dao.role.RoleDaoImpl;
import com.cao.pojo.Role;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author admin_cg
 * @data 2020/10/9 10:44
 */
public class RoleServiceImpl implements RoleService {
    private RoleDao roleDao;

    public RoleServiceImpl() {
        this.roleDao = new RoleDaoImpl();
    }

    @Override
    public List<Role> getRoleList() {
        Connection connection = null;
        List<Role> roleList = null;


        try {
            connection = BaseDao.getConnection();
            roleList = roleDao.getRoleList(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection, null, null);
        }
        return roleList;
    }

    @Test
    public void test(){
        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();
        System.out.println(JSONArray.toJSONString(roleList));
        for (Role role : roleList) {
            System.out.println(role.getRoleName());
        }
    }

}
