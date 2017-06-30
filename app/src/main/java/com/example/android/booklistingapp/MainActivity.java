package com.example.android.booklistingapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Tag for LOG Message
    private static final String LOG_TAG = MainActivity.class.getName();

    // URL from Google API
    private static final String BOOK_URL_BASE =
            "https://www.googleapis.com/books/v1/volumes?q=";

    // Google Books URL with the given key Android for the query result
    private static final String BOOK_URL_ANDROID =
            "https://www.googleapis.com/books/v1/volumes?q=android&maxResults=10";

    // Add a maximum results of 20 to the search query
    private static final String MAX_RESULTS = "&maxResults=20";

    // Adapter for the Books list
    private BooksAdapter mAdapter;

    // Edit field where the user can search for books
    private EditText searchEditTextView;

    // TextView that is visible when there is a problem with the connection or the query
    private TextView mEmptyView;

    // ProgressBar that is visible when
    private View loadingIndicator;

    // Internet connection checker
    private boolean isInternetConnected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to Views
        final ListView bookListView = (ListView) findViewById(R.id.list_view);
        searchEditTextView = (EditText) findViewById(R.id.search_EditTextView);
        ImageButton searchButton = (ImageButton) findViewById(R.id.search_button);
        mEmptyView = (TextView) findViewById(R.id.empty_text_view);
        loadingIndicator = findViewById(R.id.progress_bar);

        isInternetConnected = checkInternetConnection();

        // Set empty state view on the list view with books, when there is no data.
        bookListView.setEmptyView(mEmptyView);

        // Hide the keyboard when the app starts
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Log.e(LOG_TAG, "Hide the keyboard.");

        // Create new adapter that takes an empty list of books as input
        mAdapter = new BooksAdapter(this, new ArrayList<Books>());

        // Set the adapter on the {@link ListView)
        // so the list can be populated in the user interface
        bookListView.setAdapter(mAdapter);

        // OnItemClickListener open the website for the current book
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Books currentBook = mAdapter.getItem(position);
                Uri bookUri = Uri.parse(currentBook.getUrl());
                Intent webIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                if (webIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(webIntent);
                }
            }
        });

        BookListAsyncTask task = new BookListAsyncTask();

        //If there is a network connection
        if (isInternetConnected) {
            Log.e(LOG_TAG, "This is called when there is an Internet connection.");
            // Start the AsyncTask to fetch the books data
            task.execute(BOOK_URL_ANDROID);

        } else {
            Log.e(LOG_TAG, "This is called when there is NO Internet connection.");
            // Otherwise, display error
            // First, hide loading indicator so error will be visible
            loadingIndicator.setVisibility(View.GONE);
            //Show the empty state with no connection error message
            mEmptyView.setVisibility(View.VISIBLE);
            //Update empty state with no connection error message
            mEmptyView.setText(R.string.no_connection);
        }

        // Set a click listener to the ImageButton Searc
        searchButton.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                //If there is a network connection
                if (isInternetConnected) {
                    Log.e(LOG_TAG, "This is called if there is a internet connection.");
                    String searchWord = searchEditTextView.getText().toString().replaceAll("\\s+", "")
                            .toLowerCase();
                    if (searchWord.isEmpty()) {
                        Toast.makeText(MainActivity.this, getString(R.string.hint),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(LOG_TAG, "This is called when there is an Internet connection and a new search word.");
                        // Hide the empty state text
                        mEmptyView.setVisibility(View.GONE);
                        // Start the AsyncTask to fetch the books data
                        new BookListAsyncTask().execute(BOOK_URL_BASE + searchWord + MAX_RESULTS);
                        Log.e(LOG_TAG, "Show the URL with the user input: " + BOOK_URL_BASE
                                + searchWord);
                    }
                } else {
                    Log.e(LOG_TAG, "This is called when there is NO Internet connection.");
                    // First, hide loading indicator so error will be visible
                    loadingIndicator.setVisibility(View.GONE);
                    //Show the empty state with no connection error message
                    mEmptyView.setVisibility(View.VISIBLE);
                    //Update empty state with no connection error message
                    mEmptyView.setText(R.string.no_connection);
                }
            }
        });
    }

    // Check the Internet Connection.

    private boolean checkInternetConnection() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setText(R.string.no_connection);
            return false;
        }
    }


    /**
     * {@link AsyncTask} to perform the network request on a background thread, and
     * then update the UI with the list of books in the response.
     * <p>
     * We''ll only override two of the methods of AsyncTask doInBackground() and onPostExecute()
     * The doInBackground() runs on a background thread, so it can run long running code
     * (like network activity), without interfering with the responsiveness of the app.
     * The onPostExecute() is passed the result of the doInBackground() method, but runs on the
     * UI thread, so it can use the produced data to update the UI.
     */
    private class BookListAsyncTask extends AsyncTask<String, Void, List<Books>> {

        /**
         * This method runs on the UI thread before doInBackground().
         * It shows the progress bar when the internet connection is delayed or slow.
         */
        @Override
        protected void onPreExecute() {
            Log.e(LOG_TAG, "When the onPreExecute is called?");
            // Show loading indicator if the Internet Connection is delayed or slow.
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        /**
         * This method runs on a background thread and performs the network request.
         * We should not update the UI from a background thread, so we return a list of
         * {@link Books}s as the result.
         *
         * @return {@link Books}s as the result
         */
        @Override
        protected List<Books> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            List<Books> result = UtilsQuery.fetchBooksData(urls[0]);
            return result;
        }

        /**
         * This method runs on the main UI thread after the background work has
         * completed. This method receives as input, the return value from the doInBackground()
         * method. First clears out the adapter, to get get rid of books data from a previous
         * query to Google Books API. Then updates the adapter with the new list of books,
         * which will trigger the ListView to re-populate its list items.
         */
        @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
        @Override
        protected void onPostExecute(List<Books> books) {
            // First, hide loading indicator so error will be visible
            loadingIndicator.setVisibility(View.GONE);

            // Clear the adapter of previous earthquake data
            mAdapter.clear();

            // If there is a valid list of {@link Book}s, then add to the adapter's
            // data set. This will trigger the ListView to update.
            if (books != null && !books.isEmpty()) {
                mAdapter.addAll(books);
            } else {
                //Show the empty state with no connection error message
                mEmptyView.setVisibility(View.VISIBLE);
                //Update empty state text to display "Oops, No data found."
                mEmptyView.setText(R.string.no_books_found);
            }
        }
    }
}
