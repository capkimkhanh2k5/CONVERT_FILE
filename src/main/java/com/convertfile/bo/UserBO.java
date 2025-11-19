package com.convertfile.bo;

import com.convertfile.model.bean.User;
import com.convertfile.model.dao.UserDAO;
import com.convertfile.service.passwordService;

public class UserBO {
    private final UserDAO userDAO;

    public UserBO() {
        this.userDAO = new UserDAO();
    }

    public boolean insertUser(User user) {
        //User login with GoogleAuthentication, set default password
        String password = user.getPassword();
        if(password == null || password.isEmpty()){
            user.setPassword(passwordService.hassPassword("GoogleAuthentication2"));
        }
        
        return userDAO.insertUser(user);
    }

    public boolean checkloginUser(String username, String password) {
        User user = userDAO.getUser(username);
        if(user == null) return false;

        return passwordService.checkPassword(password, user.getPassword());
    }

    public long getUserIdByUsername(String username){
        return Long.valueOf(this.userDAO.getUser(username).getId());
    }

    public String getUserEmailByUsername(String username) {
        User user = userDAO.getUser(username);
        if(user != null){
            return user.getEmail();
        }
        return null;
    }

    public static User getUserByEmail(String userEmail) {
        return UserDAO.getUserByEmail(userEmail);
    }

    public void updateUserInfo(User user) {
        userDAO.updateUserInfo(user);
    }

    public boolean checkEmailExist(String email) {
        return userDAO.checkEmailExist(email);
    }

    public boolean updatePassword(String email, String newPassword) {
        return userDAO.updatePassword(email, newPassword);
    }

}
