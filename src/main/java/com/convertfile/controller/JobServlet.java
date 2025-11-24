package com.convertfile.controller;

import java.io.IOException;
import java.util.ArrayList;
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

        System.out.println("📋 JobServlet: doGet called");
        System.out.println("🔍 Request session ID from cookie: " + request.getRequestedSessionId());
        
        // Set response type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            HttpSession session = request.getSession(false);
            System.out.println("🔍 Session object: " + (session != null ? "EXISTS" : "NULL"));
            if (session != null) {
                System.out.println("🔑 Active session ID: " + session.getId());
            }
            Object userIdObj = (session != null) ? session.getAttribute("userId") : null;
            long userId = 0;
            if (userIdObj != null) {
                userId = (Long) userIdObj;
            }
            
            System.out.println("👤 User ID: " + userId);

            // Get jobs from DAO
            List<Map<String, Object>> listJobs = new ArrayList<>();
            
            if (userId == 0) {
                // Guest: Get files from session
                if (session != null) {
                    System.out.println("🔍 Guest session ID: " + session.getId());
                    @SuppressWarnings("unchecked")
                    List<String> guestFileIds = (List<String>) session.getAttribute("guestFile_ids");
                    System.out.println("👻 Guest file IDs from session: " + guestFileIds);
                    System.out.println("👻 Total guest files: " + (guestFileIds != null ? guestFileIds.size() : 0));
                    
                    if (guestFileIds != null && !guestFileIds.isEmpty()) {
                        listJobs = JobDAO.getJobsByFileIds(guestFileIds);
                        System.out.println("📊 Retrieved " + listJobs.size() + " jobs from DB");
                    }
                } else {
                    System.out.println("⚠️ No session found for guest");
                }
            } else {
                // User: Get all files by user ID
                System.out.println("🔍 Fetching jobs for user: " + userId);
                listJobs = JobDAO.getAllJobs(userId);
            }
            
            System.out.println("✅ Returning " + listJobs.size() + " jobs");

            // Convert to JSON and write to response
            objectMapper.writeValue(response.getWriter(), listJobs);

        } catch (Exception e) {
            System.err.println("❌ JobServlet error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\": \"error\", \"message\": \"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }
}
