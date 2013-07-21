package com.afzaln.mi_chat.utils;

import android.content.Context;

import com.koushikdutta.async.http.SimpleMiddleware;

import java.net.CookieManager;
import java.net.CookieStore;
import java.util.List;
import java.util.Map;

/** Created by afzal on 2013-07-20. */

public class MyCookieMiddleware extends SimpleMiddleware {
    CookieManager manager;

    public MyCookieMiddleware(Context context) {
        PersistentCookieStore cookieStore = new PersistentCookieStore(context);
        manager = new CookieManager(cookieStore, null);
    }

    public CookieStore getCookieStore() {
        return manager.getCookieStore();
    }

    public CookieManager getCookieManager() {
        return manager;
    }

    @Override
    public void onSocket(OnSocketData data) {
        try {
            Map<String, List<String>> cookies = manager.get(data.request.getUri(), data.request.getHeaders().getHeaders().toMultimap());
            data.request.getHeaders().addCookies(cookies);
        } catch (Exception e) {
        }
    }

    @Override
    public void onHeadersReceived(OnHeadersReceivedData data) {
        try {
            manager.put(data.request.getUri(), data.headers.getHeaders().toMultimap());
        } catch (Exception e) {
        }
    }
}