package com.macinsiders.chat.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.macinsiders.chat.provider.ProviderContract.MessagesTable;

public class ProviderDbHelper extends SQLiteOpenHelper {

    public final String TAG = getClass().getSimpleName();

    // TODO Change db name
    private static final String DATABASE_NAME = "messages.db";
    private static final int DATABASE_VERSION = 1;

    public ProviderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MessagesTable.TABLE_NAME + ";");
        createTables(db);
    }

    private void createTables(SQLiteDatabase db) {
        /* create messages table */
        StringBuilder messageTableBuilder = new StringBuilder();
        messageTableBuilder.append("CREATE TABLE " + MessagesTable.TABLE_NAME + " (" +
               MessagesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
               MessagesTable._STATUS + " INTEGER, " +
               MessagesTable._RESULT + " INTEGER, " +
               MessagesTable.REF_ID + " INTEGER, " +
               MessagesTable.USERNAME + " TEXT, " +
               MessagesTable.MESSAGE + " TEXT, " +
               MessagesTable.DATETIME + " TEXT" +
               ");");

        String sqlMessages = messageTableBuilder.toString();
        Log.i(TAG, "Creating DB messages table with string: '" + sqlMessages + "'");
        db.execSQL(sqlMessages);

        /* create other tables */
    }

}
