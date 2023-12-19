package ui;

import model.Book;
import model.BookController;
import model.Member;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class MemberFrame extends JFrame {
    private BookController bookController;
    private Member member;

    // UI Components
    private JTextField searchField;
    private JButton searchButton, checkOutButton, returnButton;
    private JList<String> bookList;
    private DefaultListModel<String> bookListModel;
    private ArrayList<Book> shownBooks;

    public MemberFrame(Member member) {
        this.member = member;
        this.bookController = BookController.getInstance(); // Assuming BookController is a singleton
    
        createUI();
    }
    

    private void createUI() {
        setTitle("Library System - Member Panel");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Search Panel
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Book List
        bookListModel = new DefaultListModel<>();
        bookList = new JList<>(bookListModel);
        JScrollPane scrollPane = new JScrollPane(bookList);

        // Buttons
        JPanel buttonPanel = new JPanel();
        checkOutButton = new JButton("Check Out");
        returnButton = new JButton("Return");
        buttonPanel.add(checkOutButton);
        buttonPanel.add(returnButton);

        // Add panels to frame
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        shownBooks = bookController.getAllBooks() ;
        refreshShownBooks(shownBooks);

        // Action Listeners
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchBooks();
            }
        });

        checkOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkOutBook();
            }
        });

        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnBook();
            }
        });
    }

    private void searchBooks() {
        String query = searchField.getText();
        ArrayList<Book> results = bookController.searchBooks(query);
        refreshShownBooks(results);
    }

    private void checkOutBook() {
        String bookInfo = bookList.getSelectedValue();
        if (bookInfo != null && !bookInfo.isEmpty()) {
            String selectedBookTitle = bookInfo.split(" --- ")[0]; 
            boolean success = bookController.checkOutBook(selectedBookTitle, member);
            if (success) {
                JOptionPane.showMessageDialog(this, "Book checked out successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to check out the book.");
            }
        }
    }

    private void returnBook() {
        String selectedBookTitle = bookList.getSelectedValue();
        if (selectedBookTitle != null) {
            boolean success = bookController.returnBook(selectedBookTitle);
            if (success) {
                JOptionPane.showMessageDialog(this, "Book returned successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to return the book.");
            }
        }
    }

    private void refreshShownBooks(ArrayList<Book> books) {
        this.shownBooks = books;
        bookListModel.clear();
        for (Book book : books) {
            bookListModel.addElement(book.getTitle() + " --- " + book.getAuthor());
        }
    }
}
