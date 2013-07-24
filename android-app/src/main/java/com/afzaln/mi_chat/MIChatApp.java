package com.afzaln.mi_chat;

import android.app.Application;
import android.content.ContentResolver;

import com.afzaln.mi_chat.provider.ProviderContract.MessagesTable;
import com.afzaln.mi_chat.provider.ProviderContract.UsersTable;

/**
 * Created by afzaln on 2013-05-22.
 */
public class MIChatApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ContentResolver cr = getContentResolver();

        cr.delete(UsersTable.CONTENT_URI, null, null);
        cr.delete(MessagesTable.CONTENT_URI, null, null);
    }
}
