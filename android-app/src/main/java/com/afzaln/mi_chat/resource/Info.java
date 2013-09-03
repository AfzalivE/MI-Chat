package com.afzaln.mi_chat.resource;

import android.content.ContentValues;

import com.afzaln.mi_chat.provider.ProviderContract.InfoTable;

public class Info {
    private static final String TAG = Info.class.getSimpleName();
    Long userId;
    int userRole;
    int channelId;
    String userName;
    String channelName;
    boolean loggedIn;

    public Info(long userId, int userRole, int channelId, String userName, String channelName, boolean loggedIn) {
        this.userId = userId;
        this.userRole = userRole;
        this.channelId = channelId;
        this.userName = userName;
        this.channelName = channelName;
        this.loggedIn = loggedIn;
    }

    public boolean isUserLoggedIn() {
        return this.loggedIn;
    }

    public ContentValues toContentValues() {
        ContentValues rowData = new ContentValues();
        rowData.put(InfoTable.USERID, this.userId);
        rowData.put(InfoTable.USERROLE, this.userRole);
        rowData.put(InfoTable.CHANNELID, this.channelId);
        rowData.put(InfoTable.USERNAME, this.userName);
        rowData.put(InfoTable.CHANNELNAME, this.channelName);
//        Log.d(TAG, "converting to ContentValues, info: " + this.userName.toString());
        return rowData;
    }
}