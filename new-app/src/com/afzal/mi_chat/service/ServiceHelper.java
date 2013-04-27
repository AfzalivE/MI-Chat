package com.afzal.mi_chat.service;

import com.afzal.mi_chat.Utils.NetUtils;
import com.loopj.android.http.XmlHttpResponseHandler;


public class ServiceHelper {
    private static final String TAG = ServiceHelper.class.getSimpleName();

    public static void getPage(XmlHttpResponseHandler myResponseHandler) {
        NetUtils.client.get("http://www.macinsiders.com/chat/?ajax=true", myResponseHandler);
    }
}
