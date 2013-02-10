package com.macinsiders.chat.provider;

import android.net.Uri;

public class ProviderContract {

    public static final String AUTHORITY = "com.macinsiders.chat.messagesprovider";

    public static final class MessagesTable implements ResourceTable {

        // Messages table contract
        public static final String TABLE_NAME = "messages";

        // URI DEFS
        static final String SCHEME = "content://";
        public static final String URI_PREFIX = SCHEME + AUTHORITY;
        private static final String URI_PATH_MESSAGES = "/" + TABLE_NAME;

        // Note the slash on the end of this one, as opposed to the
        // URI_PATH_MESSAGES, which has no slash.
        private static final String URI_PATH_MESSAGE_ID = "/" + TABLE_NAME + "/";

        public static final int MESSAGE_ID_PATH_POSITION = 1;

        // content://com.afzaln.restclient.provider/messages
        public static final Uri CONTENT_URI = Uri.parse(URI_PREFIX + URI_PATH_MESSAGES);

        // content://com.afzaln.restclient.provider/messages/
        // for content provider insert() call
        public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + URI_PATH_MESSAGES + "/");

        // content://com.afzaln.restclient.messagesprovider/messages/#
        public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME + AUTHORITY + URI_PATH_MESSAGES + "/#");

        public static final String[] ALL_COLUMNS;
        public static final String[] DISPLAY_COLUMNS;

        static {
            ALL_COLUMNS = new String[] {
                    MessagesTable._ID,
                    MessagesTable._STATUS,
                    MessagesTable._RESULT,
                    MessagesTable.REF_ID,
                    MessagesTable.USERID,
                    MessagesTable.CHANNELID,
                    MessagesTable.USERROLE,
                    MessagesTable.DATETIME,
                    MessagesTable.USERNAME,
                    MessagesTable.MESSAGE };

            DISPLAY_COLUMNS = new String[] {
                    MessagesTable._ID,
                    MessagesTable._STATUS,
                    MessagesTable.REF_ID,
                    MessagesTable.USERID,
                    MessagesTable.CHANNELID,
                    MessagesTable.USERROLE,
                    MessagesTable.DATETIME,
                    MessagesTable.USERNAME,
                    MessagesTable.MESSAGE };
        }

        public static final String REF_ID = "id";
        public static final String USERID = "userId";
        public static final String CHANNELID = "channelId";
        public static final String USERROLE = "userRole";
        public static final String DATETIME = "datetime";
        public static final String USERNAME = "username";
        public static final String MESSAGE = "message";

        // Prevent instantiation of this class
        private MessagesTable() {

        }

    }

    public class RESOURCE_TRANSACTION_FLAG {
        /**
         * The most recent transaction on this resource is a POST
         */
        public static final int POST = 1 << 0;
        /**
         * The most recent transaction on this resource is a PUT
         */
        public static final int PUT = 1 << 1;
        /**
         * The most recent transaction on this resource is a DELETE
         */
        public static final int DELETE = 1 << 2;
        /**
         * The most recent transaction on this resource is a GET
         */
        public static final int GET = 1 << 3;
        /**
         * The most recent transaction on this resource is still in progress
         */
        public static final int TRANSACTING = 1 << 4;
        /**
         * The most recent transaction on this resource is finished
         */
        public static final int COMPLETE = 1 << 5;
    }

    private ProviderContract() {
        // disallow instantiation
    }
}
