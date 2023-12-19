package model;

public class MainLibrarySystem {
    private static MainLibrarySystem instance = new MainLibrarySystem();

    private MainLibrarySystem() {}

    public static MainLibrarySystem getInstance() {
        return instance;
    }

}
