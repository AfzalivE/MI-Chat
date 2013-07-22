package com.afzaln.mi_chat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.apache.http.cookie.Cookie;

import java.util.List;

public class PrefUtils {

    /**
     * Commits a preference in SharedPreferences.
     *
     * @param context
     *            the context
     * @param stringName
     *            the preference name
     * @param stringValue
     *            the value of the preference
     */

    public static void setPref(Context context, String stringName, String stringValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
        prefsEditor.putString(stringName, stringValue);
        prefsEditor.commit();
    }

    /**
     * Returns the value of a preference in SharedPreferences.
     *
     * @param context
     *            the context
     * @param stringName
     *            the preference name
     * @return stringValue the value of the preference
     */

    public static boolean getBoolPref(Context context, String stringName) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean prefValue = sharedPrefs.getBoolean(stringName, false);
        return prefValue;
    }

    /**
     * Removes all preferences from SharedPreferences
     *
     * @param context
     *            the context
     */
    public static void clearPrefs(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
        prefsEditor.clear();
        prefsEditor.commit();
    }

    public static boolean authCookieExists(Context context) {
        List<Cookie> cookies = NetUtils.getCookieStoreInstance(context).getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("bbpassword")) {
                return true;
            }
        }
        return false;
    }
}
