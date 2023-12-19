package model;
import java.sql.Timestamp;
import java.util.Date;

public class Book {
    private String title;
    private String author;
    private BookStatus status;
    private Member currentMember;
    private Timestamp borrowDate;


    // Constructor
    public Book(String title, String author) {
        this.title = title;
        this.author = author;
        this.status = BookStatus.IN_LIBRARY;
        this.currentMember = null;
    }

    // Getter methods
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public BookStatus getStatus() {
        return status;
    }

    public Member getCurrentMember() {
        return currentMember;
    }

    // Setter methods
    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public void setCurrentMember(Member currentMember) {
        this.currentMember = currentMember;
    }

    // Method to check out the book
    public boolean checkOut(Member member) {
        if (status == BookStatus.IN_LIBRARY) {
            setStatus(BookStatus.BORROWED);
            setCurrentMember(member);
            this.borrowDate = new Timestamp(new Date().getTime());
            System.out.println(member.getUsername() + " has checked out " + title);
            return true;
        } else {
            System.out.println("Sorry, the book is not available for checkout.");
            return false;
        }
    }

    // Method to return the book
    public boolean returnBook() {
        if (status == BookStatus.BORROWED) {
            setStatus(BookStatus.IN_LIBRARY);
            System.out.println("Book returned successfully.");
            return true;
        } else {
            System.out.println("The book cannot be returned as it is not currently checked out.");
            return false;
        }
    }

    // Override toString method for better representation
    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", status=" + status +
                ", currentMember=" + (currentMember != null ? currentMember.getUsername() : "N/A") +
                '}';
    }

    public boolean passDueDate(Timestamp maxDuration){
        Timestamp now = new Timestamp(new Date().getTime());
        return now.after(new Timestamp(borrowDate.getTime() + maxDuration.getTime()));
    }
}
