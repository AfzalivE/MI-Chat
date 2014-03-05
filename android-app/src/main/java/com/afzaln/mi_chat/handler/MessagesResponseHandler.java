package com.afzaln.mi_chat.handler;

import android.util.Log;

import com.afzaln.mi_chat.processor.ResourceProcessor;
import com.loopj.android.http.XmlHttpResponseHandler;

import org.apache.http.Header;
import org.w3c.dom.Document;

/**
 * Created by afzal on 12/19/2013.
 */
public class MessagesResponseHandler extends XmlHttpResponseHandler {
    private static final String TAG = MessagesResponseHandler.class.getSimpleName();
    ResourceProcessor mProcessor;

    public MessagesResponseHandler(ResourceProcessor processor) {
        mProcessor = processor;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, Document response) {
        Log.d(TAG, "onSuccess");
        mProcessor.updateContentProvider(response);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Document errorResponse, Throwable error) {
        Log.d(TAG, "onFailure");
        error.printStackTrace();
        // Response failed :(
    }

    @Override
    public void onFinish() {
        Log.d(TAG, "onFinish");
        // Completed the request (either success or failure)
    }
}
