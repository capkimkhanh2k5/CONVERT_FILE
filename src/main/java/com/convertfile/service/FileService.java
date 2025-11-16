package com.convertfile.service;

import com.convertfile.dao.FileDAO;
import com.convertfile.model.FileInfo;

import java.sql.SQLException;
import java.util.List;
import java.util.Arrays;


public class FileService {
    private FileDAO fileDAO = new FileDAO();
    //TODO: Bổ sung khi có DATABASE
    private final List<String> typeFile = Arrays.asList("doc", "docx", "xml", "pdf", "txt");

    public boolean saveFileMetaData(FileInfo file) throws SQLException {
        return fileDAO.insertFile(file);
    }

    public boolean allowed_extension(String ext){
        return typeFile.contains(ext);
    }


    public boolean isSupportedConversion(String inputFormat, String outputFormat){
        if (inputFormat == null || outputFormat == null) return false;

        String inF = inputFormat.trim().toLowerCase();
        String outF = outputFormat.trim().toLowerCase();

        boolean inOk = typeFile.contains(inF);
        boolean outOk = typeFile.contains(outF);

        return inOk && outOk && !inF.equals(outF);
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

    public static String getFileExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        return (i > 0) ? fileName.substring(i + 1).toLowerCase() : "";
    }

    public void convertFile(String file_id) {
        // TODO: sử dụng microService
        throw new UnsupportedOperationException("Unimplemented method 'convertFile'");
    }
}