package com.macinsiders.chat.resource;


public class Login implements Resource {

	public static final String KEY_USERNAME = "username";
	public static final String KEY_COOKIE = "cookie";
	public static final String KEY_MODHASH = "modhash";

	private String mUsername;
	private String mPassword;
	private String mCookie;
	private String mModhash;

	public Login(String username, String password) {
		mUsername = username;
		mPassword = password;
	}

	public Login(String username, String cookie, String modhash) {
		
		if (username == null || cookie == null || modhash == null) {
			throw new IllegalArgumentException("null argument");
		}
		
		mUsername = username;
		mCookie = cookie;
		mModhash = modhash;
	}

	public String getUsername() {
		return mUsername;
	}

	public String getPassword() {
		return mPassword;
	}

	public void setCookie(String cookie) {
		mCookie = cookie;
	}

	public String getCookie() {
		return mCookie;
	}

	public void setModhash(String modhash) {
		mModhash = modhash;
	}

	public String getModHash() {
		return mModhash;
	}
}
