package com.afzaln.mi_chat.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.afzaln.mi_chat.provider.ProviderContract.InfoTable;
import com.afzaln.mi_chat.provider.ProviderContract.MessagesTable;
import com.afzaln.mi_chat.provider.ProviderContract.UsersTable;

public class ProviderDbHelper extends SQLiteOpenHelper {
    public final String TAG = getClass().getSimpleName();

    private static final String DATABASE_NAME = "mi_chat.db";
    private static final int DATABASE_VERSION = 5;

    public ProviderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + InfoTable.TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + UsersTable.TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + MessagesTable.TABLE_NAME + ";");
        createTables(db);
    }

    private void createTables(SQLiteDatabase db) {
        String infoTableBuilder;
        String usersTableBuilder;
        String messagesTableBuilder;

        infoTableBuilder = "CREATE TABLE " + InfoTable.TABLE_NAME + " ( " +
                InfoTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                InfoTable.USERID + " LONG, " +
                InfoTable.CHANNELID + " INTEGER, " +
                InfoTable.USERROLE + " INTEGER, " +
                InfoTable.USERNAME + " TEXT, " +
                InfoTable.CHANNELNAME + " TEXT" +
                ");";

        usersTableBuilder = "CREATE TABLE " + UsersTable.TABLE_NAME + " ( " +
                UsersTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UsersTable.USERID + " LONG, " +
                UsersTable.USERROLE + " INTEGER, " +
                UsersTable.CHANNELID + " INTEGER, " +
                UsersTable.USERNAME + " TEXT" +
                ");";

        messagesTableBuilder = "CREATE TABLE " + MessagesTable.TABLE_NAME + " ( " +
                MessagesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MessagesTable.MESSAGEID + " LONG UNIQUE, " +
                MessagesTable.TYPE + " INTEGER, " +
                MessagesTable.DATETIME + " LONG, " +
                MessagesTable.USERID + " LONG, " +
                MessagesTable.USERROLE + " INTEGER, " +
                MessagesTable.CHANNELID + " INTEGER, " +
                MessagesTable.USERNAME + " TEXT, " +
                MessagesTable.MESSAGE + " TEXT, " +
                MessagesTable.IMGLINKS + " TEXT" +
                ");";

        String sqlInfo = infoTableBuilder.toString();
        String sqlUsers = usersTableBuilder.toString();
        String sqlMessages = messagesTableBuilder.toString();

        db.execSQL(sqlInfo);
        db.execSQL(sqlUsers);
        db.execSQL(sqlMessages);
    }

}
