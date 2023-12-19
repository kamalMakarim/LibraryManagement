package model;
import model.Admin;
import model.Member;

public class UserFactory {
    public User createUser(String type, String username, String email, String password) {
        if (type.equals("Admin")) {
            return new Admin(username, email, password);
        } else if (type.equals("Member")) {
            return new Member(username, email, password);
        }
        return new Member(username, email, password);
    }
}