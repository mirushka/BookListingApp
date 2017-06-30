package com.example.android.booklistingapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mirka on 30/06/2017.
 */

public class BooksAdapter extends ArrayAdapter<Books> {

    public BooksAdapter(Activity context, ArrayList<Books> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder;
        View booksList = convertView;

        if (booksList == null) {
            holder = new ViewHolder(booksList);
            booksList = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            holder.titleView = (TextView) booksList.findViewById(R.id.book_title);
            holder.subtitleView = (TextView) booksList.findViewById(R.id.subtitle);
            holder.authorView = (TextView) booksList.findViewById(R.id.author);
            booksList.setTag(holder);
        } else {
            holder = (ViewHolder) booksList.getTag();
        }

        // Find the book at the given position in the list of books
        Books currentBook = getItem(position);

        // Format the title of the current book in that TextView
        holder.titleView.setText(currentBook.getTitle());

        // Format the subtitle at the given position in the list of books
        holder.subtitleView.setText(currentBook.getSubtitle());

        // Format the title of the current book in that TextView
        holder.authorView.setText(currentBook.getBookAuthor());

        // Return the view
        return booksList;
    }

    private static class ViewHolder {
        @BindView(R.id.book_title)
        TextView titleView;
        @BindView(R.id.subtitle)
        TextView subtitleView;
        @BindView(R.id.author)
        TextView authorView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
