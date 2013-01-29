package com.macinsiders.chat.rest.method;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.macinsiders.chat.resource.Message;
import com.macinsiders.chat.rest.Request;
import com.macinsiders.chat.rest.method.RestMethodFactory.Method;

public class PostMessagesRestMethod extends AbstractRestMethod<Message> {

    private static final String TAG = PostMessagesRestMethod.class.getSimpleName();

    private Context mContext;

    private static final String BASE_URI = "http://www.macinsiders.com/chat/?ajax=true";
    private static final String PARAM_NAME = "username";
    private static final String PARAM_MESSAGE = "message";
    private static final String PARAM_DATETIME = "datetime";

    private Message mMessage;

    private URI mUri;

    public PostMessagesRestMethod(Context context, Message Message) {
        mContext = context.getApplicationContext();
        mMessage = Message;
        Log.d(TAG, "executing post rest method for: " + Message.message());
        mUri = buildUri();
    }

    @Override
    protected Request buildRequest() {
        Log.d(TAG, "Building request");
        Map<String, String> params = new HashMap<String, String>();

        params.put(PARAM_NAME, mMessage.user());
        params.put(PARAM_MESSAGE, mMessage.message());
        params.put(PARAM_DATETIME, Long.toString(mMessage.datetime()));

        String body = buildQueryString(params);
        Log.d(TAG, "Query formed: " + body);

        Request request = new Request(Method.POST, mUri, null, body.getBytes());
        Log.d(TAG, request.toString());

        return request;
    }

    private URI buildUri() {
        String uriString = BASE_URI;
        return URI.create(uriString);
    }

    @Override
    protected Message parseResponseBody(String responseBody) throws Exception {
        Log.d(TAG, responseBody);
        // also the serverResponse
        // TODO Change this to deal with XML data instead of JSON 
        JSONArray eventsArray = new JSONArray(responseBody);

        return new Message((JSONObject) eventsArray.get(0));
    }

    @Override
    protected Context getContext() {
        return mContext;
    }

    @Override
    protected boolean requiresAuthorization() {
        return true;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    protected URI getURI() {
        return mUri;
    }

    @Override
    protected Message parseResponseCookies(Map<String, List<String>> headers) throws Exception {
        // No reason to do anything here yet
        return null;
    }

}
