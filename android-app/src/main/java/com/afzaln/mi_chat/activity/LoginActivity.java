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
import com.afzaln.mi_chat.utils.MIChatApi;
import com.afzaln.mi_chat.utils.PrefUtils;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import org.apache.http.HttpStatus;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText mUsernameField;
    private EditText mPasswordField;
    private ViewFlipper mLoginFlipper;

    private FutureCallback<Response<String>> mLoginCallback = new FutureCallback<Response<String>>() {
        @Override
        public void onCompleted(Exception e, Response<String> response) {
            if (e != null) {
                mLoginFlipper.showPrevious();
                Crouton.makeText(LoginActivity.this, e.getMessage(), Style.ALERT).show();
            } else if (response.getHeaders().getResponseCode() == HttpStatus.SC_OK) {
                Log.d(TAG, response.getHeaders().getStatusLine());

                if (PrefUtils.authCookieExists(LoginActivity.this)) {
                    Intent i = new Intent(LoginActivity.this, MessagesActivity.class);
                    LoginActivity.this.finish();
                    startActivity(i);
                } else {
                    mLoginFlipper.showPrevious();
                    Crouton.makeText(LoginActivity.this, "Username/password incorrect", Style.ALERT).show();
                }
            } else {
                mLoginFlipper.showPrevious();
                Crouton.makeText(LoginActivity.this, response.getHeaders().getStatusLine(), Style.ALERT).show();
            }
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

        if (PrefUtils.authCookieExists(LoginActivity.this)) {
            MIChatApi.login(LoginActivity.this, mLoginCallback);
        }

        Button login = (Button) findViewById(R.id.login);

        login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                hideKeyboard();
                String username = mUsernameField.getText().toString();
                String password = mPasswordField.getText().toString();
                mLoginFlipper.showNext();
                MIChatApi.login(LoginActivity.this, mLoginCallback, username, password);
            }
        });

    }

    protected void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mPasswordField.getWindowToken(), 0);
    }
}
