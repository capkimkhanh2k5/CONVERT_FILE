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

        try {
            // Defensive check: getCookies() might throw exception if cookie header is
            // malformed
            // and server is strict (though usually it returns null or valid cookies)
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("g_state".equals(cookie.getName())) {
                        // Found the problematic cookie, kill it with fire
                        Cookie newCookie = new Cookie("g_state", "");
                        newCookie.setMaxAge(0);
                        newCookie.setPath("/");
                        newCookie.setHttpOnly(false);
                        // Add Expires attribute for older browsers/legacy handling if needed,
                        // but Servlet API doesn't support it directly on Cookie object easily without
                        // custom header.
                        // MaxAge(0) should suffice for most.
                        res.addCookie(newCookie);
                    }
                }
            }
        } catch (Exception e) {
            // If parsing fails, we can't do much but ignore it and let the request proceed
            System.out.println("CookieCleanupFilter: Error processing cookies: " + e.getMessage());
        }

        chain.doFilter(request, response);
    }
}