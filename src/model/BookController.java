package model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.lang.reflect.Type;
import java.sql.Timestamp;


public class BookController {
    private static BookController instance;
    private List<Book> books;
    private static final String BOOKS_FILE = "data/books.json";
    private Timestamp maxDuration = new Timestamp(7 * 24 * 60 * 60 * 1000); //a week in miliseconds

    private BookController() {
        this.books = new ArrayList<>();
        readBooksFromFile();
    }

    public static BookController getInstance() {
        if (instance == null) {
            instance = new BookController();
        }
        return instance;
    }

    public void addBook(String title, String author) {
        Book book = new Book(title, author);
        books.add(book);    
        saveBooksToFile();
    }

    public ArrayList<Book> passDueArrayList() {
        ArrayList<Book> results = new ArrayList<>();
        for (Book book : books) {
            if (book.getStatus() == BookStatus.BORROWED && book.passDueDate(maxDuration)) {
                results.add(book);
            }
        }
        return results;
    }

    public void removeBook(String title) {
        Optional<Book> book = books.stream().filter(b -> b.getTitle().equals(title)).findFirst();
        if (book.isPresent()) {
            books.remove(book.get());
            saveBooksToFile();
        } else {
            System.out.println("Book not found.");
        }
    }

    public ArrayList<Book> searchBooks(String query) {
        ArrayList<Book> results = new ArrayList<>();
        String lowerCaseQuery = query.toLowerCase();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(lowerCaseQuery) || book.getAuthor().toLowerCase().contains(lowerCaseQuery)) {
                results.add(book);
            }
        }
        return results;
    }
    

    public boolean checkOutBook(String title, Member member) {
        Optional<Book> book = books.stream().filter(b -> b.getTitle().equals(title)).findFirst();
        if (book.isPresent()) {
            if(book.get().checkOut(member)) {
                saveBooksToFile();
                return true;
            } else {
                return false;
            }
        } else {
            System.out.println(title);
            System.out.println("Book not found.");
            return false;
        }
    }

    public boolean returnBook(String title) {
        Optional<Book> book = books.stream().filter(b -> b.getTitle().equals(title)).findFirst();
        if (book.isPresent()) {
            if(book.get().returnBook()) {
                saveBooksToFile();
                return true;
            } else {
                return false;
            }
        } else {
            System.out.println("Book not found.");
            return false;
        }
    }

    private void saveBooksToFile() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = Files.newBufferedWriter(Paths.get(BOOKS_FILE))) {
            gson.toJson(books, writer);
        } catch (IOException e) {
            e.printStackTrace(); 
        }
    }

    private void readBooksFromFile() {
        if (!Files.exists(Paths.get(BOOKS_FILE))) {
            return; // No books file found, so just return
        }
        Gson gson = new Gson();
        try (Reader reader = Files.newBufferedReader(Paths.get(BOOKS_FILE))) {
            Type bookListType = new com.google.gson.reflect.TypeToken<ArrayList<Book>>(){}.getType();
            books = gson.fromJson(reader, bookListType);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception
        }
        if (books == null) {
            books = new ArrayList<>();
        }
    }

    public void changeBookStatus(String title, BookStatus status) {
        Optional<Book> book = books.stream().filter(b -> b.getTitle().equals(title)).findFirst();
        if (book.isPresent()) {
            book.get().setStatus(status);
            saveBooksToFile();
        } else {
            System.out.println("Book not found.");
        }
    }

    public ArrayList<Book> getAllBooks() {
        return new ArrayList<>(books);
    }
}
