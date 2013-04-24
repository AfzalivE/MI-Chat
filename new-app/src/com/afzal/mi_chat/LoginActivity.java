package com.afzal.mi_chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.afzal.mi_chat.Utils.NetUtils;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.MultipartFormDataContent;
import com.google.api.client.http.MultipartFormDataContent.Part;
import com.google.api.client.http.javanet.NetHttpTransport;

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
            }
        });

    }

    public class LoginTask extends AsyncTask<String, Void, ArrayList<String>> {

        private final String TAG = LoginTask.class.getSimpleName();

        @SuppressWarnings("unchecked")
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            Map<String, String> paramsMap = new HashMap<String, String>();
            paramsMap.put("vb_login_username", params[0]);
            paramsMap.put("vb_login_password", params[1]);
            paramsMap.put("do", "login");
            paramsMap.put("cookieuser", "1");

            HttpTransport transport = NetUtils.getTransport();
            HttpRequest request;
            HttpResponse response;
            ArrayList<String> result = new ArrayList<String>();
            try {

                MultipartFormDataContent content = new MultipartFormDataContent();

                content.addPart(new Part().setName("vb_login_username").setContent(ByteArrayContent.fromString("text/plain", params[0])));
                content.addPart(new Part().setName("vb_login_password").setContent(ByteArrayContent.fromString("text/plain", params[1])));
                content.addPart(new Part().setName("do").setContent(ByteArrayContent.fromString("text/plain", "login")));
                content.addPart(new Part().setName("cookieuser").setContent(ByteArrayContent.fromString("text/plain", "1")));

                request = transport.createRequestFactory().buildPostRequest(new GenericUrl("http://www.macinsiders.com/login.php"), content);
                response = request.execute();

                HttpHeaders headers = response.getHeaders();

                result = (ArrayList<String>) headers.get("set-cookie");

            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;

        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            super.onPostExecute(result);

            for (String cookie : result) {
                if (cookie.contains("bbpassword")) {
                    Log.d(TAG, cookie);
                    Intent i = new Intent(LoginActivity.this, MessagesActivity.class);
                    startActivity(i);
                }
            }
        }

    }

    protected void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mPasswordField.getWindowToken(), 0);
    }
}
