package model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;
import javax.swing.JOptionPane;

public class UserController {
    private static final String MEMBERS_FILE = "data/members.json";
    private static final String ADMINS_FILE = "data/admins.json";

    public UserController() {
        createUsersFileIfNotExists();
    }

    public void login(String username, String password) {
        List<Member> members = readMembersFromFile();
        List<Admin> admins = readAdminsFromFile();
        Optional<Member> member = members.stream().filter(m -> m.getUsername().equals(username)).findFirst();
        Optional<Admin> admin = admins.stream().filter(a -> a.getUsername().equals(username)).findFirst();
        if (member.isPresent()) {
            if (member.get().checkPassword(password)) {
                JOptionPane.showMessageDialog(null, "Login successful!");
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect password!");
            }
        } else if (admin.isPresent()) {
            if (admin.get().checkPassword(password)) {
                JOptionPane.showMessageDialog(null, "Login successful!");
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect password!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "User not found!");
        }
    }

    private void createUsersFileIfNotExists() {
        try {
            Files.createFile(Paths.get(MEMBERS_FILE));
            Files.createFile(Paths.get(ADMINS_FILE));
        } catch (IOException e) {
            System.out.println("Users file already exists");
        }
    }

    public void registerMember(String username, String password, String email) {
        List<Member> members = readMembersFromFile(); // Ensure this method never returns null
        if (members == null) {
            JOptionPane.showMessageDialog(null, "Error reading members from file.");
            return;
        }
    
        if (isEmailUsed(members, email)) {
            JOptionPane.showMessageDialog(null, "Email is already in use. Please try a different one.");
            return;
        }
    
        Member newMember = new Member(username, email, password); // Assuming Member has an appropriate constructor
        if (newMember == null || newMember.getUsername() == null || newMember.getEmail() == null) {
            JOptionPane.showMessageDialog(null, "Error creating a new member.");
            return;
        }
    
        members.add(newMember);
        writeMembersToFile(members);
        JOptionPane.showMessageDialog(null, "Member registered successfully!");
    }
    

    public void registerAdmin(String username, String password, String email) {
        // Similar to registerMember, but for Admin
        List<Admin> admins = readAdminsFromFile();
        if (isEmailUsed(admins, email)) {
            JOptionPane.showMessageDialog(null, "Email is already in use. Please try a different one.");
            return;
        }
        Admin newAdmin = new Admin(username, email, password);
        admins.add(newAdmin);
        writeAdminsToFile(admins);
    }

    // Similar to writeUsersToFile, but for Members
    private void writeMembersToFile(List<Member> members) {
        writeToFile(members, MEMBERS_FILE);
    }

    // Similar to writeUsersToFile, but for Admins
    private void writeAdminsToFile(List<Admin> admins) {
        writeToFile(admins, ADMINS_FILE);
    }

    private <T> void writeToFile(List<T> list, String filename) {
        Gson gson = new Gson();
        try (Writer writer = Files.newBufferedWriter(Paths.get(filename))) {
            gson.toJson(list, writer);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An error occurred while writing to the file: " + e.getMessage());
        }
    }

    // Similar to readUsersFromFile, but for Members
    private List<Member> readMembersFromFile() {
        try (Reader reader = Files.newBufferedReader(Paths.get(MEMBERS_FILE))) {
            Gson gson = new Gson();
            Type userListType = new TypeToken<ArrayList<Member>>(){}.getType();
            List<Member> members = gson.fromJson(reader, userListType);
            return members != null ? members : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
    
    private List<Admin> readAdminsFromFile() {
        try (Reader reader = Files.newBufferedReader(Paths.get(ADMINS_FILE))) {
            Gson gson = new Gson();
            Type userListType = new TypeToken<ArrayList<Admin>>(){}.getType();
            List<Admin> admins = gson.fromJson(reader, userListType);
            return admins != null ? admins : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private boolean isEmailUsed(List<? extends User> users, String email) {
        if (users == null) {
            return false; 
        }
        return users.stream().anyMatch(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email));
    }
}
