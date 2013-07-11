package com.afzaln.mi_chat.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class ProviderContract {
    public static final String AUTHORITY = "com.afzaln.mi_chat.provider";

    public static final class InfoTable implements BaseColumns {
        // Info table name
        public static final String TABLE_NAME = "info";

        // URI DEFS
        static final String SCHEME = "content://";
        public static final String URI_PREFIX = SCHEME + AUTHORITY;
        private static final String URI_PATH_INFO = "/" + TABLE_NAME;

        // Note the slash on the end of this one, as opposed to the
        // URI_PATH_INFO, which has no slash.
        private static final String URI_PATH_INFO_ID = "/" + TABLE_NAME + "/";

        public static final int INFO_ID_PATH_POSITION = 1;

        // content://com.afzaln.restclient.provider/messages
        public static final Uri CONTENT_URI = Uri.parse(URI_PREFIX + URI_PATH_INFO);

        // content://com.afzaln.restclient.provider/messages/
        // for content provider insert() call
        public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + URI_PATH_INFO + "/");

        // content://com.afzaln.restclient.messagesprovider/messages/#
        public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME + AUTHORITY + URI_PATH_INFO + "/#");

        //        public static final String[] ALL_COLUMNS;
        //        public static final String[] DISPLAY_COLUMNS;
        public static final String[] COLUMNS;

        static {
            COLUMNS = new String[] {
                    // local Id
                    InfoTable._ID,
                    InfoTable.USERID,
                    InfoTable.USERROLE,
                    InfoTable.CHANNELID,
                    InfoTable.USERNAME,
                    InfoTable.CHANNELNAME };
        }

        // Prevent instantiation of this class
        private InfoTable() {
        }

        public static final String USERID = "userId";
        public static final String USERROLE = "userRole";
        public static final String CHANNELID = "channelId";
        public static final String USERNAME = "userName";
        public static final String CHANNELNAME = "channelName";

    }

    public static final class UsersTable implements BaseColumns {
        // Info table name
        public static final String TABLE_NAME = "users";

        // URI DEFS
        static final String SCHEME = "content://";
        public static final String URI_PREFIX = SCHEME + AUTHORITY;
        private static final String URI_PATH_USERS = "/" + TABLE_NAME;

        // Note the slash on the end of this one, as opposed to the
        // URI_PATH_INFO, which has no slash.
        private static final String URI_PATH_USER_ID = "/" + TABLE_NAME + "/";

        public static final int USER_ID_PATH_POSITION = 1;

        // content://com.afzaln.restclient.provider/messages
        public static final Uri CONTENT_URI = Uri.parse(URI_PREFIX + URI_PATH_USERS);

        // content://com.afzaln.restclient.provider/messages/
        // for content provider insert() call
        public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + URI_PATH_USERS + "/");

        // content://com.afzaln.restclient.messagesprovider/messages/#
        public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME + AUTHORITY + URI_PATH_USERS + "/#");

        //        public static final String[] ALL_COLUMNS;
        //        public static final String[] DISPLAY_COLUMNS;
        public static final String[] COLUMNS;

        static {
            COLUMNS = new String[] {
                    // local Id
                    UsersTable._ID,
                    UsersTable.USERID,
                    UsersTable.USERROLE,
                    UsersTable.CHANNELID,
                    UsersTable.USERNAME};
        }

        // Prevent instantiation of this class
        private UsersTable() {
        }

        public static final String USERID = "userId";
        public static final String USERROLE = "userRole";
        public static final String CHANNELID = "channelId";
        public static final String USERNAME = "userName";

    }

    public static final class MessagesTable implements BaseColumns {
        // Info table name
        public static final String TABLE_NAME = "messages";

        // URI DEFS
        static final String SCHEME = "content://";
        public static final String URI_PREFIX = SCHEME + AUTHORITY;
        private static final String URI_PATH_MESSAGES = "/" + TABLE_NAME;

        // Note the slash on the end of this one, as opposed to the
        // URI_PATH_INFO, which has no slash.
        private static final String URI_PATH_MESSAGES_ID = "/" + TABLE_NAME + "/";

        public static final int MESSAGE_ID_PATH_POSITION = 1;

        // content://com.afzaln.restclient.provider/messages
        public static final Uri CONTENT_URI = Uri.parse(URI_PREFIX + URI_PATH_MESSAGES);

        // content://com.afzaln.restclient.provider/messages/
        // for content provider insert() call
        public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + URI_PATH_MESSAGES + "/");

        // content://com.afzaln.restclient.messagesprovider/messages/#
        public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME + AUTHORITY + URI_PATH_MESSAGES + "/#");

        // public static final String[] ALL_COLUMNS;
        public static final String[] COLUMNS;
        public static final String[] DISPLAY_COLUMNS;

        static {
            COLUMNS = new String[] {
                    // local Id
                    MessagesTable._ID,
                    MessagesTable.MESSAGEID,
                    MessagesTable.TYPE,
                    MessagesTable.DATETIME,
                    MessagesTable.USERID,
                    MessagesTable.USERROLE,
                    MessagesTable.CHANNELID,
                    MessagesTable.USERNAME,
                    MessagesTable.MESSAGE,
                    MessagesTable.IMGLINKS};

            DISPLAY_COLUMNS = new String[] {
                    MessagesTable._ID,
                    MessagesTable.TYPE,
                    MessagesTable.DATETIME,
                    MessagesTable.USERROLE,
                    MessagesTable.USERNAME,
                    MessagesTable.MESSAGE,
                    MessagesTable.IMGLINKS};
        }

        // Prevent instantiation of this class
        private MessagesTable() {
        }

        public static final String MESSAGEID = "messageId";
        public static final String TYPE = "type";
        public static final String DATETIME = "dateTime";
        public static final String USERID = "userId";
        public static final String USERROLE = "userRole";
        public static final String CHANNELID = "channelId";
        public static final String USERNAME = "userName";
        public static final String MESSAGE = "message";
        public static final String IMGLINKS = "imgLinks";

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
