package com.macinsiders.chat.resource;

import org.json.JSONObject;

import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;

import com.macinsiders.chat.provider.ProviderContract.MessagesTable;
import com.macinsiders.chat.utils.JSONUtil;

public class Message implements Resource {

    private static final String TAG = Message.class.getSimpleName();

    private int id;
    private String user;
    private String message;
    private long datetime;

    public Message(JSONObject json) {
        try {
            this.id = JSONUtil.getInt(json, "id");
            this.user = JSONUtil.getString(json, "username");
            this.message = JSONUtil.getString(json, "place");
            this.datetime = JSONUtil.getLong(json, "datetime") * 1000;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    public Message(Bundle values) {
        try {
            this.user = values.getString(MessagesTable.USERNAME);
            this.message = values.getString(MessagesTable.MESSAGE);
            this.datetime = values.getLong(MessagesTable.DATETIME);
        } catch (NullPointerException e) {
            Log.e(TAG,  "No values specified for new message");
        }
    }

    public int id() {
        return id;
    }

    public String user() {
        return user;
    }
    public String message() {
        return message;
    }
    public long datetime() {
        return datetime;
    }

    public ContentValues toContentValues() {
        ContentValues rowData = new ContentValues();
        rowData.put(MessagesTable.REF_ID, this.id);
        rowData.put(MessagesTable.USERNAME, this.user);
        rowData.put(MessagesTable.MESSAGE, this.message);
        rowData.put(MessagesTable.DATETIME, this.datetime);

        return rowData;
    }
}
