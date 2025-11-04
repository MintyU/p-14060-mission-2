package com.back;

public class Quote {
    private int id;
    private String quote;
    private String author;

    public Quote(int id, String quote, String author) {
        this.id = id;
        this.quote = quote;
        this.author = author;
    }

    public int getId() {
        return this.id;
    }

    public String getQuote() {
        return this.quote;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
