package com.afzal.mi_chat.Utils;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;

public class NetUtils {

    static HttpTransport mTransport;

    public static HttpTransport getTransport() {
        if (mTransport == null) {
            mTransport = AndroidHttp.newCompatibleTransport();
        }

        return mTransport;
    }

}
