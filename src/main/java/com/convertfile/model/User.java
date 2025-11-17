package com.convertfile.model;

import java.time.LocalDateTime;

public class User {
    private long user_id;
    private String username;
    private String password;
    private String email;
    private String picture_url;
    private LocalDateTime created_at;

    public User() {
        this.user_id = 0;
        this.username = "";
        this.password = "";
        this.email = "";
        this.picture_url = "";
        this.created_at = LocalDateTime.now();
    }

    public User(long user_id, String username, String password, String email, String picture_url, LocalDateTime created_at) {
        this.user_id = user_id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.picture_url = picture_url;
        this.created_at = created_at;
    }

    public User(User user) {
        this.user_id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.picture_url = user.getPicture_url();  
        this.created_at = user.getCreated_at();
    }

    public long getId() { return user_id; }
    public void setId(long user_id) { this.user_id = user_id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }

    public String getPicture_url() { return picture_url; }
    public void setPicture_url(String picture_url) { this.picture_url = picture_url; }

}
