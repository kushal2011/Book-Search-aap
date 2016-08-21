package com.example.kusha_000.booksearch;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    TextView text;
    EditText searchBox;
    ListView bookList;
    BookAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.infoText);
        searchBox = (EditText) findViewById(R.id.searchBox);
        searchBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (searchBox.getRight()
                            - searchBox.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        text.setVisibility(View.GONE);
                        if (isInternetConnected()) {
                            String searchKeyword = searchBox.getText().toString().trim();
                            if (searchKeyword == null) {
                                setErrorText("Search keyword can't be blank");
                                return false;
                            }
                            new FetchBookInfoTask().execute(searchKeyword);
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        searchBox.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.abc, 0);

        bookList = (ListView) findViewById(R.id.bookList);
        myAdapter = new BookAdapter(this, null);
        bookList.setAdapter(myAdapter);
    }

    public boolean isInternetConnected() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            setErrorText(getResources().getString(R.string.network_error));
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setErrorText(String errorText) {
        text.setVisibility(View.VISIBLE);
        text.setText(errorText);
        text.setTextColor(getColor(R.color.errorColor));
    }

    public class FetchBookInfoTask extends AsyncTask<String, Void, ListOfBooks[]> {

        private final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";

        @Override
        protected ListOfBooks[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            String keyword = params[0];
            String jsonResponse = fetchBookInfo(keyword);
            return fetchBookInfoFromJSON(jsonResponse);
        }

        @Override
        protected void onPostExecute(ListOfBooks[] listOfBookses) {
            if (listOfBookses == null) {
                setErrorText("No data found");
            } else {
                myAdapter.setData(listOfBookses);
                myAdapter.notifyDataSetChanged();
            }
        }

        private String fetchBookInfo(String searchKeyword) {
            InputStream inputStream = null;
            BufferedReader bufferedReader = null;
            HttpURLConnection conn = null;
            String jsonResponse = null;
            String finalUrl = BASE_URL + "?q=" + searchKeyword + "&maxResults=7";
            try {
                URL url = new URL(finalUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.connect();
                int response = conn.getResponseCode();
                if (response != 200) {
                    return null;
                }
                inputStream = conn.getInputStream();
                if (inputStream == null) {
                    return null;
                }
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                StringBuilder stringBuilder = new StringBuilder("");
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                if (stringBuilder.length() == 0) {
                    return null;
                }
                jsonResponse = stringBuilder.toString();
                Log.d(LOG_TAG, jsonResponse);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString());
            } finally {
                if (conn != null) {
                }
                conn.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return jsonResponse;
        }

        private ListOfBooks[] fetchBookInfoFromJSON(String jsonResponse) {
            final String ITEMS = "items";
            final String VOLUMES_INFO = "volumeInfo";
            final String TITLE = "title";
            final String AUTHORS = "authors";
            ListOfBooks[] listOfBooksInfo = null;
            try {
                JSONObject booksJSON = new JSONObject(jsonResponse);
                JSONArray booksJSONArray = booksJSON.getJSONArray(ITEMS);
                int itemsCount = booksJSONArray.length();
                listOfBooksInfo = new ListOfBooks[itemsCount];
                for (int i = 0; i < itemsCount; i++) {
                    JSONObject bookJSONOBject = booksJSONArray.getJSONObject(i);
                    JSONObject volumesJSONObject = bookJSONOBject.getJSONObject(VOLUMES_INFO);
                    String title = volumesJSONObject.getString(TITLE);
                    JSONArray authorsJSONArray = volumesJSONObject.getJSONArray(AUTHORS);
                    String authors = getAuthorsFromArray(authorsJSONArray);
                    listOfBooksInfo[i] = new ListOfBooks(title, authors);
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString());
            }
            return listOfBooksInfo;
        }

        private String getAuthorsFromArray(JSONArray authorsJSONArray) throws JSONException {
            int numberOfAuthors = authorsJSONArray.length();
            StringBuilder stringBuilder = new StringBuilder("");
            for (int i = 0; i < numberOfAuthors; i++) {
                String author = authorsJSONArray.getString(i);
                stringBuilder.append(author + ", ");
            }
            return stringBuilder.toString();
        }
    }
}
