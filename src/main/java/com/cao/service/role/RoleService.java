package com.cao.service.role;

import com.cao.pojo.Role;

import java.sql.Connection;
import java.util.List;

/**
 * @author admin_cg
 * @data 2020/10/9 10:44
 */
public interface RoleService {
    public List<Role> getRoleList();
}
