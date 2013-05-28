package com.afzaln.mi_chat.resource;

import android.content.ContentValues;

import com.afzaln.mi_chat.provider.ProviderContract.UsersTable;

public class User {

    private static final String TAG = User.class.getSimpleName();

    Long userId;
    int userRole;
    int channelId;
    String userName;

    public User(long userId, int userRole, int channelId, String userName) {
        this.userId = userId;
        this.userRole = userRole;
        this.channelId = channelId;
        this.userName = userName;
    }

    public ContentValues toContentValues() {
        ContentValues rowData = new ContentValues();
        rowData.put(UsersTable.USERID, this.userId);
        rowData.put(UsersTable.USERROLE, this.userRole);
        rowData.put(UsersTable.CHANNELID, this.channelId);
        rowData.put(UsersTable.USERNAME, this.userName);
//        Log.d(TAG, "converting to ContentValues, user: " + this.userName);
        return rowData;
    }
}
