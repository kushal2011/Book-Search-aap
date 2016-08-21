package com.example.kusha_000.booksearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by kusha_000 on 29-06-2016.
 */
public class BookAdapter extends BaseAdapter {


    private ListOfBooks[] listOfBooksInfo;
    private Context context;
    private LayoutInflater inflater;

    public BookAdapter(MainActivity mainActivity, ListOfBooks[] listOfBooksInfo) {
        context = mainActivity;
        this.listOfBooksInfo = listOfBooksInfo;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (listOfBooksInfo != null) {
            return listOfBooksInfo.length;
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int i) {
        return listOfBooksInfo[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        ViewHolder holder = null;
        View rowView = convertView;
        if (rowView == null) {
            holder = new ViewHolder();
            rowView = inflater.inflate(R.layout.list_item_layout, null);
            holder.name = (TextView) rowView.findViewById(R.id.name);
            holder.author = (TextView) rowView.findViewById(R.id.author);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }
        holder.name.setText(listOfBooksInfo[i].getName());
        holder.author.setText(listOfBooksInfo[i].getAuthors());
        return rowView;
    }

    public void setData(ListOfBooks[] listOfBooksInfo) {
        this.listOfBooksInfo = listOfBooksInfo;
    }

    public class ViewHolder {
        TextView name;
        TextView author;
    }

}
