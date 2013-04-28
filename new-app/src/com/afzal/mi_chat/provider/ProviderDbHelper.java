package com.afzal.mi_chat.provider;

import com.afzal.mi_chat.provider.ProviderContract.InfoTable;
import com.afzal.mi_chat.provider.ProviderContract.UsersTable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProviderDbHelper extends SQLiteOpenHelper {
    public final String TAG = getClass().getSimpleName();

    private static final String DATABASE_NAME = "mi_chat.db";
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
//        db.execSQL("DROP TABLE IF EXISTS " + InfoTable.TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + UsersTable.TABLE_NAME + ";");
//        db.execSQL("DROP TABLE IF EXISTS " + MessagesTable.TABLE_NAME + ";");
        createTables(db);
    }

    private void createTables(SQLiteDatabase db) {
        StringBuilder infoTableBuilder = new StringBuilder();
        StringBuilder usersTableBuilder = new StringBuilder();
        StringBuilder messagesTableBuilder = new StringBuilder();

        infoTableBuilder.append("CREATE TABLE " + InfoTable.TABLE_NAME + " ( " +
                InfoTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                InfoTable.USERID + " LONG, " +
                InfoTable.CHANNELID + " INTEGER, " +
                InfoTable.USERROLE + " INTEGER, " +
                InfoTable.USERNAME + " TEXT, " +
                InfoTable.CHANNELNAME + " TEXT" +
                ");");

        usersTableBuilder.append("CREATE TABLE " + UsersTable.TABLE_NAME + " ( " +
                UsersTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UsersTable.USERID + " LONG, " +
                UsersTable.USERROLE + " INTEGER, " +
                UsersTable.CHANNELID + " INTEGER, " +
                UsersTable.USERNAME + " TEXT" +
                ");");

        String sqlInfo = infoTableBuilder.toString();
        String sqlUsers = usersTableBuilder.toString();

        db.execSQL(sqlInfo);
        db.execSQL(sqlUsers);

        // TODO other tables

    }

}
