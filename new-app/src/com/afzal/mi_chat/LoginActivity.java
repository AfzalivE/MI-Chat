package com.afzal.mi_chat;

import java.util.List;

import org.apache.http.cookie.Cookie;

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
import android.widget.ViewFlipper;

import com.afzal.mi_chat.Utils.NetUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private boolean mRetryLogin = true;

    private EditText mUsernameField;
    private EditText mPasswordField;
    private ViewFlipper mLoginFlipper;
    private AsyncHttpResponseHandler loginResponseHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onStart() {
            mLoginFlipper.showNext();
            Log.d(TAG, "onStart");
        }

        @Override
        public void onSuccess(String response) {
            if (authCookieExists()) {
                Intent i = new Intent(LoginActivity.this, MessagesActivity.class);
                startActivity(i);
            } else {
                mLoginFlipper.showPrevious();
                Crouton.makeText(LoginActivity.this, "Username/password incorrect", Style.ALERT).show();
            }
            Log.d(TAG, "onSuccess");
        }

        @Override
        public void onFailure(Throwable e, String response) {
            // TODO deal with
            Log.d(TAG, "onFailure");
            if (authCookieExists() && mRetryLogin) {
                login(null);
            } else {
                mLoginFlipper.showPrevious();
                Crouton.makeText(LoginActivity.this, "Couldn't sign in, please try again", Style.ALERT).show();
            }

        }

        @Override
        public void onFinish() {
            Log.d(TAG, "onFinish");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        getWindow().setBackgroundDrawable(null);

        mUsernameField = (EditText) findViewById(R.id.username);
        mPasswordField = (EditText) findViewById(R.id.password);
        mPasswordField.setTypeface(Typeface.DEFAULT);
        mPasswordField.setTransformationMethod(new PasswordTransformationMethod());
        mLoginFlipper = (ViewFlipper) findViewById(R.id.loginflipper);
        mLoginFlipper.setOutAnimation(LoginActivity.this, android.R.anim.fade_out);
        mLoginFlipper.setInAnimation(LoginActivity.this, android.R.anim.fade_in);

        Button login = (Button) findViewById(R.id.login);
        if (authCookieExists()) {
            login(null);
        }

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

                login(params);

            }
        });

    }

    protected void login(RequestParams params) {
        NetUtils.client.setCookieStore(NetUtils.getCookieStoreInstance(LoginActivity.this));
        if (params != null) {
            NetUtils.client.post("http://www.macinsiders.com/login.php", params, loginResponseHandler);
        } else {
            NetUtils.client.post("http://www.macinsiders.com/login.php", loginResponseHandler);
        }
    }

    protected void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mPasswordField.getWindowToken(), 0);
    }

    protected boolean authCookieExists() {
        List<Cookie> cookies = NetUtils.getCookieStoreInstance(LoginActivity.this).getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("bbpassword")) {
                return true;
            }
        }
        return false;
    }
}
