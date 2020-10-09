package com.cao.dao.user;

import com.cao.dao.BaseDao;
import com.cao.pojo.Role;
import com.cao.pojo.User;
import com.mysql.cj.util.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author admin_cg
 * @data 2020/10/8 13:46
 */
public class UserDaoImpl implements UserDao {
    @Override
    public User getLoginUser(Connection connection, String userCode) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;
        String sql;
        if(connection != null){
            sql = "select * from smbms_user where userCode=?";
            Object[] params = {userCode};

            resultSet = BaseDao.execute(connection, sql, params, resultSet, preparedStatement);
            if (resultSet.next()) {
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUserCode(resultSet.getString("userCode"));
                user.setUserName(resultSet.getString("userName"));
                user.setUserPassword(resultSet.getString("userPassword"));
                user.setGender(resultSet.getInt("gender"));
                user.setBirthday(resultSet.getDate("birthday"));
                user.setPhone(resultSet.getString("phone"));
                user.setAddress(resultSet.getString("address"));
                user.setUserRole(resultSet.getInt("userRole"));
                user.setCreatedBy(resultSet.getInt("createdBy"));
                user.setCreationDate(resultSet.getDate("creationDate"));
                user.setModifyBy(resultSet.getInt("modifyBy"));
                user.setModifyDate(resultSet.getDate("modifyDate"));
            }
            BaseDao.closeResource(null, preparedStatement, resultSet);
        }
        return user;
    }

    @Override
    public int updatePwd(Connection connection, int id, String password) throws SQLException {
        PreparedStatement  pstm = null;
        int execute = 0;

        if(connection != null){
            String sql = "update smbms_user set userPassword = ? where id = ?";
            Object[] params = {password, id};
            execute = BaseDao.execute(connection, sql, params, pstm);
            BaseDao.closeResource(null, pstm, null);
        }
        return execute;
    }

    @Override
    public int getUserCount(Connection connection, String username, int userRole) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int count = 0;

        ArrayList<Object> list = new ArrayList<>();
        if (connection != null){
            StringBuffer sql = new StringBuffer();
            sql.append("select count(1) as count from smbms_user u, smbms_role r where u.userRole = r.id");

            if (!StringUtils.isNullOrEmpty(username)){
                sql.append(" and u.userName like ?");
                list.add("%" + username + "%");
            }
            if (userRole > 0){
                sql.append(" and u.userRole = ?");
                list.add(userRole);
            }
            Object[] objects = list.toArray();

            System.out.println("UserDaoImpl->getUserCount:" + sql.toString());

            rs = BaseDao.execute(connection, sql.toString(), objects, rs, pstm);

            if (rs.next()){
                count = rs.getInt("count");
            }
            BaseDao.closeResource(null, pstm, rs);
        }
        return count;
    }

    @Override
    public List<User> getUserList(Connection connection, String username, int userRole, int currentPageNo, int pageSize) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet resultSet = null;

        List<User> userList = new ArrayList<>();

        if (connection != null){
            StringBuffer sql = new StringBuffer();
            sql.append("select u.*, r.roleName as userRoleName from smbms_user u, smbms_role r where u.userRole = r" +
                    ".id");
            ArrayList<Object> list = new ArrayList<>();
            if(!StringUtils.isNullOrEmpty(username)){
                sql.append(" and u.userName like ?");
                list.add("%" + username + "%");
            }
            if (userRole > 0){
                sql.append(" and u.userRole = ?");
                list.add(userRole);
            }
            sql.append(" order by creationDate DESC limit ?, ?");
            currentPageNo = (currentPageNo-1) * pageSize;
            list.add(currentPageNo);
            list.add(pageSize);

            Object[] params = list.toArray();
            System.out.println("sql --->" + sql.toString());
            resultSet = BaseDao.execute(connection, sql.toString(), params, resultSet, pstm);
            while (resultSet.next()){
                User _user = new User();
                _user.setId(resultSet.getInt("id"));
                _user.setUserCode(resultSet.getString("userCode"));
                _user.setUserName(resultSet.getString("userName"));
                _user.setGender(resultSet.getInt("gender"));
                _user.setBirthday(resultSet.getDate("birthday"));
                _user.setPhone(resultSet.getString("phone"));
                _user.setUserRole(resultSet.getInt("userRole"));
                _user.setUserRoleName(resultSet.getString("userRoleName"));
                userList.add(_user);
            }
            BaseDao.closeResource(null, pstm, resultSet);
        }
        return userList;
    }

    @Override
    public int addUser(Connection connection, User user) throws SQLException {
        PreparedStatement pstm = null;
        int updateRow = 0;

        if (connection != null){
            String sql = "insert into smbms_user (userCode, userName, userPassword, gender, birthday, phone, address," +
                    "userRole, createdBy, creationDate,) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            Object[] params = {user.getUserCode(), user.getUserName(), user.getUserPassword(), user.getGender(),
                    user.getBirthday(), user.getPhone(), user.getAddress(), user.getUserRole(), user.getCreatedBy(),
                    user.getCreationDate()};

            updateRow = BaseDao.execute(connection, sql, params, pstm);
            BaseDao.closeResource(null, pstm, null);
        }
        return updateRow;
    }

    @Override
    public int checkExist(Connection connection, String userCode) throws SQLException {
        PreparedStatement pstm = null;
        int updateRow = 0;
        ResultSet rs = null;

        if(connection != null){
            String sql = "select userCode from smbms_user where userCode = ?";
            Object[] params = {userCode};
            rs = BaseDao.execute(connection, sql, params, rs, pstm);
            updateRow = rs.next() ? 1 : 0;
            BaseDao.closeResource(null, pstm, rs);
        }
        return updateRow;
    }
}
