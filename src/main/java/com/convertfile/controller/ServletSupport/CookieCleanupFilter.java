package com.convertfile.controller.ServletSupport;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter("/*")
public class CookieCleanupFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("g_state".equals(cookie.getName())) {
                    Cookie newCookie = new Cookie("g_state", null);
                    newCookie.setMaxAge(0);
                    newCookie.setPath("/");
                    res.addCookie(newCookie);
                }
            }
        }
        
        chain.doFilter(request, response);
    }
}