package com.convertfile.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        
        HttpSession session = request.getSession(false);

        // Nếu không có session -> Guest
        if(session == null){
            request.setAttribute("username", "GUEST");
            request.setAttribute("files", new ArrayList<FileInfo>());
            request.setAttribute("totalFiles", 0);
            request.setAttribute("completedFiles", 0);
            
            request.getRequestDispatcher("home.jsp").forward(request, response);
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

            List<FileInfo> filesList = new ArrayList<>();
            int totalFiles = 0;
            int completedFiles = 0;
            
            for(String file_id : file_ids) {
                FileInfo file = fileBO.getFileByID(file_id);
                if (file == null) continue;
                
                filesList.add(file);
                totalFiles++;
                
                if(file.getCurrent_status() == EnumStatus.FileStatus.CONVERTED) {
                    completedFiles++;
                }
            }
            
            request.setAttribute("username", username);
            request.setAttribute("files", filesList);
            request.setAttribute("totalFiles", totalFiles);
            request.setAttribute("completedFiles", completedFiles);
            
            request.getRequestDispatcher("home.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");

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
                    guestFile_ids = new ArrayList<>();
                    session.setAttribute("guestFile_ids", guestFile_ids);
                }
                guestFile_ids.add(file_id);
            }
            
            // Set attributes cho JSP
            request.setAttribute("statusProgress", "PROCESSING");
            request.setAttribute("fileInfo", info);
            request.setAttribute("message", "File uploaded, conversion started!");

            request.getRequestDispatcher("upload-success.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("statusProgress", "FAILED");
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("upload-error.jsp").forward(request, response);
        }
    }
}