package com.convertfile.model.bean;

import java.time.LocalDateTime;

import com.convertfile.model.bean.EnumStatus.FileStatus;

public class Files {
    private String file_id;
    private long user_id;
    private String original_name;
    private String saved_name;
    private long file_size;
    private String file_path;
    private String public_id;
    private String input_public_id; // Public ID của file gốc (input)
    private String input_format;
    private String output_format;
    private FileStatus current_status;
    private String description;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    // Constructor
    public Files() {
        this.file_id = "";
        this.user_id = 0;
        this.original_name = "";
        this.saved_name = "";
        this.file_size = 0;
        this.file_path = "";
        this.public_id = "";
        this.input_public_id = "";
        this.input_format = "";
        this.output_format = "";
        this.current_status = FileStatus.UPLOADED;
        this.description = "";
        this.created_at = LocalDateTime.now();
        this.updated_at = LocalDateTime.now();
    }

    public Files(String file_id, long user_id, String original_name, String saved_name, long file_size,
            String file_path, String public_id, String input_public_id, String input_format, String output_format, FileStatus current_status,
            String description, LocalDateTime created_at, LocalDateTime updated_at) {
        this.file_id = file_id;
        this.user_id = user_id;
        this.original_name = original_name;
        this.saved_name = saved_name;
        this.file_size = file_size;
        this.file_path = file_path;
        this.public_id = public_id;
        this.input_public_id = input_public_id;
        this.input_format = input_format;
        this.output_format = output_format;
        this.current_status = current_status;
        this.description = description;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public Files(Files info) {
        this.file_id = info.getFile_id();
        this.user_id = info.getUser_id();
        this.original_name = info.getOriginal_name();
        this.saved_name = info.getSaved_name();
        this.file_size = info.getFile_size();
        this.file_path = info.getFile_path();
        this.public_id = info.getPublic_id();
        this.input_public_id = info.getInput_public_id();
        this.input_format = info.getInput_format();
        this.output_format = info.getOutput_format();
        this.current_status = info.getCurrent_status();
        this.description = info.getDescription();
        this.created_at = info.getCreated_at();
        this.updated_at = info.getUpdated_at();
    }

    // Getters / Setters
    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getOriginal_name() {
        return original_name;
    }

    public void setOriginal_name(String original_name) {
        this.original_name = original_name;
    }

    public String getSaved_name() {
        return saved_name;
    }

    public void setSaved_name(String saved_name) {
        this.saved_name = saved_name;
    }

    public long getFile_size() {
        return file_size;
    }

    public void setFile_size(long file_size) {
        this.file_size = file_size;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getPublic_id() {
        return public_id;
    }

    public void setPublic_id(String public_id) {
        this.public_id = public_id;
    }

    public String getInput_public_id() {
        return input_public_id;
    }

    public void setInput_public_id(String input_public_id) {
        this.input_public_id = input_public_id;
    }

    public String getInput_format() {
        return input_format;
    }

    public void setInput_format(String input_format) {
        this.input_format = input_format;
    }

    public String getOutput_format() {
        return output_format;
    }

    public void setOutput_format(String output_format) {
        this.output_format = output_format;
    }

    public FileStatus getCurrent_status() {
        return current_status;
    }

    public void setCurrent_status(FileStatus current_status) {
        this.current_status = current_status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }
}
