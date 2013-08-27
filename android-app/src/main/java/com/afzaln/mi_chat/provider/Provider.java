package com.afzaln.mi_chat.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.afzaln.mi_chat.provider.ProviderContract.InfoTable;
import com.afzaln.mi_chat.provider.ProviderContract.MessagesTable;
import com.afzaln.mi_chat.provider.ProviderContract.UsersTable;

import java.util.HashMap;

public class Provider extends ContentProvider {

    private static HashMap<String, String> infoProjectionMap;
    private static HashMap<String, String> userProjectionMap;
    private static HashMap<String, String> messageProjectionMap;
    private static final UriMatcher uriMatcher;

    private static final int MATCHER_INFO = 1;
    private static final int MATCHER_INFO_ID = 2;
    private static final int MATCHER_USER = 3;
    private static final int MATCHER_USER_ID = 4;
    private static final int MATCHER_MESSAGE = 5;
    private static final int MATCHER_MESSAGE_ID = 6;

    private ProviderDbHelper dbHelper;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(ProviderContract.AUTHORITY, InfoTable.TABLE_NAME, MATCHER_INFO);
        uriMatcher.addURI(ProviderContract.AUTHORITY, InfoTable.TABLE_NAME + "/#", MATCHER_INFO_ID);
        uriMatcher.addURI(ProviderContract.AUTHORITY, UsersTable.TABLE_NAME, MATCHER_USER);
        uriMatcher.addURI(ProviderContract.AUTHORITY, UsersTable.TABLE_NAME + "/#", MATCHER_USER_ID);
        uriMatcher.addURI(ProviderContract.AUTHORITY, MessagesTable.TABLE_NAME, MATCHER_MESSAGE);
        uriMatcher.addURI(ProviderContract.AUTHORITY, MessagesTable.TABLE_NAME + "/#", MATCHER_MESSAGE_ID);

        infoProjectionMap = new HashMap<String, String>();
        for (String column : InfoTable.COLUMNS) {
            infoProjectionMap.put(column, column);
        }
        userProjectionMap = new HashMap<String, String>();
        for (String column : UsersTable.COLUMNS) {
            userProjectionMap.put(column, column);
        }

        messageProjectionMap = new HashMap<String, String>();
        for (String column : MessagesTable.COLUMNS) {
            messageProjectionMap.put(column, column);
        }
    }

    @Override
    public boolean onCreate() {
        this.dbHelper = new ProviderDbHelper(this.getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] selectedColumns, String whereClause, String[] whereValues, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        /*
         * asking for a single info - use the info projection map, but add a
         * where clause to only return the one info
         */
        switch (uriMatcher.match(uri)) {
            case MATCHER_INFO:
                qb.setTables(InfoTable.TABLE_NAME);
                qb.setProjectionMap(infoProjectionMap);
                break;
            case MATCHER_INFO_ID:
                qb.setTables(InfoTable.TABLE_NAME);
                qb.setProjectionMap(infoProjectionMap);
                // Find the info ID itself in the incoming URI
                String infoId = uri.getPathSegments().get(InfoTable.INFO_ID_PATH_POSITION);
                qb.appendWhere(InfoTable._ID + "=" + infoId);
                break;
            case MATCHER_USER:
                qb.setTables(UsersTable.TABLE_NAME);
                qb.setProjectionMap(userProjectionMap);
                break;
            case MATCHER_USER_ID:
                qb.setTables(UsersTable.TABLE_NAME);
                qb.setProjectionMap(userProjectionMap);
                // Find the user ID itself in the incoming URI
                String userId = uri.getPathSegments().get(UsersTable.USER_ID_PATH_POSITION);
                qb.appendWhere(UsersTable._ID + "=" + userId);
                break;
            case MATCHER_MESSAGE:
                qb.setTables(MessagesTable.TABLE_NAME);
                qb.setProjectionMap(messageProjectionMap);
                break;
            case MATCHER_MESSAGE_ID:
                qb.setTables(MessagesTable.TABLE_NAME);
                qb.setProjectionMap(messageProjectionMap);
                // Find the message ID itself in the incoming URI
                String messageId = uri.getPathSegments().get(MessagesTable.MESSAGE_ID_PATH_POSITION);
                qb.appendWhere(MessagesTable._ID + "=" + messageId);
                break;
            default:
                // If the URI doesn't match any of the known patterns, throw an
                // exception.
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = this.dbHelper.getReadableDatabase();

        // the two nulls here are 'grouping' and 'filtering by group'
        Cursor cursor = qb.query(db, selectedColumns, whereClause, whereValues, null, null, sortOrder);

        // Tell the Cursor about the URI to watch, so it knows when its source
        // data changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case MATCHER_INFO:
                return "vnd.android.cursor.dir/vnd.chat.info";
            case MATCHER_INFO_ID:
                return "vnd.android.cursor.item/vnd.chat.info";
            case MATCHER_USER:
                return "vnd.android.cursor.dir/vnd.chat.user";
            case MATCHER_USER_ID:
                return "vnd.android.cursor.item/vnd.chat.user";
            case MATCHER_MESSAGE:
                return "vnd.android.cursor.dir/vnd.chat.message";
            case MATCHER_MESSAGE_ID:
                return "vnd.android.cursor.item/vnd.chat.message";
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            throw new SQLException("ContentValues argument for .insert() is null, cannot insert row");
        }

        SQLiteDatabase db = this.dbHelper.getWritableDatabase();

        db.beginTransaction();

        String insertTable;
        Uri baseUri;

        try {
            switch (uriMatcher.match(uri)) {
                case MATCHER_INFO:
                    insertTable = InfoTable.TABLE_NAME;
                    baseUri = InfoTable.CONTENT_ID_URI_BASE;
                    break;
                case MATCHER_USER:
                    insertTable = UsersTable.TABLE_NAME;
                    baseUri = UsersTable.CONTENT_ID_URI_BASE;
                    break;
                case MATCHER_MESSAGE:
                    insertTable = MessagesTable.TABLE_NAME;
                    baseUri = MessagesTable.CONTENT_ID_URI_BASE;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }

            long newRowId = db.insert(insertTable, null, values);

            if (newRowId > 0) {// if rowID is -1, it means the insert failed
                // Build a new URI with the new resource's ID
                // appended to it.
                db.setTransactionSuccessful();
                Uri notifyUri = ContentUris.withAppendedId(baseUri, newRowId);
                // Notify observers that our data changed.
                getContext().getContentResolver().notifyChange(notifyUri, null);
                return notifyUri;
            }
        } finally {
            db.endTransaction();
        }

        /* insert failed; halt and catch fire */
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String whereClause, String[] whereValues) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        int deletedRowsCount;
        String finalWhere;
        String tableName;

        db.beginTransaction();

        try {
            switch (uriMatcher.match(uri)) {
                case MATCHER_INFO:
                    tableName = InfoTable.TABLE_NAME;
                    break;

                case MATCHER_INFO_ID:
                    tableName = InfoTable.TABLE_NAME;
                    String infoId = uri.getPathSegments().get(InfoTable.INFO_ID_PATH_POSITION);
                    finalWhere = InfoTable._ID + " = " + infoId;

                    // if we were passed a 'where' arg, add that to our 'finalWhere'
                    if (whereClause != null) {
                        whereClause = finalWhere + " AND " + whereClause;
                    } else {
                        whereClause = finalWhere;
                    }
                    break;

                case MATCHER_USER:
                    tableName = UsersTable.TABLE_NAME;
                    break;

                case MATCHER_USER_ID:
                    tableName = UsersTable.TABLE_NAME;
                    String userId = uri.getPathSegments().get(UsersTable.USER_ID_PATH_POSITION);
                    finalWhere = UsersTable._ID + " = " + userId;

                    // if we were passed a 'where' arg, add that to our 'finalWhere'
                    if (whereClause != null) {
                        whereClause = finalWhere + " AND " + whereClause;
                    } else {
                        whereClause = finalWhere;
                    }
                    break;

                case MATCHER_MESSAGE:
                    tableName = MessagesTable.TABLE_NAME;
                    break;

                case MATCHER_MESSAGE_ID:
                    tableName = MessagesTable.TABLE_NAME;
                    String messageId = uri.getPathSegments().get(MessagesTable.MESSAGE_ID_PATH_POSITION);
                    finalWhere = MessagesTable._ID + " = " + messageId;

                    // if we were passed a 'where' arg, add that to our 'finalWhere'
                    if (whereClause != null) {
                        whereClause = finalWhere + " AND " + whereClause;
                    } else {
                        whereClause = finalWhere;
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }

            deletedRowsCount = db.delete(tableName, whereClause, whereValues);

            if (deletedRowsCount > 0) {
                db.setTransactionSuccessful();
                getContext().getContentResolver().notifyChange(uri, null);
            }
        } finally {
            db.endTransaction();
        }

        return deletedRowsCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    // Default bulkInsert is terrible. Make it better!
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        if (uriMatcher.match(uri) != MATCHER_USER && uriMatcher.match(uri) != MATCHER_MESSAGE) {
            throw new IllegalArgumentException("Unknown URI " + uri + ". Matcher returned: " + uriMatcher.match(uri));
        }

        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        db.beginTransaction();
        int insertedCount = 0;
        long newRowId = -1;
        try {
            for (ContentValues cv : values) {
                newRowId = this.insert(uri, cv, db);
                insertedCount++;
            }
            db.setTransactionSuccessful();
            // Build a new Node URI appended with the row ID of the last node to
            // get inserted in the batch
            Uri nodeUri;

            switch (uriMatcher.match(uri)) {
                case MATCHER_USER:
                    nodeUri = ContentUris.withAppendedId(UsersTable.CONTENT_ID_URI_BASE, newRowId);
                    break;
                case MATCHER_MESSAGE:
                    nodeUri = ContentUris.withAppendedId(MessagesTable.CONTENT_ID_URI_BASE, newRowId);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }
            // Notify observers that our data changed.
            getContext().getContentResolver().notifyChange(nodeUri, null);
            return insertedCount;

        } finally {
            db.endTransaction();
        }
    }

    // Used by our implementation of builkInsert
    private long insert(Uri uri, ContentValues initialValues, SQLiteDatabase writableDb) {
        // NOTE: this method does not initiate a transaction - this is up to the
        // caller!
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            throw new SQLException("ContentValues arg for .insert() is null, cannot insert row.");
        }

        long newRowId = -1;
        switch (uriMatcher.match(uri)) {
            case MATCHER_USER:
                newRowId = writableDb.insert(UsersTable.TABLE_NAME, null, values);
                break;
            case MATCHER_MESSAGE:
                newRowId = writableDb.insert(MessagesTable.TABLE_NAME, null, values);
                break;
        }

        if (newRowId == -1) { // if rowID is -1, it means the insert failed
            throw new SQLException("Failed to insert row into " + uri); // Insert
            // failed: halt and catch fire.
        }
        return newRowId;
    }

}
