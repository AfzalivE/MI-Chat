package com.afzal.mi_chat.Utils;

import org.apache.http.client.params.ClientPNames;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;

public class NetUtils {

    public static AsyncHttpClient client;
    private static PersistentCookieStore myCookieStore;

    public static synchronized AsyncHttpClient getClientInstance() {
        if (client == null) {
            client = new AsyncHttpClient();
        }

        client.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        return client;
    }

    public static synchronized PersistentCookieStore getCookieStoreInstance(Context context) {
        if (myCookieStore == null) {
            myCookieStore = new PersistentCookieStore(context);
        }
        return myCookieStore;
    }
}
