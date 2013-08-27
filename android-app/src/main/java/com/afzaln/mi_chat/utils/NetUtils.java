package com.afzaln.mi_chat.utils;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.XmlHttpResponseHandler;

import org.apache.http.client.params.ClientPNames;

public class NetUtils {

    private static final String BASE_URI = "http://www.macinsiders.com/chat/?ajax=true";
    private static final String LOGIN_URI = "http://www.macinsiders.com/login.php";

    public static AsyncHttpClient mClient;
    private static PersistentCookieStore mCookieStore;

    public static synchronized AsyncHttpClient getClientInstance() {
        if (mClient == null) {
            mClient = new AsyncHttpClient();
            mClient.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        }

        return mClient;
    }

    public static synchronized PersistentCookieStore getCookieStoreInstance(Context context) {
        if (mCookieStore == null) {
            mCookieStore = new PersistentCookieStore(context);
        }
        return mCookieStore;
    }

    public static void getPage(XmlHttpResponseHandler myResponseHandler, long lastId) {
        String uri = BASE_URI;
        if (lastId != -1) {
            uri = uri + "&lastID=" + Long.toString(lastId);
        }
        getClientInstance().get(uri, myResponseHandler);
    }

    public static void postMessage(XmlHttpResponseHandler myResponseHandler, String message, long lastId) {
        String uri = BASE_URI;
        RequestParams params = new RequestParams();
        params.put("text", message);
        params.put("lastID", Long.toString(lastId));
        getClientInstance().post(uri, params, myResponseHandler);
    }

    public static void postLogin(AsyncHttpResponseHandler myResponseHandler, Context context, String username, String password) {
        String uri = LOGIN_URI;
        AsyncHttpClient client = getClientInstance();
        client.setCookieStore(getCookieStoreInstance(context));
        if (username != null && password != null) {
            RequestParams params = new RequestParams();
            params.put("vb_login_username", username);
            params.put("vb_login_password", password);
            params.put("do", "login");
            params.put("cookieuser", "1");
            client.post(uri, params, myResponseHandler);
        } else {
            client.post(uri, myResponseHandler);
        }
    }

    public static void postLogout(XmlHttpResponseHandler myResponseHandler) {
        String uri = BASE_URI;
        RequestParams params = new RequestParams();
        params.put("logout", "true");
        getClientInstance().post(uri, params, myResponseHandler);
    }
}
