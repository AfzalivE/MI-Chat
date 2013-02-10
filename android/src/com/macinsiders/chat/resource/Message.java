package com.macinsiders.chat.resource;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

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

    public Message(Node node) {
        // TODO do something if any of the attrs are not found
        NamedNodeMap userAttrs = node.getAttributes();
        id = Long.parseLong(userAttrs.getNamedItem("id").getTextContent());
        userId = Long.parseLong(userAttrs.getNamedItem("userID").getTextContent());
        channelId = Integer.parseInt(userAttrs.getNamedItem("channelID").getTextContent());
        userRole = Integer.parseInt(userAttrs.getNamedItem("userRole").getTextContent());

        SimpleDateFormat dateParser = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

        String datetimeStr = userAttrs.getNamedItem("dateTime").getTextContent();

        try {
            datetime = dateParser.parse(datetimeStr).getTime();
        } catch (ParseException e) {
            datetime = 0; // initialize to 0 if failed to parse
            e.printStackTrace();
        }

        // assume username is always the first node
        // and message is always the second/last node
        username = node.getFirstChild().getTextContent();
        message = node.getLastChild().getTextContent();

    }

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
