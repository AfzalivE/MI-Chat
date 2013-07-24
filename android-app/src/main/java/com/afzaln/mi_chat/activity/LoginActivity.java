package com.afzaln.mi_chat.activity;

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

import com.afzaln.mi_chat.R;
import com.afzaln.mi_chat.utils.NetUtils;
import com.afzaln.mi_chat.utils.PrefUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private boolean mRetryLogin = true;

    private EditText mUsernameField;
    private EditText mPasswordField;
    private ViewFlipper mLoginFlipper;
    private AsyncHttpResponseHandler mLoginResponseHandler = new LoginResponseHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        getWindow().setBackgroundDrawable(null);

        if (PrefUtils.authCookieExists(this)) {
            NetUtils.postLogin(mLoginResponseHandler, LoginActivity.this, null, null);
        }

        mUsernameField = (EditText) findViewById(R.id.username);
        mPasswordField = (EditText) findViewById(R.id.password);
        mPasswordField.setTypeface(Typeface.DEFAULT);
        mPasswordField.setTransformationMethod(new PasswordTransformationMethod());
        mLoginFlipper = (ViewFlipper) findViewById(R.id.loginflipper);
        mLoginFlipper.setOutAnimation(LoginActivity.this, android.R.anim.fade_out);
        mLoginFlipper.setInAnimation(LoginActivity.this, android.R.anim.fade_in);


        Button login = (Button) findViewById(R.id.login);

        login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                hideKeyboard();
                String username = mUsernameField.getText().toString();
                String password = mPasswordField.getText().toString();
                NetUtils.postLogin(mLoginResponseHandler, LoginActivity.this, username, password);
            }
        });

    }

    protected void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mPasswordField.getWindowToken(), 0);
    }

    private class LoginResponseHandler extends AsyncHttpResponseHandler {
        @Override
        public void onStart() {
            if (mRetryLogin) {
                mLoginFlipper.showNext();
            }
            Log.d(TAG, "onStart");
        }

        @Override
        public void onSuccess(String response) {
            if (PrefUtils.authCookieExists(LoginActivity.this)) {
                Intent i = new Intent(LoginActivity.this, MessagesActivity.class);
                LoginActivity.this.finish();
                startActivity(i);
            } else {
                mLoginFlipper.showPrevious();
                Crouton.makeText(LoginActivity.this, "Username/password incorrect", Style.ALERT).show();
            }
            Log.d(TAG, "onSuccess");
        }

        @Override
        public void onFailure(Throwable e, String response) {
            Log.d(TAG, "onFailure");
            if (PrefUtils.authCookieExists(LoginActivity.this) && mRetryLogin) {
                mRetryLogin = false;
                NetUtils.postLogin(mLoginResponseHandler, LoginActivity.this, null, null);
            } else {
                mLoginFlipper.showPrevious();
                Crouton.makeText(LoginActivity.this, "Couldn't sign in, please try again", Style.ALERT).show();
            }

        }

        @Override
        public void onFinish() {
            Log.d(TAG, "onFinish");
        }
    }
}
