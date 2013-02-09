package com.macinsiders.chat.resource;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.net.Uri;

import com.macinsiders.chat.provider.ProviderContract.MessagesTable;

public class Messages implements Resource {
    public static final Uri CONTENT_URI = MessagesTable.CONTENT_URI;

    List<Message> messages;

    /**
     * Construct Messages from its xml Node representation
     *
     * @param MessagesArray
     * @throws IllegalArgumentException
     *             - if the node does not contain the required keys
     */

    public Messages(NodeList messagesNodes) {
        messages = new ArrayList<Message>();

        for (int i = 0; i < messagesNodes.getLength(); i++) {
            Node node = messagesNodes.item(i);
            messages.add(new Message(node));
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
