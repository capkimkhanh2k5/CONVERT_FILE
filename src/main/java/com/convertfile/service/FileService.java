package com.convertfile.service;

import com.convertfile.dao.FileDAO;
import com.convertfile.model.FileInfo;

import java.sql.SQLException;
import java.util.List;
import java.util.Arrays;


public class FileService {
    private FileDAO fileDAO = new FileDAO();

    public boolean saveFileMetaData(FileInfo file) throws SQLException {
        return fileDAO.insertFileMetaData(file);
    }

    public boolean allowed_extension(String ext){
        //TODO: Fix khi có DATABASE
        List<String> ALLOWED_EXT = Arrays.asList("doc", "docx", "xml", "pdf", "txt"); // Example allowed extensions
        return ALLOWED_EXT.contains(ext);
    }

    //Check định dạng
    public void validateFile(String fileName, long size) throws IllegalArgumentException {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File nam invalid!");
        }
        String ext = getFileExtension(fileName);
        if (!allowed_extension(ext)) {
            throw new IllegalArgumentException("File extension invalid: " + ext);
        }
        if (size == 0) {
            throw new IllegalArgumentException("File empty!");
        }
    }

    public FileInfo getFileByID(String fileID){
        return fileDAO.getFileByID(fileID);
    }

    public List<FileInfo> getAllFiles(){
        return fileDAO.getAllFiles();
    }

    private String getFileExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        return (i > 0) ? fileName.substring(i + 1).toLowerCase() : "";
    }
}