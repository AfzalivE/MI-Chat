package com.afzal.mi_chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.afzal.mi_chat.Utils.NetUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

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
                RequestParams params = new RequestParams();
                params.put("vb_login_username", username);
                params.put("vb_login_password", password);
                params.put("do", "login");
                params.put("cookieuser", "1");
                NetUtils.client.post("http://www.macinsiders.com/login.php", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        Log.d(TAG, "TEST");
                    }

                    @Override
                    public void onSuccess(String response) {
                        Log.d(TAG, response);
                        Intent i = new Intent(LoginActivity.this, MessagesActivity.class);
                        startActivity(i);
                    }

                    @Override
                    public void onFailure(Throwable e, String response) {
                        Log.d(TAG, "FAILED");
                        // Response failed :(
                    }

                    @Override
                    public void onFinish() {
                        Log.d(TAG, "TEST");
                        // Completed the request (either success or failure)
                    }
                });
            }
        });

    }

    protected void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mPasswordField.getWindowToken(), 0);
    }
}
