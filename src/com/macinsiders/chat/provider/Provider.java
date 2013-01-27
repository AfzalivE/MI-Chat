package com.macinsiders.chat.provider;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.macinsiders.chat.provider.ProviderContract.MessagesTable;

public class Provider extends ContentProvider {
    // TODO Switch to ContentProviderOperation for performance
    private static final String TAG = Provider.class.getSimpleName();

    private static HashMap<String, String> messagesProjectionMap;

    private static final UriMatcher uriMatcher;

    private static final int MATCHER_MESSAGES = 1;

    private static final int MATCHER_MESSAGE_ID = 2;

    /* other matchers for other objects */

    private ProviderDbHelper dbHelper;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(ProviderContract.AUTHORITY, MessagesTable.TABLE_NAME, MATCHER_MESSAGES);

        uriMatcher.addURI(ProviderContract.AUTHORITY, MessagesTable.TABLE_NAME + "/#", MATCHER_MESSAGE_ID);

        // Create and initialize a projection map that returns all columns,
        // This map returns a column name for a given string. The two are
        // usually equal, but we need this structure
        // later, down in .query()
        messagesProjectionMap = new HashMap<String, String>();
        for (String column : MessagesTable.ALL_COLUMNS) {
            messagesProjectionMap.put(column, column);
        }
    }

    @Override
    public boolean onCreate() {
        this.dbHelper = new ProviderDbHelper(this.getContext());
        return true;
    }

    @Override
    public int delete(Uri uri, String whereClause, String[] whereValues) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        int deletedRowsCount;
        String finalWhere;
        String tableName = null;

        db.beginTransaction();

        try {
            switch (uriMatcher.match(uri)) {
                case MATCHER_MESSAGES:
                    tableName = MessagesTable.TABLE_NAME;
                    break;

                case MATCHER_MESSAGE_ID:
                    tableName = MessagesTable.TABLE_NAME;
                    String id = uri.getPathSegments().get(MessagesTable.MESSAGE_ID_PATH_POSITION);
                    finalWhere = MessagesTable._ID + " = " + id;
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
    public Uri insert(Uri uri, ContentValues initialValues) {
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            throw new SQLException("ContentValues argument for .insert() is null, cannot insert row");
        }

        SQLiteDatabase db = this.dbHelper.getWritableDatabase();

        // Validate the incoming uri
        db.beginTransaction();
        // Perform the update based on the incoming uri's pattern
        String insertTable = null;
        Uri baseUri = null;

        try {
            switch (uriMatcher.match(uri)) {

                case MATCHER_MESSAGES:
                    insertTable = MessagesTable.TABLE_NAME;
                    baseUri = MessagesTable.CONTENT_ID_URI_BASE;
                    break;

                default:
                    // Incoming URI pattern is invalid: halt & catch fire.
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }

            Log.d(TAG, values.keySet().toString());
            long newRowId = db.insert(insertTable, null, values);

            Log.d(TAG, Long.toString(newRowId));
            if (newRowId > 0) { // if rowID is -1, it means the insert failed
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
    public Cursor query(Uri uri, String[] selectedColumns, String whereClause, String[] whereValues, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        // Choose the projection and adjust the "where" clause based on URI
        // pattern-matching.
        switch (uriMatcher.match(uri)) {
            case MATCHER_MESSAGES:
                qb.setTables(MessagesTable.TABLE_NAME);
                qb.setProjectionMap(messagesProjectionMap);
                break;

            /*
             * asking for a single message - use the messages projection, but add a
             * where clause to only return the one message
             */
            case MATCHER_MESSAGE_ID:
                qb.setTables(MessagesTable.TABLE_NAME);
                qb.setProjectionMap(messagesProjectionMap);
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
            case MATCHER_MESSAGES:
                return "vnd.android.cursor.dir/vnd.chat.message";
            case MATCHER_MESSAGE_ID:
                return "vnd.android.cursor.item/vnd.chat.message";
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues updateValues, String whereClause, String[] whereValues) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        int updatedRowsCount;
        String finalWhere = null;
        String tableName = null;

        db.beginTransaction();
        // Perform the update based on the incoming URI's pattern
        try {
            switch (uriMatcher.match(uri)) {

                case MATCHER_MESSAGES:
                    // Perform the update and return the number of rows updated.
                    tableName = MessagesTable.TABLE_NAME;
                    break;

                case MATCHER_MESSAGE_ID:
                    tableName = MessagesTable.TABLE_NAME;
                    String id = uri.getPathSegments().get(MessagesTable.MESSAGE_ID_PATH_POSITION);
                    finalWhere = MessagesTable._ID + " = " + id;

                    // if we were passed a 'where' arg, add that to our
                    // 'finalWhere'
                    if (whereClause != null) {
                        finalWhere = finalWhere + " AND " + whereClause;
                    }
                    break;

                default:
                    // Incoming URI pattern is invalid: halt & catch fire.
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }

            updatedRowsCount = db.update(tableName, updateValues, finalWhere, whereValues);

            Log.d(TAG, Integer.toString(updatedRowsCount));
            if (updatedRowsCount > 0) {
                db.setTransactionSuccessful();

                /*
                 * Gets a handle to the content resolver object for the current
                 * context, and notifies it that the incoming URI changed. The
                 * object passes this along to the resolver framework, and
                 * observers that have registered themselves for the provider
                 * are notified.
                 */
                getContext().getContentResolver().notifyChange(uri, null);
            }

        } finally {
            db.endTransaction();
        }

        // Returns the number of rows updated.
        return updatedRowsCount;
    }

    // Default bulkInsert is terrible. Make it better!
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        if (uriMatcher.match(uri) != MATCHER_MESSAGES) {
            throw new IllegalArgumentException("Unknown URI " + uri);
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
            Uri nodeUri = ContentUris.withAppendedId(MessagesTable.CONTENT_ID_URI_BASE, newRowId);
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

        long newRowId = writableDb.insert(MessagesTable.TABLE_NAME, null, values);
        if (newRowId == -1) { // if rowID is -1, it means the insert failed
            throw new SQLException("Failed to insert row into " + uri); // Insert
            // failed:
            // halt
            // and
            // catch
            // fire.
        }
        return newRowId;
    }

    /* to save images (or maybe only thumbnails */
    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        File file = new File(this.getContext().getFilesDir(), uri.getPath());
        ParcelFileDescriptor parcel = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        return parcel;
    }

}
