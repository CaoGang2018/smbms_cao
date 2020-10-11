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
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
        } else if (method != null && method.equals("deluser")){
            this.delUser(req, resp);
        } else if (method != null && method.equals("view")){
            this.getUserById(req, resp, "userview.jsp");
        } else if (method != null && method.equals("modify")){
            this.getUserById(req, resp, "usermodify.jsp");
        } else if (method != null && method.equals("modifyexe")){
            this.modifyUser(req, resp);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private void modifyUser(HttpServletRequest req, HttpServletResponse resp) {
        String id = req.getParameter("id");
        String userName = req.getParameter("userName");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");

        User user = new User();
        user.setId(Integer.parseInt(id));
        user.setUserName(userName);
        user.setGender(Integer.parseInt(gender));
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(Integer.parseInt(userRole));
        user.setModifyBy(((User) req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        user.setModifyDate(new Date());

        UserServiceImpl userService = new UserServiceImpl();

        try {
            if (userService.updateUser(user)){
                resp.sendRedirect(req.getContextPath() + "/jsp/user.do?method=query");
            } else {
                req.getRequestDispatcher("usermodify.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getUserById(HttpServletRequest req, HttpServletResponse resp, String url) {
        int id = Integer.parseInt(req.getParameter("uid"));
        UserServiceImpl userService = new UserServiceImpl();
        User user = userService.getUserById(id);
        req.setAttribute("user", user);
        try {
            req.getRequestDispatcher(url).forward(req, resp);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }

    private void delUser(HttpServletRequest req, HttpServletResponse resp) {
        String uid = req.getParameter("uid");
        int id = Integer.parseInt(uid);
        UserServiceImpl userService = new UserServiceImpl();

        Map<String, String> map = new HashMap<>();
        if (userService.getUserById(id) != null){
            if (userService.delUserById(id)){
                map.put("delResult", "true");
            } else {
                map.put("delResult", "false");
            }
        } else {
            map.put("delResult", "notexist");
        }
        try {
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            writer.write(JSONArray.toJSONString(map));
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
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
            writer.write(JSONArray.toJSONString(roleList));
            System.out.println(JSONArray.toJSONString(roleList));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> addUserHelper(HttpServletRequest req){
        String uploadPath = this.getServletContext().getRealPath("/statics/images");
        File uploadFile = new File(uploadPath);
        if (!uploadFile.exists()){
            uploadFile.mkdir();
        }

        // 临时文件路径
        String tmpPath = this.getServletContext().getRealPath("/statics/temp_images");
        File file = new File(tmpPath);
        if (!file.exists()){
            file.mkdir();
        }

        DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
        diskFileItemFactory.setSizeThreshold(1024*1024); // 缓冲区大小
        diskFileItemFactory.setRepository(file);

        // 2 获取ServletFileUpload
        ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);

        // 监听文件上传进度
        servletFileUpload.setProgressListener(new ProgressListener() {
            @Override
            public void update(long l, long l1, int i) {
                System.out.println("总大小：" + l1 + " 已上传：" + l);
            }
        });

        // 处理乱码
        servletFileUpload.setHeaderEncoding("UTF-8");
        servletFileUpload.setFileSizeMax(1024*1024*10);
        servletFileUpload.setSizeMax(1024*1024*10);

        Map<String, String> userMap = new HashMap<>();


        try {
            List<FileItem> fileItems = servletFileUpload.parseRequest(req);
            for (FileItem fileItem : fileItems) {
                if (fileItem.isFormField()){
                    String fieldName = fileItem.getFieldName();
                    String value = fileItem.getString("UTF-8");
                    userMap.put(fieldName, value);
                    System.out.println(fieldName + " : " + value);
                } else {
                    String name = fileItem.getName();
                    System.out.println("上传的文件名" + name);
                    if (name == null || name.trim().equals("")){
                        continue;
                    }
                    //String fileName = name.substring(name.lastIndexOf("/") + 1);
                    String fileExtName = name.substring(name.lastIndexOf("."));

                    String uuidPath = UUID.randomUUID().toString();

                    String realPath = uploadPath + "\\" + uuidPath;
                    File realPathFile = new File(realPath);
                    if (!realPathFile.exists()){
                        realPathFile.mkdir();
                    }

                    String fileP = realPath + "\\" + userMap.get("userCode") + fileExtName;
                    String fileP1 = uuidPath + "\\" + userMap.get("userCode") + fileExtName;
                    InputStream inputStream = fileItem.getInputStream();
                    FileOutputStream fos = new FileOutputStream(fileP);
                    userMap.put(fileItem.getFieldName(), fileP1);

                    System.out.println("images" + fileItem.getFieldName());

                    byte[] buffer = new byte[1024 * 1024];
                    int len = 0;
                    while ((len = inputStream.read(buffer)) > 0){
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                    inputStream.close();

                    fileItem.delete();
                }
            }
        } catch (FileUploadException | IOException e) {
            e.printStackTrace();
        }
        return userMap;
    }
    private void addUser(HttpServletRequest req, HttpServletResponse resp) {
        /*String userCode = req.getParameter("userCode");
        String userName = req.getParameter("userName");
        System.out.println("userName" + userName);
        String userPassword = req.getParameter("userPassword");
        String gender = req.getParameter("gender");
        System.out.println("gender" + gender);
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");
        System.out.println("userRole" + userRole);*/

        Map<String, String> userMap = addUserHelper(req);
        System.out.println(userMap.keySet().toArray()[0]);

        User user = new User();
        user.setUserCode(userMap.get("userCode"));
        user.setUserName(userMap.get("userName"));
        user.setUserPassword(userMap.get("userPassword"));
        user.setGender(Integer.valueOf(userMap.get("gender")));
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(userMap.get("birthday")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setPhone(userMap.get("phone"));
        user.setAddress(userMap.get("address"));
        user.setUserRole(Integer.valueOf(userMap.get("userRole")));
        user.setCreationDate(new Date());
        user.setCreatedBy(((User) req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        user.setIdPicPath(userMap.get("idPicPath"));
        user.setWorkPicPath(userMap.get("workPicPath"));

        System.out.println("=====================");
        System.out.println(userMap.get("workPicPath"));
        System.out.println("=====================");

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
