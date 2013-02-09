package com.macinsiders.chat.processor;

import java.net.UnknownHostException;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.macinsiders.chat.provider.ProviderContract.MessagesTable;
import com.macinsiders.chat.provider.ProviderContract.RESOURCE_TRANSACTION_FLAG;
import com.macinsiders.chat.resource.Message;
import com.macinsiders.chat.resource.Messages;
import com.macinsiders.chat.rest.method.GetMessagesRestMethod;
import com.macinsiders.chat.rest.method.PostMessagesRestMethod;
import com.macinsiders.chat.rest.method.RestMethod;
import com.macinsiders.chat.rest.method.RestMethodResult;

public class MessagesProcessor implements ResourceProcessor {

    protected static final String TAG = MessagesProcessor.class.getSimpleName();

    private Context mContext;

    public MessagesProcessor(Context context) {
        mContext = context;
    }

    @Override
    public void getResource(ResourceProcessorCallback callback, Bundle params) {
        String newestId = getNewestMessageId();
        RestMethod<Messages> method = new GetMessagesRestMethod(mContext, newestId);
        RestMethodResult<Messages> result = null;
//        try {
            result = method.execute();
//        } catch (UnknownHostException e) {
//            Log.e(TAG, "Internet error: " + e.getLocalizedMessage());
//        }

        updateContentProvider(result);

        callback.send(result.getStatusCode(), null);
    }

    @Override
    public void postResource(ResourceProcessorCallback callback, Bundle params) {
        Log.d(TAG, "start postResource");
        Message Message = new Message(params);
        Log.d(TAG, "Created Message object: " + Message.message());

        Uri messageUri = addNewMessage(Message);

        RestMethod<Message> method = new PostMessagesRestMethod(mContext, Message);
        Log.d(TAG, "Executing post method");
        RestMethodResult<Message> result = null;
//        try {
            result = method.execute();
//        } catch (UnknownHostException e) {
//            Log.e(TAG, "Internet error: " + e.getLocalizedMessage());
//        }

        updateNewMessage(messageUri, result);

        callback.send(result.getStatusCode(), messageUri.getLastPathSegment());

    }

    private void updateNewMessage(Uri messageUri, RestMethodResult<Message> result) {
        ContentValues values;
        ContentResolver cr = this.mContext.getContentResolver();
        if (result.getResource() != null) {
            values = result.getResource().toContentValues();
        } else {
            values = new ContentValues();
        }

        values.put(MessagesTable._STATUS, RESOURCE_TRANSACTION_FLAG.COMPLETE);
        values.put(MessagesTable._RESULT, result.getStatusCode());
        cr.update(messageUri, values, null, null);
    }

    private Uri addNewMessage(Message Message) {
        ContentResolver cr = this.mContext.getContentResolver();

        ContentValues values = new ContentValues();
        values.put(MessagesTable.USERNAME, Message.username());
        values.put(MessagesTable.MESSAGE, Message.message());
        values.put(MessagesTable.DATETIME, Message.datetime());

        return cr.insert(Messages.CONTENT_URI, values);
    }

    private String getNewestMessageId() {
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(Messages.CONTENT_URI, new String[] { MessagesTable.REF_ID }, null, null, MessagesTable.REF_ID + " DESC LIMIT 1");

        if (cursor.moveToNext()) {
            String id = cursor.getString(0);
//            cursor.close();
            return id;
        } else {
            return null;
        }
    }

    private void updateContentProvider(RestMethodResult<Messages> result) {
        Messages messages = result.getResource();
        List<Message> messageList = null;

        try {
            messageList = messages.getMessages();
        } catch (NullPointerException e) {
            Log.d(TAG, "couldn't get messages");
        }

        ContentResolver cr = this.mContext.getContentResolver();

        // insert/update row for each cat picture in the list
        for (Message Message : messageList) {
            cr.insert(Messages.CONTENT_URI, Message.toContentValues());
        }
    }

    @Override
    public void putResource(ResourceProcessorCallback callback, Bundle params) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteResource(ResourceProcessorCallback callback, Bundle params) {
        // TODO Auto-generated method stub

    }
}
