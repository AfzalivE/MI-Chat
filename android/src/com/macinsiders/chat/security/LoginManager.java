package com.macinsiders.chat.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.macinsiders.chat.resource.Login;

public class LoginManager {

	private static final String TAG = LoginManager.class.getSimpleName();
	
	private SharedPreferences mSharedPreferences;

	public LoginManager(Context context) {
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context
				.getApplicationContext());
	}

	public Login getLogin() {
		String username = mSharedPreferences.getString(Login.KEY_USERNAME, null);
		String cookie = mSharedPreferences.getString(Login.KEY_COOKIE, null);
		String modhash = mSharedPreferences.getString(Login.KEY_MODHASH, null);

		Login login = null;
		
		try {
			login = new Login(username, cookie, modhash);
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "User not logged in");
		}
		
		return login;
	}

	public void save(Login login) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();

		String username = null;
		String cookie = null;
		String modhash = null;
		
		if (login != null) {
			username = login.getUsername();
			cookie = login.getCookie();
			modhash = login.getModHash();
		}
		
		editor.putString(Login.KEY_USERNAME, username);
		editor.putString(Login.KEY_COOKIE, cookie);
		editor.putString(Login.KEY_MODHASH, modhash);
		editor.commit();
	}

	public boolean isLoggedIn() {
		return getLogin() != null;
	}
	
	public void logout() {
		save(null);
	}
}
