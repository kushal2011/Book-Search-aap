package com.example.kusha_000.booksearch;

/**
 * Created by kusha_000 on 29-06-2016.
 */
public class ListOfBooks {
    private String name;
    private String authors;

    public ListOfBooks(String name, String authors) {
        this.name = name;
        this.authors = authors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }
}
