package com.macinsiders.chat.rest.method;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.macinsiders.chat.resource.Login;
import com.macinsiders.chat.rest.Request;
import com.macinsiders.chat.rest.method.RestMethodFactory.Method;

public class PostLoginRestMethod extends AbstractRestMethod<Login> {

    private static final String TAG = PostLoginRestMethod.class.getSimpleName();

    private Context mContext;

    private static final String BASE_URI = "http://www.macinsiders.com/login.php";

    private static final String PARAM_KEY_USERNAME = "vb_login_username";
    private static final String PARAM_KEY_PASSWORD = "vb_login_password";
    private static final String PARAM_KEY_COOKIEUSER = "cookieuser";
    private static final String PARAM_VALUE_COOKIEUSER = "1";
    private static final String PARAM_KEY_DO = "do";
    private static final String PARAM_VALUE_DO = "login";

    private URI mUri;

    private Login mLogin;

    public PostLoginRestMethod(Context context, Login login) {
        mContext = context.getApplicationContext();
        mLogin = login;
        mUri = buildUri();
    }

    @Override
    protected Request buildRequest() {
        Map<String, String> params = new HashMap<String, String>();
        params.put(PARAM_KEY_USERNAME, mLogin.getUsername());
        params.put(PARAM_KEY_PASSWORD, mLogin.getPassword());
        params.put(PARAM_KEY_COOKIEUSER, PARAM_VALUE_COOKIEUSER);
        params.put(PARAM_KEY_DO, PARAM_VALUE_DO);

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
        // // Log.d(TAG, responseBody);
        // // JSONObject body = new JSONObject(responseBody);
        // // JSONObject json = body.getJSONObject("json");
        // // JSONObject data = json.getJSONObject("data");
        // // String cookie = data.getString("cookie");
        // // String modhash = data.getString("modhash");
        // //
        // // mLogin.setCookie(cookie);
        // // mLogin.setModhash(modhash);
        //
        return mLogin;
    }

    @Override
    protected Login parseResponseCookies(Map<String, List<String>> headers) throws Exception {
        List<String> cookies = null;
        for (String str : headers.get("Set-Cookie")) {
            if (str.contains("bbpassword")) {
                cookies = headers.get("Set-Cookie");
            }
        }
        mLogin.setCookie(cookies);
        return mLogin;

    }

    @Override
    protected Context getContext() {
        return mContext;
    }

    @Override
    protected boolean requiresAuthorization() {
        return false;
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
