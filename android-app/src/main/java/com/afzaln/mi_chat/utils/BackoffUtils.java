package com.afzaln.mi_chat.utils;

import java.util.Calendar;

/**
 * Created by afzal on 2013-08-01.
 */
public class BackoffUtils {

    public static final int DEFAULT_REFRESH_INTERVAL = 3000;

    /**
     * Updates the refresh interval
     * <p/>
     * < 60 sec old = DEFAULT_REFRESH_INTERVAL
     * > 60 sec old = 5 sec interval
     * > 120 sec old = 10 sec interval
     * > 150 sec old = 20 sec interval
     * > 180 sec old = 40 sec interval
     */
    public static int getRefreshInterval(boolean newMessagesExist, long latestItem) {
        // Only increase interval if there are new messages
        if (!newMessagesExist) {
            long currentTime = Calendar.getInstance().getTimeInMillis();

            if (currentTime - latestItem > 60000) {
                return 5000;
            } else if (currentTime - latestItem > 120000) {
                return 10000;
            } else if (currentTime - latestItem > 150000) {
                return 20000;
            } else if (currentTime - latestItem > 180000) {
                return 40000;
            }
        }

        // Otherwise reset the interval
        return DEFAULT_REFRESH_INTERVAL;
    }
}
