package com.cao.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author admin_cg
 * @data 2020/10/7 21:06
 */
public class CharacterEncodingFilter implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletRequest.setCharacterEncoding("utf-8");
        servletResponse.setCharacterEncoding("utf-8");

        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {

    }
}
