package com.afzal.mi_chat.resource;

import android.content.ContentValues;
import com.afzal.mi_chat.provider.ProviderContract.MessagesTable;
import com.afzal.mi_chat.utils.BbToHtml;

public class Message {

    private static final String TAG = Message.class.getSimpleName();

    Long messageId;
    Long dateTime;
    Long userId;
    int userRole;
    int channelId;
    String userName;
    String message;

    public Message(long messageId, long dateTime, long userId, int userRole, int channelId, String userName, String messageText) {
        this.messageId = messageId;
        this.dateTime = dateTime;
        this.userId = userId;
        this.userRole = userRole;
        this.channelId = channelId;
        this.userName = userName;
        this.message = parseMessage(messageText);;
    }

    private String parseMessage(String messageText) {
        messageText = BbToHtml.process(messageText);
        return messageText;
    }

    public ContentValues toContentValues() {
        ContentValues rowData = new ContentValues();
        rowData.put(MessagesTable.MESSAGEID, this.messageId);
        rowData.put(MessagesTable.DATETIME, this.dateTime);
        rowData.put(MessagesTable.USERID, this.userId);
        rowData.put(MessagesTable.USERROLE, this.userRole);
        rowData.put(MessagesTable.CHANNELID, this.channelId);
        rowData.put(MessagesTable.USERNAME, this.userName);
        rowData.put(MessagesTable.MESSAGE, this.message);
//        Log.d(TAG, "converting to ContentValues, message: " + this.messageId.toString());
        return rowData;
    }
}
