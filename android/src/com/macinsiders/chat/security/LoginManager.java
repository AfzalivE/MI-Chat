package com.macinsiders.chat.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.common.base.Joiner;
import com.macinsiders.chat.resource.Login;

public class LoginManager {

    private static final String TAG = LoginManager.class.getSimpleName();

    private SharedPreferences mSharedPreferences;

    public LoginManager(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public Login getLogin() {
        String username = mSharedPreferences.getString(Login.KEY_USERNAME, null);

        if (username != null) {
            Log.d(TAG, username);
        }
        
        String cookies = mSharedPreferences.getString(Login.KEY_COOKIE, null);
        // Deserialize the cookies from String to List<String> to use it

        List<String> cookieList = new ArrayList<String>();

        if (cookies != null) {
            // TODO also works without splitting since the
            // string is in the format we put the "Cookie"
            // header value in
            Collections.addAll(cookieList, cookies.split(";"));
            Log.d(TAG, cookieList.toString());
        }

        Login login = null;

        try {
            login = new Login(username, cookieList);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "User not logged in");
            Log.d(TAG, e.getLocalizedMessage());
        }

        return login;
    }

    public void save(Login login) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        String username = null;
        String cookies = null;

        if (login != null) {
            username = login.getUsername();
            // Log.d(TAG, Joiner.on(";").join(login.getCookies()));
            cookies = Joiner.on(";").join(login.getCookies());
        }

        editor.putString(Login.KEY_USERNAME, username);
        editor.putString(Login.KEY_COOKIE, cookies);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return getLogin() != null;
    }

    public void logout() {
        // remove login details in SharedPreferences
        save(null);        
    }
}
