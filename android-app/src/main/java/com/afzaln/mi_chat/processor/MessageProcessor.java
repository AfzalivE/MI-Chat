package com.afzaln.mi_chat.processor;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.afzaln.mi_chat.handler.MessagesResponseHandler;
import com.afzaln.mi_chat.provider.ProviderContract.InfoTable;
import com.afzaln.mi_chat.provider.ProviderContract.MessagesTable;
import com.afzaln.mi_chat.provider.ProviderContract.UsersTable;
import com.afzaln.mi_chat.resource.Info;
import com.afzaln.mi_chat.resource.Message;
import com.afzaln.mi_chat.resource.Page;
import com.afzaln.mi_chat.resource.User;
import com.afzaln.mi_chat.utils.NetUtils;
import com.loopj.android.http.XmlHttpResponseHandler;

import org.w3c.dom.Document;

import java.util.List;

/**
 * Created by afzaln on 2013-05-21.
 */
public class MessageProcessor implements ResourceProcessor {

    protected static final String TAG = PageProcessor.class.getSimpleName();
    private Context mContext;
    private XmlHttpResponseHandler mMessagesResponseHandler = new MessagesResponseHandler(MessageProcessor.this);

    public MessageProcessor(Context context) {
        mContext = context;
    }

    @Override
    public void getResource() {

    }

    @Override
    public void postResource(Bundle params) {
        String message = params.getString("message");
        // get the last id
        long lastId = getLastMessageId();

        NetUtils.postMessage(mMessagesResponseHandler, message, lastId);
    }

    @Override
    public void updateContentProvider(Document result) {
        Page page = new Page(result);
        List<User> userList;
        List<Message> messageList;
        Info info;

        ContentResolver cr = this.mContext.getContentResolver();

        try {
            userList = page.getUserList();
            cr.delete(UsersTable.CONTENT_URI, null, null);
            ContentValues[] crValues = new ContentValues[userList.size()];
            for (int i = 0; i < userList.size(); i++) {
                crValues[i] = userList.get(i).toContentValues();
            }
            cr.bulkInsert(UsersTable.CONTENT_URI, crValues);
        } catch (NullPointerException e) {
            Log.d(TAG, "couldn't get user list");
        }

        try {
            messageList = page.getMessageList();
            ContentValues[] crValues = new ContentValues[messageList.size()];
            for (int i = 0; i < messageList.size(); i++) {
                crValues[i] = messageList.get(i).toContentValues();
            }
            cr.bulkInsert(MessagesTable.CONTENT_URI, crValues);
        } catch (NullPointerException e) {
            Log.d(TAG, "couldn't get message list");
        }

        try {
            info = page.getInfo();
            if (info != null) {
                cr.delete(InfoTable.CONTENT_URI, null, null);
                cr.insert(InfoTable.CONTENT_URI, info.toContentValues());
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "couldn't get user info");
        }

    }

    private long getLastMessageId() {
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(MessagesTable.CONTENT_URI, new String[]{MessagesTable.MESSAGEID}, null, null, MessagesTable.MESSAGEID + " DESC LIMIT 1");

        if (cursor.moveToNext()) {
            Log.d(TAG, Long.toString(cursor.getLong(0)));
            long lastId = cursor.getLong(0);
            Log.d(TAG, "Last ID: " + Long.toString(lastId));
            return lastId;
        } else {
            return -1;
        }
    }

    @Override
    public void putResource(ResourceProcessorCallback callback, Bundle params) {

    }

    @Override
    public void deleteResource() {

    }
}
