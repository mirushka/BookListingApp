package com.example.android.booklistingapp;

/**
 * Created by Mirka on 30/06/2017.
 */

public class Books {

    // Book title
    private String mBookTitle;

    // Book Subtitel
    private String mSubtitle;

    // Book writer
    private StringBuilder mBookAuthor;

    // URL of the book;
    private String mUrl;

    /**
     * Create a new constructor for Books object.
     *
     * @param title    is the title of the book
     * @param writer   is the names of the authors of the book
     * @param subtitle is the picture of the book
     * @param url      is the url of the book
     */

    public Books(String title, StringBuilder writer, String subtitle, String url) {
        mBookTitle = title;
        mBookAuthor = writer;
        mSubtitle = subtitle;
        mUrl = url;
    }

    //Getter methods
    public String getTitle() {
        return mBookTitle;
    }

    public StringBuilder getBookAuthor() {
        return mBookAuthor;
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    public String getUrl() {
        return mUrl;
    }


}
