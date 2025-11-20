package com.convertfile.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.convertfile.model.dao.JobDAO;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/jobs")
public class JobServlet extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set response type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            HttpSession session = request.getSession();
            Object userIdObj = session.getAttribute("userId");
            long userId = 0;
            if (userIdObj != null) {
                userId = (Long) userIdObj;
            }

            // Get jobs from DAO
            List<Map<String, Object>> listJobs;
            if (userId == 0) {
                // Guest: Get files from session
                @SuppressWarnings("unchecked")
                List<String> guestFileIds = (List<String>) session.getAttribute("guestFile_ids");
                listJobs = JobDAO.getJobsByFileIds(guestFileIds);
            } else {
                // User: Get all files by user ID
                listJobs = JobDAO.getAllJobs(userId);
            }

            // Convert to JSON and write to response
            objectMapper.writeValue(response.getWriter(), listJobs);

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\": \"error\", \"message\": \"" + e.getMessage() + "\"}");
        }
    }
}
