package com.convertfile.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import org.json.JSONArray;
import org.json.JSONObject;

import com.convertfile.bo.FileBO;
import com.convertfile.bo.UserBO;
import com.convertfile.model.EnumStatus;
import com.convertfile.model.FileInfo;
import com.convertfile.service.FileService;

@WebServlet("/home")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 5, // 5 MB
    maxFileSize = 1024 * 1024 * 50,      // 50 MB
    maxRequestSize = 1024 * 1024 * 100   // 100 MB
)
public class HomeServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;

    private final FileService fileService = new FileService();
    private final UserBO userBO = new UserBO();
    private final FileBO fileBO = new FileBO();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json; charset=UTF-8");
        JSONObject jsonResponse = new JSONObject();
        
        HttpSession session = request.getSession(false);

        // Nếu không có session, không thể là khách hoặc người dùng
        if(session == null){
            jsonResponse.put("statusProgress", "FAILED");
            jsonResponse.put("message", "No session found");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(jsonResponse.toString());
            return;
        }

        String username = (String) session.getAttribute("username");
        
        try {
            // Lấy tất cả file của user
            String[] file_ids;

            if(username != null){
                long user_id = userBO.getUserByUsername(username);
                file_ids = fileBO.getAllFile_idsByUser_id(user_id);
            } else {
                @SuppressWarnings("unchecked")
                List<String> guestFile_ids = (List<String>) session.getAttribute("guestFile_ids");
                if(guestFile_ids == null){
                    file_ids = new String[0];
                } else {
                    file_ids = guestFile_ids.toArray(new String[0]);
                }
            }

            JSONArray filesArray = new JSONArray();
            
            int totalFiles = 0;
            int completedFiles = 0;
            
            for(String file_id : file_ids) {
                FileInfo file = fileBO.getFileByID(file_id);

                if (file == null) continue;
                
                JSONObject fileJson = new JSONObject();
                fileJson.put("file_id", file.getFile_id());
                fileJson.put("original_name", file.getOriginal_name());
                fileJson.put("file_size", file.getFile_size());
                fileJson.put("current_status", file.getCurrent_status().toString());
                fileJson.put("output_format", file.getOutput_format());
                
                filesArray.put(fileJson);
                totalFiles++;
                
                if(file.getCurrent_status() == EnumStatus.FileStatus.CONVERTED) {
                    completedFiles++;
                }
            }
            
            jsonResponse.put("statusProgress", "SUCCESS");
            jsonResponse.put("files", filesArray);
            jsonResponse.put("totalFiles", totalFiles);
            jsonResponse.put("completedFiles", completedFiles);
            
            response.setStatus(HttpServletResponse.SC_OK);
            
        } catch (Exception e) {
            jsonResponse.put("statusProgress", "FAILED");
            jsonResponse.put("message", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            response.getWriter().write(jsonResponse.toString());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        
        JSONObject jsonResponse = new JSONObject();

        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        long user_id;
        boolean isGuest = false;

        if(username == null){
            user_id = 0;
            isGuest = true;
        } else {
            user_id = userBO.getUserByUsername(username);
        }


        try {
            Part filePart = request.getPart("file");
            if(filePart == null || filePart.getSize() == 0)
                throw new ServletException("No files have been uploaded!");

            String original_name = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            fileService.validateFile(original_name, filePart.getSize());

            String input_format = FileService.getFileExtension(original_name);

            String file_id = UUID.randomUUID().toString();
            String saved_name = file_id + "_" + original_name;

            // Đọc đường dẫn từ web.xml -> đường dẫn và ghi file
            String inputDir = getServletContext().getInitParameter("UPLOAD_INPUT_PATH");
            String uploadPath = getServletContext().getInitParameter("UPLOAD_OUTPUT_PATH");

            //Đường dẫn tuyệt đối
            String appPath = getServletContext().getRealPath("");

            inputDir = appPath + File.separator + inputDir;
            uploadPath = appPath + File.separator + uploadPath;

            String savedFilePath = uploadPath + File.separator + saved_name;
            filePart.write(savedFilePath);

            // Lưu metadata
            FileInfo info = new FileInfo();
            
            info.setFile_id(file_id);
            LocalDateTime now = LocalDateTime.now();

            info.setOriginal_name(original_name);
            info.setUser_id(user_id);
            info.setSaved_name(saved_name);
            info.setFile_size((long)filePart.getSize());
            info.setInput_path(inputDir);
            info.setOutput_path(uploadPath);
            info.setInput_format(input_format);
            info.setOutput_format(null);
            info.setCurrent_status(EnumStatus.FileStatus.UPLOADED);
            info.setCreated_at(now);
            info.setUpdated_at(now);

            fileService.saveFileMetaData(info);

            fileService.convertFile(file_id); 

            //Nếu là khách, lưu file_id vào session
            if(isGuest){
                @SuppressWarnings("unchecked")
                List<String> guestFile_ids = (List<String>) session.getAttribute("guestFile_ids");
                if(guestFile_ids == null){
                    guestFile_ids = List.of(file_id);
                    session.setAttribute("guestFile_ids", guestFile_ids);
                }
                guestFile_ids.add(file_id);
            }
            
            // Tạo phản hồi JSON
            jsonResponse.put("statusProgress", "PROCESSING");
            jsonResponse.put("file_id", info.getFile_id());
            jsonResponse.put("original_name", info.getOriginal_name());
            jsonResponse.put("saved_name", info.getSaved_name());
            jsonResponse.put("file_size", String.format("%.2f", info.getFile_size()));
            jsonResponse.put("input_path", inputDir);
            jsonResponse.put("output_path", uploadPath);
            jsonResponse.put("input_format", info.getInput_format());
            jsonResponse.put("output_format", info.getOutput_format());
            jsonResponse.put("current_status", info.getCurrent_status());
            jsonResponse.put("created_at", info.getCreated_at());
            jsonResponse.put("message", "File uploaded, conversion started!");

            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            jsonResponse.put("statusProgress", "FAILED");
            jsonResponse.put("message", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            response.getWriter().write(jsonResponse.toString());
            response.getWriter().flush();

            System.out.println(jsonResponse.toString());
        }
    }
}
