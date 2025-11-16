package com.convertfile.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.convertfile.model.User;
import com.convertfile.service.passwordService;

public class UserDAO {
    public boolean insertUser(User user) {
        String sql = "INSERT INTO users (username, password, email, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, passwordService.hassPassword(user.getPassword()));
            ps.setString(3, user.getEmail());
            ps.setTimestamp(4, Timestamp.valueOf(user.getCreated_at()));
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUser(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setCreated_at(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }
}
