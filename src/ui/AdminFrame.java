package ui;

import model.Admin;
import model.Book;
import model.BookController;
import model.BookStatus;
import model.UserController;
import javax.swing.*;
import java.awt.*;
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
        shownBooks = bookController.getAllBooks();
        refreshShownBooks(shownBooks);
    
        // Book Management Panel
        JPanel manageBookPanel = new JPanel();
        manageBookPanel.setLayout(new BoxLayout(manageBookPanel, BoxLayout.Y_AXIS)); // Set layout
        titleField = new JTextField(10);
        authorField = new JTextField(10);
        statusComboBox = new JComboBox<>(BookStatus.values());
        addBookButton = new JButton("Add Book");
        changeStatusButton = new JButton("Change Status");
    
        addCompToPanel(manageBookPanel, new JLabel("Title:"), titleField);
        addCompToPanel(manageBookPanel, new JLabel("Author:"), authorField);
        addButton(manageBookPanel, addBookButton);
        addCompToPanel(manageBookPanel, new JLabel("Status:"), statusComboBox);
        addButton(manageBookPanel, changeStatusButton);
    
        // Admin Management Panel
        JPanel adminPanel = new JPanel();
        adminPanel.setLayout(new BoxLayout(adminPanel, BoxLayout.Y_AXIS)); // Set layout
        usernameField = new JTextField(10);
        passwordField = new JTextField(10);
        emailField = new JTextField(10);
        addAdminButton = new JButton("Add Admin");
    
        addCompToPanel(adminPanel, new JLabel("Username:"), usernameField);
        addCompToPanel(adminPanel, new JLabel("Password:"), passwordField);
        addCompToPanel(adminPanel, new JLabel("Email:"), emailField);
        adminPanel.add(addAdminButton);

    
        // Add panels to frame
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(manageBookPanel, BorderLayout.SOUTH);
        add(adminPanel, BorderLayout.EAST);
    
        // Action Listeners
        searchButton.addActionListener(e -> searchBooks());
        addBookButton.addActionListener(e -> addBook());
        changeStatusButton.addActionListener(e -> changeBookStatus());
        addAdminButton.addActionListener(e -> addAdmin());
    }

    private void addCompToPanel(JPanel panel, JComponent label, JComponent comp) {
        JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        flowPanel.add(label);
        flowPanel.add(comp);
        panel.add(flowPanel);
    }
    
    private void addButton(JPanel panel, JButton button) {
        JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        flowPanel.add(button);
        panel.add(flowPanel);
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
        shownBooks = bookController.getAllBooks();
        refreshShownBooks(shownBooks);
        JOptionPane.showMessageDialog(this, "Book added successfully.");
        
    }

    private void changeBookStatus() {
        String selectedBookTitle = bookList.getSelectedValue().split(" - ")[0]; // Extract title from selection
        BookStatus newStatus = (BookStatus) statusComboBox.getSelectedItem();
        bookController.changeBookStatus(selectedBookTitle, newStatus);
        refreshShownBooks(shownBooks);
        JOptionPane.showMessageDialog(this, "Book status updated successfully.");
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
            if(book.getStatus() == BookStatus.IN_LIBRARY){
                bookListModel.addElement(book.getTitle() + " - " + book.getAuthor() + " [" + book.getStatus() + "]");
            }
            if(book.getStatus() == BookStatus.BORROWED){
                String dueDateStatus = "";
                if(book.passDueDate(bookController.maxDuration)){
                    dueDateStatus = " has overdue";
                } else {
                    dueDateStatus = " was borrowed on " + book.borrowDate.toString();
                }
                bookListModel.addElement(book.getTitle() + " - " + book.getAuthor() + " [" + book.getStatus() + "] by " + book.getCurrentMember().getUsername() + dueDateStatus  );
            }
        }
    }
}
