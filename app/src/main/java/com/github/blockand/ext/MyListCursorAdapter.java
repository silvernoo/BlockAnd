package com.github.blockand.ext;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.blockand.R;

/**
 * Created by skyfishjy on 10/31/14.
 */
public class MyListCursorAdapter extends CursorRecyclerViewAdapter<MyListCursorAdapter.ViewHolder> {

    public MyListCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        public TextView mTextView1;
        public TextView mTextView2;
        public TextView count;
        public int position;
        public int _id;


        public ViewHolder(View view) {
            super(view);
            mTextView1 = (TextView) view.findViewById(R.id.text1);
            mTextView2 = (TextView) view.findViewById(R.id.text2);
            count = (TextView) view.findViewById(R.id.count);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor, int position) {
        MyListItem myListItem = MyListItem.fromCursor(cursor);
        viewHolder.mTextView1.setText(myListItem.name == null ? viewHolder.mTextView1.getResources().getString(R.string.no_name) : myListItem.name);
        viewHolder.mTextView2.setText(myListItem.number);
        viewHolder.count.setText(String.valueOf(myListItem.count));
        viewHolder._id = myListItem._id;
        viewHolder.position = position;
    }
}