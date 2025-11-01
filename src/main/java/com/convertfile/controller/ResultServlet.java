package com.convertfile.controller;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.convertfile.model.FileInfo;
import com.convertfile.service.FileService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/result")
public class ResultServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final FileService fileService = new FileService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json; charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        JSONObject jsonResponse = new JSONObject();

        try{
            String fileID = request.getParameter("fileID");
            
            //1 file
            if(fileID != null && !fileID.isEmpty()){
                FileInfo file = fileService.getFileByID(fileID);
                if(file != null){
                    jsonResponse.put("statusProgress", "SUCCESS");
                    jsonResponse.put("fileId", file.getFileId());
                    jsonResponse.put("originalName", file.getOriginalName());
                    jsonResponse.put("savedName", file.getSavedName());
                    jsonResponse.put("fileSizeMB", String.format("%.2f", file.getFileSize()));
                    jsonResponse.put("status", file.getStatus());
                    jsonResponse.put("filePath", file.getFilePath());
                    jsonResponse.put("message", "Get File Completed!");
                } else {
                    jsonResponse.put("statusProgress", "FAILED");
                    jsonResponse.put("message", "File not found!");
                }
            //Nhieu file
            } else {
                List<FileInfo> files = fileService.getAllFiles();

                JSONArray jsonArray = new JSONArray();

                for(FileInfo file : files){
                    JSONObject fileJson = new JSONObject();

                    fileJson.put("fileId", file.getFileId());
                    fileJson.put("orginalName", file.getOriginalName());
                    fileJson.put("savedName", file.getSavedName());
                    fileJson.put("fileSizeMB", String.format("%.2f", file.getFileSize()));
                    fileJson.put("status", file.getStatus());
                    fileJson.put("filePath", file.getFilePath());

                    jsonArray.put(fileJson);

                }
                
                jsonResponse.put("statusProgress", "SUCCESS");
                jsonResponse.put("files", jsonArray);
                jsonResponse.put("message", "Get All Files Completed!");
            }
            response.setStatus(HttpServletResponse.SC_OK);
            
        } catch (Exception e) {
            jsonResponse.put("statusProgress", "FAILED");
            jsonResponse.put("message", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            response.getWriter().write(jsonResponse.toString());
        }
    }
}
