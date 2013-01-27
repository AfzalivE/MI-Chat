package com.macinsiders.chat.resource;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.net.Uri;

import com.macinsiders.chat.provider.ProviderContract.MessagesTable;

public class Messages implements Resource {
    public static final Uri CONTENT_URI = MessagesTable.CONTENT_URI;

    List<Message> messages;

    /**
     * Construct Messages from its JSON representation
     *
     * @param MessagesArray
     * @throws IllegalArgumentException
     *             - if the JSON does not contain the required keys
     */
    public Messages(JSONArray messagesArray) {
        int count = messagesArray.length();
        messages = new ArrayList<Message>();

        try {
            for (int i = 0; i < count; i++) {
                messages.add(new Message(messagesArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            throw new IllegalArgumentException("Error constructing Messages. " + e.getLocalizedMessage());
        }
    }

    /**
     * Get the list of messages
     *
     * @return list of messages
     */
    public List<Message> getMessages() {
        return messages;
    }
}
