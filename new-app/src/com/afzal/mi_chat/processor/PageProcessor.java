package com.afzal.mi_chat.processor;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.afzal.mi_chat.provider.ProviderContract.MessagesTable;
import com.afzal.mi_chat.provider.ProviderContract.UsersTable;
import com.afzal.mi_chat.resource.Message;
import com.afzal.mi_chat.resource.Page;
import com.afzal.mi_chat.resource.User;
import com.afzal.mi_chat.utils.NetUtils;
import com.loopj.android.http.XmlHttpResponseHandler;

public class PageProcessor implements ResourceProcessor {

    private XmlHttpResponseHandler myResponseHandler = new XmlHttpResponseHandler() {
        @Override
        public void onStart() {
            Log.d(TAG, "onStart");
        }

        @Override
        public void onSuccess(Document response) {
            Log.d(TAG, "onSuccess");
            try {
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer t;
                t = tf.newTransformer();
                Source src = new DOMSource(response);
                OutputStream out = new ByteArrayOutputStream();
                Result res = new StreamResult(out);
                t.transform(src, res);
                Log.d(TAG, out.toString());

                updateContentProvider(response);

//                mCallback.send(200, null);

            } catch (TransformerConfigurationException e1) {
            } catch (TransformerException e) {}
        }

        @Override
        public void onFailure(Throwable e, Document response) {
            Log.d(TAG, "onFailure");
            e.printStackTrace();
            // Response failed :(
        }

        @Override
        public void onFinish() {
            Log.d(TAG, "onFinish");
            // Completed the request (either success or failure)
        }
    };

    protected static final String TAG = PageProcessor.class.getSimpleName();
    private Context mContext;

    public PageProcessor(Context context) {
        mContext = context;
    }

    @Override
    public void getResource() {
        // get the last id
        long lastId = getLastMessageId();
        // call get with the last id
        NetUtils.getPage(myResponseHandler, lastId);
    }

    private long getLastMessageId() {
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(MessagesTable.CONTENT_URI, new String[] { MessagesTable.MESSAGEID } , null, null, MessagesTable.DATETIME + " DESC LIMIT 1");

        if (cursor.moveToNext()) {
            Log.d(TAG, Long.toString(cursor.getLong(0)));
            long lastId = cursor.getLong(0);
            Log.d(TAG, "Last ID: " + Long.toString(lastId));
            return lastId;
        } else {
            return -1;
        }
    }

    private void updateContentProvider(Document result) {
        Page page = new Page(result);
        List<User> userList = null;
        List<Message> messageList = null;

        try {
            userList = page.getUserList();
            messageList = page.getMessageList();
        } catch (NullPointerException e) {
            Log.d(TAG, "couldn't get user list");
        }

        ContentResolver cr = this.mContext.getContentResolver();

        cr.delete(UsersTable.CONTENT_URI, null, null);
        for (User user : userList) {
            cr.insert(UsersTable.CONTENT_URI, user.toContentValues());
        }

        for (Message message : messageList) {
            cr.insert(MessagesTable.CONTENT_URI, message.toContentValues());
        }

    }

    @Override
    public void postResource(ResourceProcessorCallback callback, Bundle params) {}

    @Override
    public void putResource(ResourceProcessorCallback callback, Bundle params) {}

    @Override
    public void deleteResource(ResourceProcessorCallback callback, Bundle params) {}

}
