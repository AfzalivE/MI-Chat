package com.afzal.mi_chat.Utils;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;

public class NetUtils {

    public static AsyncHttpClient client = new AsyncHttpClient();
    private static PersistentCookieStore myCookieStore;

    public static synchronized PersistentCookieStore getCookieStoreInstance(Context context) {
        if (myCookieStore == null) {
            myCookieStore = new PersistentCookieStore(context);
        }
        return myCookieStore;
    }
}
