package com.afzaln.mi_chat.handler;

import android.util.Log;

import com.afzaln.mi_chat.processor.PageProcessor;
import com.afzaln.mi_chat.utils.PrefUtils;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

/**
 * Created by anajam on 8/26/13.
 */
public class AutoLoginResponseHandler extends TextHttpResponseHandler {

    private static final String TAG = AutoLoginResponseHandler.class.getSimpleName();
    PageProcessor mParent;

    public AutoLoginResponseHandler(PageProcessor parent) {
        mParent = parent;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String response) {
        if (PrefUtils.authCookieExists(mParent.getContext())) {
            mParent.getResource();
        } else {
            Log.d(TAG, "Couldn't log in");
        }
        Log.d(TAG, "onSuccess");
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String response, Throwable error) {
        Log.d(TAG, "onFailure");
    }

    @Override
    public void onFinish() {
        Log.d(TAG, "onFinish");
    }
}
