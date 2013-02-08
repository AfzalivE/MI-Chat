package com.macinsiders.chat.resource;

import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;

import com.macinsiders.chat.provider.ProviderContract.MessagesTable;

public class Message implements Resource {

    private static final String TAG = Message.class.getSimpleName();

    private long id;
    private long userId;
    private int channelId;
    private int userRole;
    private long datetime;
    private String username;
    private String message;

    // TODO Constructor to take messages node as argument
    public Message(long id, long userId, int channelId, int userRole, long datetime, String username, String message) {
        try {
            this.id = id;
            this.userId = userId;
            this.channelId = channelId;
            this.userRole = userRole;
            this.datetime = datetime;
            this.username = username;
            this.message = message;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    public Message(Bundle values) {
        try {
            this.username = values.getString(MessagesTable.USERNAME);
            this.message = values.getString(MessagesTable.MESSAGE);
            this.datetime = values.getLong(MessagesTable.DATETIME);
        } catch (NullPointerException e) {
            Log.e(TAG, "No values specified for new message");
        }
    }

    // apparently getters aren't a good idea for java
    public long id() {
        return id;
    }

    public long userId() {
        return id;
    }

    public long channelId() {
        return id;
    }

    public long userRole() {
        return userRole;
    }

    public long datetime() {
        return datetime;
    }

    public String username() {
        return username;
    }

    public String message() {
        return message;
    }

    public ContentValues toContentValues() {
        ContentValues rowData = new ContentValues();
        rowData.put(MessagesTable.REF_ID, this.id);
        rowData.put(MessagesTable.USERID, this.userId);
        rowData.put(MessagesTable.CHANNELID, this.channelId);
        rowData.put(MessagesTable.USERROLE, this.userRole);
        rowData.put(MessagesTable.DATETIME, this.datetime);
        rowData.put(MessagesTable.USERNAME, this.username);
        rowData.put(MessagesTable.MESSAGE, this.message);
        return rowData;
    }
}
