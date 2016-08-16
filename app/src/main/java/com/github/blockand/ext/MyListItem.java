package com.github.blockand.ext;

import android.database.Cursor;

public class MyListItem {
    public int _id;
    public String name;
    public String number;
    public int count;

    public static MyListItem fromCursor(Cursor cursor) {
        MyListItem myListItem = new MyListItem();
        myListItem._id = cursor.getInt(0);
        myListItem.name = cursor.getString(1);
        myListItem.number = cursor.getString(2);
        myListItem.count = cursor.getInt(3);
        return myListItem;
    }
}