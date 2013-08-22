package com.afzaln.mi_chat.utils;

import android.content.Context;

import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

/** Created by afzal on 2013-07-17. */
// TODO a more elegant name maybe
public class MIChatApi {
    private static final String TAG = MIChatApi.class.getSimpleName();

    public static final String BASE_URI = "http://www.macinsiders.com/chat/?ajax=true";
    public static final String LOGIN_URI = "http://www.macinsiders.com/login.php";

    public static Future<Response<String>> login(Context context, FutureCallback<Response<String>> callback, String username, String password) {
        return Ion.with(context, LOGIN_URI)
                .setBodyParameter("vb_login_username", username)
                .setBodyParameter("vb_login_password", password)
                .setBodyParameter("do", "login")
                .setBodyParameter("cookieuser", "1")
                .asString()
                .withResponse()
                .setCallback(callback);
    }

    public static Future<Response<String>> login(Context context, FutureCallback<Response<String>> callback) {
        return Ion.with(context)
                .load("POST", LOGIN_URI)
                .asString()
                .withResponse()
                .setCallback(callback);
    }

    public static Future<Response<String>> logout(Context context, FutureCallback<Response<String>> callback) {
        return Ion.with(context, BASE_URI)
                .setBodyParameter("logout", "true")
                .asString()
                .withResponse()
                .setCallback(callback);
    }

    public static Future<Response<String>> getPage(Context context, long lastId, FutureCallback<Response<String>> callback) {
        return Ion.with(context, getUrl(lastId))
                .asString()
                .withResponse()
                .setCallback(callback);
    }

    public static Future<Response<String>> postMessage(Context context, String message, long lastId, FutureCallback<Response<String>> callback) {
        return Ion.with(context, BASE_URI)
                  .setBodyParameter("text", message)
                  .setBodyParameter("lastID", Long.toString(lastId))
                  .asString()
                  .withResponse()
                  .setCallback(callback);
    }

    private static String getUrl(long lastId) {
        return (lastId != -1 ? BASE_URI + "&lastID=" + Long.toString(lastId) : BASE_URI);
    }
}
