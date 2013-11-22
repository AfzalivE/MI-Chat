package com.afzaln.mi_chat.handler;

import android.content.Intent;

import com.afzaln.mi_chat.activity.LoginActivity;
import com.afzaln.mi_chat.activity.MessagesActivity;
import com.afzaln.mi_chat.utils.NetUtils;
import com.loopj.android.http.XmlHttpResponseHandler;

import org.apache.http.Header;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Created by anajam on 8/26/13.
 */
public class LogoutResponseHandler extends XmlHttpResponseHandler {

    MessagesActivity mActivity;

    public LogoutResponseHandler(MessagesActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onStart() {
//            Log.d(TAG, "onStart");
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, Document response) {
//            Log.d(TAG, "onSuccess");
        Node info = response.getElementsByTagName("info").item(0);
        if (info.getAttributes().getNamedItem("type").getNodeValue().equals("logout")) {
            NetUtils.getCookieStoreInstance(mActivity).clear();
            Intent i = new Intent(mActivity, LoginActivity.class);
            mActivity.finish();
            mActivity.startActivity(i);
        };
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Document errorResponse, Throwable error) {
//            Log.d(TAG, "onFailure");
        error.printStackTrace();
        // Response failed :(
    }

    @Override
    public void onFinish() {
//            Log.d(TAG, "onFinish");
        // Completed the request (either success or failure)
    }

}
