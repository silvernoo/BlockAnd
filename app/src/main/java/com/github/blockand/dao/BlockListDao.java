package com.github.blockand.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.github.blockand.dao.generate.BlockListDbHelper;
import com.github.blockand.ext.MyListItem;

import java.util.Random;

/**
 * Created by saikou on 2016/8/12 0012.
 * Email uedeck@gmail.com .
 */
public class BlockListDao {

    public final SQLiteDatabase writableDatabase;

    public int excludeId = -1;

    public BlockListDao(Context context) {
        BlockListDbHelper blockListDbHelper = new BlockListDbHelper(context);
        writableDatabase = blockListDbHelper.getWritableDatabase();
//        stuffing();
    }

    private void stuffing() {
        for (int i = 0; i < 10; i++) {
            ContentValues values = new ContentValues();
            values.put(FeedEntry.BlockList.name, "abc");
            values.put(FeedEntry.BlockList.number, String.valueOf(new Random().nextInt(9999 - 1000 + 1) + 1000));
            values.put(FeedEntry.BlockList.name, String.valueOf(new Random().nextInt(9999 - 1000 + 1) + 1000));
            writableDatabase.insert(BlockListDbHelper.TABLE_NAME, null, values);
        }
    }

    public Cursor loadAll(int st, int count) {
        if (excludeId == -1)
            return writableDatabase.query(BlockListDbHelper.TABLE_NAME, null, null, null, null, null, null, (st == -1) ? null : String.format("%s,%s", st, count));
        else
            return writableDatabase.query(BlockListDbHelper.TABLE_NAME, null, null, null, "_id", String.format("BLOCK_LIST._id != %s", excludeId), null, (st == -1) ? null : String.format("%s,%s", st, count));
    }

    public void addNumber(String name, String number) {
        ContentValues values = new ContentValues();
        values.put(FeedEntry.BlockList.name, name);
        values.put(FeedEntry.BlockList.number, number);
        writableDatabase.insert(BlockListDbHelper.TABLE_NAME, null, values);
    }

    public void flow() {
        writableDatabase.delete(BlockListDbHelper.TABLE_NAME, "BLOCK_LIST._id == ?", new String[]{String.valueOf(excludeId)});
        excludeId = -1;
    }

    public void deleteAll() {
        writableDatabase.delete(BlockListDbHelper.TABLE_NAME, null, null);
    }

    public void updateById(int id, MyListItem myListItem) {
        ContentValues values = new ContentValues();
        values.put(FeedEntry.BlockList.name, myListItem.name);
        values.put(FeedEntry.BlockList.number, myListItem.number);
        values.put(FeedEntry.BlockList.count, myListItem.count);
        writableDatabase.update(BlockListDbHelper.TABLE_NAME, values, "_id = ?", new String[]{String.valueOf(id)});
    }
}
