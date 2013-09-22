package com.afzaln.mi_chat.resource;

import android.content.ContentValues;

import com.afzaln.mi_chat.provider.ProviderContract.MessagesTable;
import com.afzaln.mi_chat.utils.ParserUtils;

public class Message {

    private static final String TAG = Message.class.getSimpleName();

    public static final int NORMAL_TYPE = 0;
    public static final int ACTION_TYPE = 1;
    public static final int ERROR_TYPE = 2;

    Long messageId;
    int type;
    Long dateTime;
    Long userId;
    int userRole;
    int channelId;
    String userName;
    String message;
    String imgLinks;

    public Message(long messageId, int type, long dateTime, long userId, int userRole, int channelId, String userName, String messageText, String imgLinks) {
        this.messageId = messageId;
        this.type = type;
        this.dateTime = dateTime;
        this.userId = userId;
        this.userRole = userRole;
        this.channelId = channelId;
        this.userName = userName;
        this.message = ParserUtils.process(userName, messageText, type);
        this.imgLinks = imgLinks;
    }

    public ContentValues toContentValues() {
        ContentValues rowData = new ContentValues();
        rowData.put(MessagesTable.MESSAGEID, this.messageId);
        rowData.put(MessagesTable.TYPE, this.type);
        rowData.put(MessagesTable.DATETIME, this.dateTime);
        rowData.put(MessagesTable.USERID, this.userId);
        rowData.put(MessagesTable.USERROLE, this.userRole);
        rowData.put(MessagesTable.CHANNELID, this.channelId);
        rowData.put(MessagesTable.USERNAME, this.userName);
        rowData.put(MessagesTable.MESSAGE, this.message);
        rowData.put(MessagesTable.IMGLINKS, this.imgLinks);
//        Log.d(TAG, "converting to ContentValues, message: " + this.messageId.toString());
        return rowData;
    }
}
