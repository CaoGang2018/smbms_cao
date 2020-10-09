package com.cao.servlet.user;


import com.alibaba.fastjson.JSONArray;
import com.cao.pojo.Role;
import com.cao.pojo.User;
import com.cao.service.role.RoleServiceImpl;
import com.cao.service.user.UserService;
import com.cao.service.user.UserServiceImpl;
import com.cao.util.Constants;
import com.cao.util.PageSupport;
import com.mysql.cj.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author admin_cg
 * @data 2020/10/8 16:01
 */
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        System.out.println(method);
        if (method != null && method.equals("savepwd")){
            this.updatePwd(req, resp);
        } else if (method != null && method.equals("pwdmodify")){
            this.pwdModify(req, resp);
        } else if (method != null && method.equals("query")){
            this.query(req, resp);
        } else if (method != null && method.equals("add")){
            this.addUser(req, resp);
        } else if (method != null && method.equals("getrolelist")){
            this.getRoleList(req, resp);
        } else if (method != null && method.equals("ucexist")){
            this.checkExist(req, resp);
        }
    }

    private void checkExist(HttpServletRequest req, HttpServletResponse resp) {
        UserServiceImpl userService = new UserServiceImpl();
        Map<String, String> map = new HashMap<>();

        String userCode = req.getParameter("userCode");
        if (userService.checkExist(userCode)){
            map.put("userCode", "exist");
        } else {
            map.put("userCode", "notExist");
        }
        try {
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            writer.write(JSONArray.toJSONString(map));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getRoleList(HttpServletRequest req, HttpServletResponse resp) {
        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();

        try {
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            //writer.write(JSONArray.toJSONString(roleList));
            System.out.println(JSONArray.toJSONString(roleList));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private void addUser(HttpServletRequest req, HttpServletResponse resp) {
        String userCode = req.getParameter("userCode");
        String userName = req.getParameter("userName");
        String userPassword = req.getParameter("userPassword");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");

        User user = new User();
        user.setUserCode(userCode);
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        user.setGender(Integer.parseInt(gender));
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(Integer.parseInt(userRole));
        user.setCreationDate(new Date());
        user.setCreatedBy(((User) req.getSession().getAttribute(Constants.USER_SESSION)).getId());

        UserServiceImpl userService = new UserServiceImpl();
        if (userService.addUser(user)){
            try {
                resp.sendRedirect(req.getContextPath() + "/jsp/user.do?method=query");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                req.getRequestDispatcher("useradd.jsp").forward(req, resp);
            } catch (ServletException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void query(HttpServletRequest req, HttpServletResponse resp){
        String queryUserName = req.getParameter("queryUserName");
        String temp = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");
        int queryUserRole = 0;

        UserServiceImpl userService = new UserServiceImpl();

        int pageSize = 5;
        int currentPageNo = 1;


        if (queryUserName == null){
            queryUserName = "";
        }
        if (temp != null && !temp.equals("")){
            queryUserRole = Integer.parseInt(temp);
        }
        if (pageIndex != null){
            try {
                currentPageNo = Integer.parseInt(pageIndex);
            } catch (Exception e) {
                try {
                    resp.sendRedirect("error.jsp");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }

        int totalCount = userService.getUserCount(queryUserName, queryUserRole);

        PageSupport pageSupport = new PageSupport();
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setPageSize(pageSize);
        pageSupport.setTotalCount(totalCount);

        int totalPageCount = pageSupport.getTotalPageCount();

        if (totalPageCount < 1){
            currentPageNo = 1;
        }else if (currentPageNo > totalPageCount){
            currentPageNo = totalPageCount;
        }

        List<User> userList = userService.getUserList(queryUserName, queryUserRole, currentPageNo, pageSize);
        req.setAttribute("userList", userList);

        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();
        req.setAttribute("roleList", roleList);
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("currentPageNo", currentPageNo);
        req.setAttribute("totalPageCount", totalPageCount);
        req.setAttribute("queryUserName", queryUserName);
        req.setAttribute("queryUserRole", queryUserRole);

        try {
            req.getRequestDispatcher("userlist.jsp").forward(req, resp);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }

    }
    public void updatePwd(HttpServletRequest req, HttpServletResponse resp){
        Object attribute = req.getSession().getAttribute(Constants.USER_SESSION);
        String newpassword = req.getParameter("newpassword");
        boolean flag = false;

        if (attribute != null && !StringUtils.isNullOrEmpty(newpassword)){
            UserService userService = new UserServiceImpl();
            flag = userService.updatePwd(((User) attribute).getId(), newpassword);
            if(flag){
                req.setAttribute("message", "修改密码成功， 请退出重新登录");
                req.getSession().removeAttribute(Constants.USER_SESSION);
            } else{
                req.setAttribute("message", "修改密码失败");
            }
        } else {
            req.setAttribute("message", "新密码有问题");
        }
        try {
            req.getRequestDispatcher("pwdmodify.jsp").forward(req, resp);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }

    public void pwdModify(HttpServletRequest req, HttpServletResponse resp){
        Object attribute = req.getSession().getAttribute(Constants.USER_SESSION);
        String oldpassword = req.getParameter("oldpassword");

        Map<String, String> map = new HashMap<>();
        if(attribute == null){
            map.put("result", "sessionerror");
        } else if (StringUtils.isNullOrEmpty(oldpassword)){
            map.put("result", "error");
        } else {
            String userPassword = ((User) attribute).getUserPassword();
            if (oldpassword.equals(userPassword)){
                map.put("result", "true");
            } else {
                map.put("result", "false");
            }
        }

        try {
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            writer.write(JSONArray.toJSONString(map));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
