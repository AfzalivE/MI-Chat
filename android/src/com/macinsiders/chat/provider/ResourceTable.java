package com.macinsiders.chat.provider;

import android.provider.BaseColumns;

public interface ResourceTable extends BaseColumns {

    /*
     * The current transaction status. May be null if no current transaction for
     * this resource <P>Type: RESOURCE_TRANSACTION enum (STRING)</P>
     */
    public static final String _STATUS = "_status";

    /*
     * The transaction result code, typically an http status. <P>Type: INTEGER
     * </P>
     */
    public static final String _RESULT = "_result";

}
