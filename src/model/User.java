package model;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class User {
    private String username;
    private String email;
    private String passwordHash;


    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.passwordHash = hashPassword(password);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setUsername(String newUsername) {
        this.username = newUsername;
    }
    
    public void setEmail(String newEmail) {
        this.email = newEmail;
    }

    public void setPasswordHash(String newPassword) {
        this.passwordHash = hashPassword(newPassword);
    }

    public static User createUser(String type, String username, String email, String password) {
        if (type.equals("Admin")) {
            return new Admin(username, email, password);
        } else if (type.equals("Member")) {
            return new Member(username, email, password);
        }
        return null;
    }   

    public boolean checkPassword(String password) {
        return this.passwordHash.equals(hashPassword(password));
    }
}
