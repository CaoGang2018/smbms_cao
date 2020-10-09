package com.cao.servlet.user;

import com.cao.pojo.User;
import com.cao.service.user.UserService;
import com.cao.service.user.UserServiceImpl;
import com.cao.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author admin_cg
 * @data 2020/10/8 14:30
 */
public class LoginServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.out.println("LoginServlet--start...");
        String userCode = req.getParameter("userCode");
        String userPassword = req.getParameter("userPassword");

        UserServiceImpl userService = new UserServiceImpl();
        User user = userService.login(userCode, userPassword);

        if(user != null){
            if (userPassword.equals(user.getUserPassword())){
                req.getSession().setAttribute(Constants.USER_SESSION, user);
                resp.sendRedirect("jsp/frame.jsp");
            } else {
                req.setAttribute("error", "密码不正确");
                req.getRequestDispatcher("login.jsp").forward(req, resp);
            }
        }else {
            req.setAttribute("error", "用户名不正确");
            req.getRequestDispatcher("login.jsp").forward(req, resp);

        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
