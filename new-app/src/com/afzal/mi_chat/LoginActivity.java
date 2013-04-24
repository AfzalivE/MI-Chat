package com.afzal.mi_chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.afzal.mi_chat.Utils.MultipartFormDataContent;
import com.afzal.mi_chat.Utils.MultipartFormDataContent.Part;
import com.afzal.mi_chat.Utils.NetUtils;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.common.io.CharStreams;

public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private EditText mUsernameField;
    private EditText mPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        getWindow().setBackgroundDrawable(null);

        mUsernameField = (EditText) findViewById(R.id.username);

        mPasswordField = (EditText) findViewById(R.id.password);
        mPasswordField.setTypeface(Typeface.DEFAULT);
        mPasswordField.setTransformationMethod(new PasswordTransformationMethod());

        Button login = (Button) findViewById(R.id.login);

        login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                hideKeyboard();
                String username = mUsernameField.getText().toString();
                String password = mPasswordField.getText().toString();
                new LoginTask().execute(username, password);
                //                Intent i = new Intent(LoginActivity.this, MessagesActivity.class);
                //                startActivity(i);
            }
        });

    }

    public class LoginTask extends AsyncTask<String, Void, String> {

        private final String TAG = LoginTask.class.getSimpleName();

        @SuppressWarnings("unchecked")
        @Override
        protected String doInBackground(String... params) {
            Map<String, String> paramsMap = new HashMap<String, String>();
            paramsMap.put("vb_login_username", params[0]);
            paramsMap.put("vb_login_password", params[1]);
            paramsMap.put("do", "login");
            paramsMap.put("cookieuser", "1");

            String body = buildQueryString(paramsMap);

            HttpTransport transport = NetUtils.getTransport();
            HttpRequest request;
            HttpResponse response;
            String result = new String();
            try {

                MultipartFormDataContent content = new MultipartFormDataContent();

                content.addPart(new Part().setName("vb_login_username").setContent(ByteArrayContent.fromString("text/plain", params[0])));
                content.addPart(new Part().setName("vb_login_password").setContent(ByteArrayContent.fromString("text/plain", params[1])));
                content.addPart(new Part().setName("do").setContent(ByteArrayContent.fromString("text/plain", "login")));
                content.addPart(new Part().setName("cookieuser").setContent(ByteArrayContent.fromString("text/plain", "1")));

                request = transport.createRequestFactory().buildPostRequest(new GenericUrl("http://www.macinsiders.com/login.php"), content);
                //                request = transport.createRequestFactory().buildGetRequest(new GenericUrl("http://www.macinsiders.com/chat/?ajax=true"));
                response = request.execute();

                //                String content = CharStreams.toString(new InputStreamReader(response.getContent()));
                //                Log.d(TAG, content);

                HttpHeaders headers = response.getHeaders();

                ArrayList<String> cookies = (ArrayList<String>) headers.get("set-cookie");
                for (String cookie : cookies) {
                    Log.d(TAG, cookie);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, result);
        }

    }

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

    protected void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mPasswordField.getWindowToken(), 0);
    }
}
