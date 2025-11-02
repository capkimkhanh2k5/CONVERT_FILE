package com.convertfile.service;

import org.mindrot.jbcrypt.BCrypt;

public class passwordService {
    //Hash password
    public static String hassPassword(String plainPassword){
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    //Check password
    public static boolean checkPassword(String plainPassword, String hashedPassword){
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
