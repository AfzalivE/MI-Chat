package com.afzaln.mi_chat;

import android.app.Application;
import android.content.ContentResolver;

import com.afzaln.mi_chat.provider.ProviderContract.MessagesTable;
import com.afzaln.mi_chat.provider.ProviderContract.UsersTable;
import com.crashlytics.android.Crashlytics;

/**
 * Created by afzaln on 2013-05-22.
 */
public class MIChatApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.DEBUG_BUILD) {
            Crashlytics.start(this);
        }
        ContentResolver cr = getContentResolver();

        cr.delete(UsersTable.CONTENT_URI, null, null);
        cr.delete(MessagesTable.CONTENT_URI, null, null);
    }
}
