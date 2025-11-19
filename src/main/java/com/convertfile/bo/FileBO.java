package com.convertfile.bo;

import com.convertfile.model.bean.Files;
import com.convertfile.model.dao.FileDAO;

public class FileBO {
    private final FileDAO fileDAO = new FileDAO();

    public boolean insertFile(Files file) {
        return fileDAO.insertFile(file);
    }

    public Files getFileByID(String fileID) {
        return fileDAO.getFileByID(fileID);
    }

    public boolean updateStatus(String fileID, String status) {
        return fileDAO.updateStatus(fileID, status);
    }

    public String[] getAllFile_idsByUser_id(long user_id) {
        return fileDAO.getAllFile_idsByUser_id(user_id);
    }

}
