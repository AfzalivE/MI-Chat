package com.macinsiders.chat.rest.method;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.UriMatcher;
import android.net.Uri;

import com.macinsiders.chat.provider.ProviderContract;
import com.macinsiders.chat.provider.ProviderContract.MessagesTable;

public class RestMethodFactory {

	private static RestMethodFactory instance;
	private static Object lock = new Object();
	private UriMatcher uriMatcher;
	private Context mContext;

	private static final int MESSAGES = 1;

	private RestMethodFactory(Context context) {
		mContext = context.getApplicationContext();
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(ProviderContract.AUTHORITY, MessagesTable.TABLE_NAME, MESSAGES);
	}

	public static RestMethodFactory getInstance(Context context) {
		synchronized (lock) {
			if (instance == null) {
				instance = new RestMethodFactory(context);
			}
		}

		return instance;
	}

	public RestMethod getRestMethod(Uri resourceUri, Method method,
			Map<String, List<String>> headers, byte[] body) {

		switch (uriMatcher.match(resourceUri)) {
		case MESSAGES:
			if (method == Method.GET) {
				return new GetMessagesRestMethod(mContext, null);
			}
			break;
		}

		return null;
	}

	public static enum Method {
		GET, POST, PUT, DELETE
	}

}
