<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<%
    // LOGIC KIỂM TRA CÓ CẦN RELOAD KHÔNG
    boolean needReload = false;
    List<Map<String, Object>> checkList = (List<Map<String, Object>>) request.getAttribute("LIST_JOB");
    
    if (checkList != null) {
        for (Map<String, Object> job : checkList) {
            String s = (String) job.get("status");
            // Nếu còn cái nào chưa xong (WAITING hoặc PROCESSING) -> Bật cờ Reload
            if ("WAITING".equals(s) || "PROCESSING".equals(s)) {
                needReload = true;
                break;
            }
        }
    }
%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Process Status</title>
    
    <% if (needReload) { %>
        <meta http-equiv="refresh" content="2">
    <% } %>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background: linear-gradient(135deg, #e8eaf6 0%, #c5e1f5 50%, #f5f5dc 100%);
            min-height: 100vh; position: relative; overflow-x: hidden;
        }
        /* Họa tiết nền */
        .shape { position: absolute; border-radius: 50%; opacity: 0.6; z-index: 0; }
        .shape-1 { width: 80px; height: 80px; background: white; top: 20px; right: 10%; }
        .shape-2 { width: 60px; height: 60px; background: #fff9c4; top: 20px; right: 5%; }
        .shape-3 { width: 150px; height: 150px; background: #7c4dff; bottom: -50px; left: -50px; opacity: 0.4; }

        /* Glass Card */
        .glass-card {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border-radius: 20px;
            padding: 30px;
            box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
            border: 1px solid rgba(255, 255, 255, 0.18);
            margin-top: 50px;
            position: relative; z-index: 2;
        }
        
        /* Header */
        header { background: white; padding: 20px 0; box-shadow: 0 2px 10px rgba(0,0,0,0.05); position: relative; z-index: 10; }
        nav { display: flex; justify-content: space-between; align-items: center; max-width: 1200px; margin: 0 auto; padding: 0 20px; }
        .logo { font-size: 28px; font-weight: bold; text-decoration: none; }
        .logo span:first-child { color: #5e35b1; }
        .logo span:last-child { color: #1e88e5; }

        .btn-new-task {
            background: #5e35b1; color: white; border: none; padding: 10px 25px;
            border-radius: 50px; text-decoration: none; font-weight: 600;
            box-shadow: 0 4px 15px rgba(94, 53, 177, 0.3);
            transition: transform 0.2s;
        }
        .btn-new-task:hover { transform: translateY(-2px); color: white; }
    </style>
</head>
<body>
    <div class="shape shape-1"></div>
    <div class="shape shape-2"></div>
    <div class="shape shape-3"></div>

    <header>
        <nav>
            <a href="#" class="logo"><span>File</span><span>Convert</span></a>
            <a href="upload.jsp" class="btn-new-task">Create New Task</a>
        </nav>
    </header>

    <div class="container">
        <div class="glass-card">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h3 class="fw-bold text-secondary">Your <span style="color: #5e35b1;">Processing Tasks</span></h3>
                <% if (needReload) { %>
                    <span class="badge bg-light text-dark border spinner-grow-sm">
                        <span class="spinner-border spinner-border-sm text-primary" role="status"></span> Live Updating...
                    </span>
                <% } else { %>
                     <span class="badge bg-success">All Completed</span>
                <% } %>
            </div>

            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>File Name</th>
                        <th>Type</th>
                        <th>Status</th>
                        <th style="width: 35%;">Progress</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        if (checkList != null && !checkList.isEmpty()) {
                            for (Map<String, Object> job : checkList) {
                                String status = (String) job.get("status");
                                int progress = (Integer) job.get("progress");
                                
                                String badgeClass = "bg-secondary";
                                if ("WAITING".equals(status)) badgeClass = "bg-warning text-dark";
                                if ("PROCESSING".equals(status)) badgeClass = "bg-primary";
                                if ("COMPLETED".equals(status)) badgeClass = "bg-success";

                                String barStyle = (progress == 100) ? "bg-success" : "progress-bar-striped progress-bar-animated";
                    %>
                    <tr>
                        <td class="fw-bold text-secondary"><%= job.get("name") %></td>
                        <td><span class="badge bg-light text-dark border"><%= job.get("type") %></span></td>
                        <td><span class="badge <%= badgeClass %>"><%= status %></span></td>
                        <td>
                            <div class="d-flex align-items-center">
                                <div class="progress flex-grow-1" style="height: 8px; border-radius: 10px;">
                                    <div class="progress-bar <%= barStyle %>" style="width: <%= progress %>%; background-color: #5e35b1;"></div>
                                </div>
                                <span class="ms-2 small fw-bold text-muted"><%= progress %>%</span>
                            </div>
                        </td>
                    </tr>
                    <%      }
                        } else { 
                    %>
                        <tr><td colspan="4" class="text-center py-5 text-muted">No tasks found.</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>