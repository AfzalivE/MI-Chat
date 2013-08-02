package com.afzaln.mi_chat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.koushikdutta.ion.Ion;

import java.net.HttpCookie;
import java.util.List;

public class PrefUtils {

    private static final String TAG = PrefUtils.class.getSimpleName();
    private static final String COOKIE_AUTH_TOKEN = "bbpassword";
    private static final String COOKIE_PREFS = "CookiePrefsFile";
    private static final String COOKIE_NAME_PREFIX = "cookie_";
    private static final String COOKIE_NAME_STORE = "names";

    /**
     * Commits a preference in SharedPreferences.
     *
     * @param context     the context
     * @param stringName  the preference name
     * @param stringValue the value of the preference
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
     * @param context    the context
     * @param stringName the preference name
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
     * @param context the context
     */
    public static void clearPrefs(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
        prefsEditor.clear();
        prefsEditor.commit();
    }

    /**
     * Determines if the cookie named "bbpassword"
     * exists in the CookieStore
     *
     * @return true if auth cookie was found in the CookieStore
     */
    public static boolean authCookieExists(Context context) {
        List<HttpCookie> cookies = Ion.getDefault(context).getCookieMiddleware().getCookieStore().getCookies();
        for (HttpCookie cookie : cookies) {
            if (cookie.getName().equals(COOKIE_AUTH_TOKEN)) {
                    return true;
                }
            }
            return false;
        }

    public static void clearCookiePrefs(Context context) {
        // Clear cookies from Shared Preferences and local store
        Ion.getDefault(context).getCookieMiddleware().clear();
    }
}
