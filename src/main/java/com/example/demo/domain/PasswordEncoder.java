package com.example.demo.domain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncoder {

    public static String encodePassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] hashedPassword = md.digest();

            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : hashedPassword) {
                stringBuilder.append(String.format("%02x", b));
            }

            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean checkPassword(String enteredPassword, String storedHash) {
        String enteredPasswordHash = encodePassword(enteredPassword);
        return enteredPasswordHash.equals(storedHash);
    }

}