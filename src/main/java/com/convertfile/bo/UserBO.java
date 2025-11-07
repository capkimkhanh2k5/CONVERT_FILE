package com.convertfile.bo;

import com.convertfile.dao.UserDAO;
import com.convertfile.model.User;
import com.convertfile.service.passwordService;

public class UserBO {
    private final UserDAO userDAO;

    public UserBO() {
        this.userDAO = new UserDAO();
    }

    public boolean registerUser(User user) {
        return userDAO.insertUser(user);
    }

    public boolean checkloginUser(String username, String password) {
        User user = userDAO.getUser(username);
        if(user == null) return false;

        return passwordService.checkPassword(password, user.getPassword());
    }

    public long getUserByUsername(String username){
        return Long.valueOf(this.userDAO.getUser(username).getId());
    }

}
