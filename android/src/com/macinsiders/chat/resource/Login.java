package com.macinsiders.chat.resource;

import java.util.List;

public class Login implements Resource {

    public static final String KEY_USERNAME = "username";
    public static final String KEY_COOKIE = "cookie";

    private String mUsername;
    private String mPassword;
    private List<String> mCookies;

    public Login(String username, String password) {
        mUsername = username;
        mPassword = password;
    }

    public Login(String username, List<String> cookies) {

        if (username == null) {
            throw new IllegalArgumentException("username is null");
        }
        if (cookies == null) {
            throw new IllegalArgumentException("cookies is null");
        }
        
        mUsername = username;
        mCookies = cookies;

    }

    public String getUsername() {
        return mUsername;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setCookie(List<String> cookies) {
        mCookies = cookies;
    }

    public List<String> getCookies() {
        return mCookies;
    }
}
