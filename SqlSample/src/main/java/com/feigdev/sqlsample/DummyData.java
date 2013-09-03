package com.feigdev.sqlsample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.feigdev.androidserialsql.AccessDB;
import com.feigdev.androidserialsql.DefineDB;
import com.feigdev.androidserialsql.WriterTask;

/**
 * Created by ejohn on 9/2/13.
 */
public class DummyData {
    private static final String TAG = "DummyData";
    private static final String DB_NAME = "DummyData";
    private static final int VERSION = 1;
    private static final String ITEMS = "items";
    private static final String ITEMS_TABLE_DEFINITION = "create table "
            + ITEMS + "( _id integer primary key autoincrement, item text);";
    private final DefineDB myDB = new DefineDB(DB_NAME, VERSION);
    private final AccessDB accessDB;

    public DummyData(Context c, Runnable callback) {
        myDB.setTableDefenition(ITEMS, ITEMS_TABLE_DEFINITION);
        accessDB = new AccessDB(c, myDB);
        accessDB.addWriteTask(new WriterTask(DB_NAME, null) {

            @Override
            public void run() {
                SQLiteDatabase db = getDB();
                db.beginTransaction();
                try {
                    for (int i = 0; i < 5; i++) {
                        String val;
                        switch (i) {
                            case 0:
                                val = "zero";
                                break;
                            case 1:
                                val = "one";
                                break;
                            case 2:
                                val = "two";
                                break;
                            case 3:
                                val = "three";
                                break;
                            case 4:
                                val = "four";
                                break;
                            default:
                                val = "broken";
                                break;
                        }
                        ContentValues values = new ContentValues();
                        values.put("item", val);
                        Log.d(TAG, "insert " + values.toString());
                        db.insert(ITEMS, null, values);
                    }
                    db.setTransactionSuccessful();
                } catch (Exception ex) {
                    Log.e(TAG, "failed to insert", ex);
                } finally {
                    db.endTransaction();
                }
            }
        });
        accessDB.addWriteTask(new WriterTask(DB_NAME, callback) {

            @Override
            public void run() {
                SQLiteDatabase db = getDB();
                db.beginTransaction();
                try {
                    ContentValues values = new ContentValues();
                    values.put("item", "five");
                    db.insert(ITEMS, null, values);
                    db.setTransactionSuccessful();
                } catch (Exception ex) {
                    Log.e(TAG, "failed to insert", ex);
                } finally {
                    db.endTransaction();
                }
                callback.run();
            }
        });

    }

    SQLiteDatabase getDB() {
        return accessDB.getReadableDB();
    }

    Cursor getItems() {
        return accessDB.getReadableDB().query(ITEMS, null, null, null, null, null, null);
    }

}