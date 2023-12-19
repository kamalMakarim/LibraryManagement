package ui;

import model.Book;
import model.BookController;
import model.BookStatus;
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
    private JButton searchButton, checkOutButton, returnButton, showBorrowButtonedButton;
    private JList<String> bookList;
    private DefaultListModel<String> bookListModel;
    private ArrayList<Book> shownBooks;
    private ArrayList<Book> myBorrowedBooks;
    private boolean showBorrowedBooks;

    public MemberFrame(Member member) {
        this.member = member;
        System.out.println(member.getUsername() + " has logged in.");
        showBorrowedBooks = false;
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
        showBorrowButtonedButton = new JButton("Show Borrowed Books");
        buttonPanel.add(showBorrowButtonedButton);
        buttonPanel.add(checkOutButton);
        buttonPanel.add(returnButton);
        returnButton.setVisible(showBorrowedBooks);

        // Add panels to frame
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        shownBooks = bookController.getAllBooks();
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
                shownBooks = bookController.getAllBooks();
                refreshShownBooks(shownBooks);
            }
        });

        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnBook();
                shownBooks = bookController.getBorrowedBooks(member);
            }
        });
        showBorrowButtonedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showBorrowedBooks = !showBorrowedBooks;
                returnButton.setVisible(showBorrowedBooks);
                checkOutButton.setVisible(!showBorrowedBooks);
                myBorrowedBooks = bookController.getBorrowedBooks(member);
                if(showBorrowedBooks){
                    showBorrowButtonedButton.setText("Show All Books");
                    refreshShownBooks(myBorrowedBooks);
                }else{
                    showBorrowButtonedButton.setText("Show Borrowed Books");
                    refreshShownBooks(bookController.getAllBooks());
                }
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
        String bookInfo = bookList.getSelectedValue();
        if (bookInfo != null && !bookInfo.isEmpty()) {
            String selectedBookTitle = bookInfo.split(" --- ")[0];
            if (myBorrowedBooks.stream().anyMatch(b -> b.getTitle().equals(selectedBookTitle))) {
                boolean success = bookController.returnBook(selectedBookTitle);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Book returned successfully.");
                    myBorrowedBooks.removeIf(b -> b.getTitle().equals(selectedBookTitle));
                    refreshShownBooks(myBorrowedBooks);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to return the book.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "This book is not borrowed by you.");
            }
        }
    }

    private void refreshShownBooks(ArrayList<Book> books) {
        if (books == null) {
            books = new ArrayList<>();  
        }
    
        if (bookListModel == null) {
            bookListModel = new DefaultListModel<>();
        }
    
        bookListModel.clear();
        for (Book book : books) {
            bookListModel.addElement(book.getTitle() + " --- " + book.getAuthor() + " [" + book.getStatus() + "]");
        }
    }
    
}
