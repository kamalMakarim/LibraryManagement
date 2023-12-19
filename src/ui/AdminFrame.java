package ui;

import model.Admin;
import model.Book;
import model.BookController;
import model.BookStatus;
import model.User;
import model.UserController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class AdminFrame extends JFrame {
    private BookController bookController;
    private UserController userController;
    private Admin admin;

    // UI Components
    private JTextField searchField, titleField, authorField, usernameField, passwordField, emailField;
    private JButton searchButton, addBookButton, changeStatusButton, addAdminButton;
    private JList<String> bookList;
    private JComboBox<BookStatus> statusComboBox;
    private DefaultListModel<String> bookListModel;
    private ArrayList<Book> shownBooks;

    public AdminFrame(Admin admin) {
        this.admin = admin;
        this.bookController = BookController.getInstance(); // Assuming BookController is a singleton
        this.userController = UserController.getInstance(); // Assuming UserController is a singleton

        createUI();
    }

    private void createUI() {
        setTitle("Library System - Admin Panel");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        

        // Search Panel for Books
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Book List
        bookListModel = new DefaultListModel<>();
        bookList = new JList<>(bookListModel);
        JScrollPane scrollPane = new JScrollPane(bookList);

        // Book Management Panel
        JPanel manageBookPanel = new JPanel();
        titleField = new JTextField(10);
        authorField = new JTextField(10);
        statusComboBox = new JComboBox<>(BookStatus.values());
        addBookButton = new JButton("Add Book");
        changeStatusButton = new JButton("Change Status");

        manageBookPanel.add(new JLabel("Title:"));
        manageBookPanel.add(titleField);
        manageBookPanel.add(new JLabel("Author:"));
        manageBookPanel.add(authorField);
        manageBookPanel.add(statusComboBox);
        manageBookPanel.add(addBookButton);
        manageBookPanel.add(changeStatusButton);

        // Admin Management Panel
        JPanel adminPanel = new JPanel();
        usernameField = new JTextField(10);
        passwordField = new JTextField(10);
        emailField = new JTextField(10);
        addAdminButton = new JButton("Add Admin");

        adminPanel.add(new JLabel("Username:"));
        adminPanel.add(usernameField);
        adminPanel.add(new JLabel("Password:"));
        adminPanel.add(passwordField);
        adminPanel.add(new JLabel("Email:"));
        adminPanel.add(emailField);
        adminPanel.add(addAdminButton);

        // Add panels to frame
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(manageBookPanel, BorderLayout.WEST);
        add(adminPanel, BorderLayout.EAST);
        shownBooks = bookController.getAllBooks();
        refreshShownBooks(shownBooks);

        // Action Listeners
        searchButton.addActionListener(e -> searchBooks());
        addBookButton.addActionListener(e -> addBook());
        changeStatusButton.addActionListener(e -> changeBookStatus());
        addAdminButton.addActionListener(e -> addAdmin());
    }

    private void searchBooks() {
        String query = searchField.getText();
        ArrayList<Book> results = bookController.searchBooks(query);
        refreshShownBooks(results);
    }

    private void addBook() {
        String title = titleField.getText();
        String author = authorField.getText();
        bookController.addBook(title, author);
        JOptionPane.showMessageDialog(this, "Book added successfully.");
        refreshShownBooks(shownBooks);
    }

    private void changeBookStatus() {
        String selectedBookTitle = bookList.getSelectedValue().split(" - ")[0]; // Extract title from selection
        BookStatus newStatus = (BookStatus) statusComboBox.getSelectedItem();
        bookController.changeBookStatus(selectedBookTitle, newStatus);
        JOptionPane.showMessageDialog(this, "Book status updated successfully.");
        refreshShownBooks(shownBooks);
    }

    private void addAdmin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        userController.registerAdmin(username, password, email);
        JOptionPane.showMessageDialog(this, "New admin added successfully.");
    }

    private void refreshShownBooks(ArrayList<Book> books) {
        bookListModel.clear();
        for (Book book : books) {
            bookListModel.addElement(book.getTitle() + " - " + book.getAuthor() + " [" + book.getStatus() + "]");
        }
    }
}
