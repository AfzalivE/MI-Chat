package com.macinsiders.chat.rest.method;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.macinsiders.chat.resource.Login;
import com.macinsiders.chat.rest.Request;
import com.macinsiders.chat.rest.method.RestMethodFactory.Method;

public class PostLogoutRestMethod extends AbstractRestMethod<Login> {

    private static final String TAG = PostLogoutRestMethod.class.getSimpleName();

    private Context mContext;

    private static final String BASE_URI = "http://www.macinsiders.com/chat/?ajax=true";

    private static final String PARAM_KEY_LOGOUT = "logout";
    private static final String PARAM_VALUE_LOGOUT = "true";

    private URI mUri;

//    private Login mLogin;

    public PostLogoutRestMethod(Context context) {
        mContext = context.getApplicationContext();
//        mLogin = login;
        mUri = buildUri();
    }

    @Override
    protected Request buildRequest() {
        Map<String, String> params = new HashMap<String, String>();
        params.put(PARAM_KEY_LOGOUT, PARAM_VALUE_LOGOUT);

        String body = buildQueryString(params);

        Request request = new Request(Method.POST, mUri, null, body.getBytes());
        return request;
    }

    private URI buildUri() {
        String uriString = BASE_URI;
        return URI.create(uriString);
    }

    @Override
    protected Login parseResponseBody(String responseBody) throws Exception {
        Log.d(TAG, responseBody);
        // TODO deal with chat logout
        return null;
    }

    @Override
    protected Login parseResponseCookies(Map<String, List<String>> headers) throws Exception {
        return null;
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

}
