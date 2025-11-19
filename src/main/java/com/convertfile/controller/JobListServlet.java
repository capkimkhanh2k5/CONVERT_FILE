package com.convertfile.controller;

import com.convertfile.model.dao.JobDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/jobs")
public class JobListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Gọi DAO lấy dữ liệu
        List<Map<String, Object>> jobList = JobDAO.getAllJobs();
        
        // 2. Đẩy dữ liệu sang JSP
        request.setAttribute("LIST_JOB", jobList);
        
        // 3. Chuyển hướng về trang giao diện
        request.getRequestDispatcher("jobs.jsp").forward(request, response);
    }
}