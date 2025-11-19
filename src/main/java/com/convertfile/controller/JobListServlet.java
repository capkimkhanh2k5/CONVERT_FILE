package com.convertfile.controller;

import com.convertfile.model.dao.JobDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/jobs")
public class JobListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Lấy UserID từ Session
        HttpSession session = request.getSession();
        Object userIdObj = session.getAttribute("userId");
        long userId = 0;
        if (userIdObj != null) {
            userId = (Long) userIdObj;
        }

        // 2. Gọi hàm DAO (Đã sửa có tham số)
        List<Map<String, Object>> jobList = JobDAO.getAllJobs(userId);
        
        request.setAttribute("LIST_JOB", jobList);
        request.getRequestDispatcher("jobs.jsp").forward(request, response);
    }
}