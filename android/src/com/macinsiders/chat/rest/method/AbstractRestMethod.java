package com.macinsiders.chat.rest.method;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.macinsiders.chat.resource.Login;
import com.macinsiders.chat.resource.Resource;
import com.macinsiders.chat.rest.DefaultRestClient;
import com.macinsiders.chat.rest.Request;
import com.macinsiders.chat.rest.Response;
import com.macinsiders.chat.rest.RestClient;
import com.macinsiders.chat.security.LoginManager;

public abstract class AbstractRestMethod<T extends Resource> implements RestMethod<T> {

    private static final String TAG = AbstractRestMethod.class.getSimpleName();
    private static final String DEFAULT_ENCODING = "UTF-8";
    private RestClient mRestClient;

    public RestMethodResult<T> execute() {

        Request request = buildRequest();
        if (requiresAuthorization()) {
            LoginManager loginManager = new LoginManager(getContext());
            authorize(request, loginManager.getLogin());
        }

        Response response = doRequest(request);
        return buildResult(response);

    }

    private void authorize(Request request, Login login) {

        if (request != null && login != null) {

            List<String> cookie = new ArrayList<String>();
            cookie.add("reddit_session=" + login.getCookies());
            request.addHeader("Cookie", cookie);

            String body = new String(request.getBody());
            body += "&uh=" + login.getModHash();
            request.setBody(body.getBytes());
        }
    }

    public void setRestClient(RestClient client) {
        mRestClient = client;
    }

    protected abstract Context getContext();

    /**
     * Subclasses can overwrite for full control, eg. need to do special
     * inspection of response headers, etc.
     * 
     * @param response
     * @return
     */
    protected RestMethodResult<T> buildResult(Response response) {

        int status = response.status;
        String statusMsg = "";
        String responseBody = null;
        T cookies = null; 
        T resource = null;

        try {
            responseBody = new String(response.body, getCharacterEncoding(response.headers));
            logResponse(status, responseBody);
            resource = parseResponseBody(responseBody);
            cookies = parseResponseCookies(response.headers);
        } catch (Exception ex) {
            // our own internal error code, not from service
            // spec only defines up to 505
            status = 506;
            statusMsg = ex.getMessage();
        }
        return new RestMethodResult<T>(status, statusMsg, cookies, resource);
    }

    protected abstract URI getURI();

    protected String buildQueryString(Map<String, String> params) {

        StringBuilder queryStringBuilder = new StringBuilder();

        Iterator<String> paramKeyIter = params.keySet().iterator();

        if (paramKeyIter.hasNext()) {
            queryStringBuilder.append(getKeyValuePair(params, paramKeyIter.next()));
        }

        while (paramKeyIter.hasNext()) {
            queryStringBuilder.append("&" + getKeyValuePair(params, paramKeyIter.next()));
        }

        return queryStringBuilder.toString();
    }

    private String getKeyValuePair(Map<String, String> params, String key) {
        return key + "=" + params.get(key);
    }

    /**
     * Returns the log tag for the class extending AbstractRestMethod
     * 
     * @return log tag
     */
    protected abstract String getLogTag();

    /**
     * Build the {@link Request}.
     * 
     * @return Request for this REST method
     */
    protected abstract Request buildRequest();

    /**
     * Determines whether the REST method requires authentication
     * 
     * @return <code>true</code> if authentication is required,
     *         <code>false</code> otherwise
     */
    protected boolean requiresAuthorization() {
        return false;
    }

    protected abstract T parseResponseBody(String responseBody) throws Exception;
    protected abstract T parseResponseCookies(Map<String, List<String>> headers) throws Exception;

    protected Response doRequest(Request request) {

        if (mRestClient == null) {
            mRestClient = new DefaultRestClient();
        }

        logRequest(request);
        return mRestClient.execute(request);
    }

    private String getCharacterEncoding(Map<String, List<String>> headers) {     
        String encoding = headers.get("Content-Type").get(0).split("=")[1];
        
        Log.d(TAG, encoding);
//        return DEFAULT_ENCODING;
        return encoding;
    }

    private void logRequest(Request request) {
        Log.d(TAG, "Request: " + request.getMethod().toString() + " " + request.getRequestUri().toASCIIString());
    }

    private void logResponse(int status, String responseBody) {
        // truncated response body because page source is huge
        Log.d(TAG, "Response: status=" + status + ", body=" + responseBody.substring(0, 50));
    }
}
