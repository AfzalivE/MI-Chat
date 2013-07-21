package com.afzaln.mi_chat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@linkplain CookieStore cookie jar} that only cares to store the cookies.
 * It persists the cookie through app launches and maintains it securely.
 *
 * @author Dandré Allison
 */
public class PersistentCookieStore implements CookieStore {

    private static final String TAG = PersistentCookieStore.class.getSimpleName();
    private final HashMap<String, HttpCookie> mCookies;
    private final SharedPreferences mCookiePrefs;
    private static final String COOKIE_PREFS = "CookiePrefsFile";
    private static final String COOKIE_NAME_STORE = "names";
    private static final String COOKIE_NAME_PREFIX = "cookie_";


    /* Constructor */
    public PersistentCookieStore(Context context) {
        mCookiePrefs = context.getSharedPreferences(COOKIE_PREFS, Context.MODE_PRIVATE);
        mCookies = new HashMap<String, HttpCookie>();

        String storedCookieNames = mCookiePrefs.getString(COOKIE_NAME_STORE, null);
        if (storedCookieNames != null) {
            String[] cookieNames = TextUtils.split(storedCookieNames, ",");
            for (String name : cookieNames) {
                String encodedCookie = mCookiePrefs.getString(COOKIE_NAME_PREFIX + name, null);
                if (encodedCookie != null) {
                    HttpCookie decodedCookie = decodeCookie(encodedCookie);
                    if (decodedCookie != null) {
                        mCookies.put(name, decodedCookie);
                    }
                }
            }
        }

        // Clear out expired cookies
        clearExpired();
    }

    /* Cookie Store */
    @Override
    public void add(URI uri, HttpCookie cookie) {
        Log.d(TAG, uri.toString());
        Log.v(TAG, "Adding Cookie: " + cookie);

        String name = cookie.getName() + cookie.getDomain();

        // Save cookie into local store or remove if expired
        if (!cookie.hasExpired()) {
            mCookies.put(name, cookie);
        } else {
            mCookies.remove(name);
        }

        // Stores the cookies in shared preferences
        final SharedPreferences.Editor editor = mCookiePrefs.edit();
        editor.putString(COOKIE_NAME_STORE, TextUtils.join(",", mCookies.keySet()));
        editor.putString(COOKIE_NAME_PREFIX + name, encodeCookie(new SerializableCookie(cookie)));
        editor.commit();
    }

    @Override
    public List<HttpCookie> getCookies() {
        Collection<HttpCookie> list = mCookies.values();
        return new ArrayList<HttpCookie>(list);
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        return new ArrayList<HttpCookie>();
    }

    @Override
    public List<URI> getURIs() {
        return new ArrayList<URI>();
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        String name = cookie.getName() + cookie.getDomain();
        mCookies.remove(name);
        final SharedPreferences.Editor editor = mCookiePrefs.edit();
        editor.putString(COOKIE_NAME_STORE, TextUtils.join(",", mCookies.keySet()));
        editor.remove(COOKIE_NAME_PREFIX + name);
        return editor.commit();
    }

    @Override
    public boolean removeAll() {
        // Clear cookies from Shared Preferences
        final SharedPreferences.Editor editor = mCookiePrefs.edit();
        for (String name : mCookies.keySet()) {
            editor.remove(COOKIE_NAME_PREFIX + name);
        }
        editor.remove(COOKIE_NAME_STORE);
        boolean success = editor.commit();

        // Clear cookies from local store
        mCookies.clear();

        return success;
    }

    public boolean clearExpired() {
        boolean clearedAny = false;
        final Editor editor = mCookiePrefs.edit();

        for (HashMap.Entry<String, HttpCookie> entry : mCookies.entrySet()) {
            String name = entry.getKey();
            HttpCookie cookie = entry.getValue();
            if (cookie.hasExpired()) {
                // Clear cookie from local store
                mCookies.remove(name);

                // Clear cookie from Shared Preferences
                editor.remove(COOKIE_NAME_PREFIX + name);

                // We've cleared at least one
                clearedAny = true;
            }
        }

        // Update names in Shared Preferences
        if (clearedAny) {
            editor.putString(COOKIE_NAME_STORE, TextUtils.join(",", mCookies.keySet()));
        }
        editor.commit();

        return clearedAny;
    }

    protected String encodeCookie(SerializableCookie cookie) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(cookie);
        } catch (Exception e) {
            return null;
        }

        return byteArrayToHexString(os.toByteArray());
    }

    protected HttpCookie decodeCookie(String cookieStr) {
        byte[] bytes = hexStringToByteArray(cookieStr);
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        HttpCookie cookie = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(is);
            cookie = ((SerializableCookie)ois.readObject()).getCookie();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cookie;
    }

    // Using some super basic byte array <-> hex conversions so we don't have
    // to rely on any large Base64 libraries. Can be overridden if you like!
    protected String byteArrayToHexString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (byte element : b) {
            int v = element & 0xff;
            if(v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }

    protected byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for(int i=0; i<len; i+=2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}