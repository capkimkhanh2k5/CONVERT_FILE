package com.convertfile.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/result")
public class ResultServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // TODO: Sau này sẽ lấy thông tin từ JobDAO / CSDL thật
        // Tạm thời mô phỏng dữ liệu
        request.setAttribute("status", "Hoàn thành");
        request.setAttribute("totalFiles", 3);
        request.setAttribute("completedFiles", 3);
        request.setAttribute("downloadLink", "downloads/output.zip");

        request.getRequestDispatcher("result.jsp").forward(request, response);
    }
}
