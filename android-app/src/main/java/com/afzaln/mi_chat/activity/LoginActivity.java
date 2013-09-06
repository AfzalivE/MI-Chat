package com.afzaln.mi_chat.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewFlipper;

import com.afzaln.mi_chat.R;
import com.afzaln.mi_chat.handler.LoginResponseHandler;
import com.afzaln.mi_chat.utils.NetUtils;
import com.afzaln.mi_chat.utils.PrefUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;

import static com.afzaln.mi_chat.provider.ProviderContract.InfoTable;

public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private EditText mUsernameField;
    private EditText mPasswordField;
    public ViewFlipper mLoginFlipper;
    private AsyncHttpResponseHandler mLoginResponseHandler = new LoginResponseHandler(LoginActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setBackgroundDrawable(null);

        if (PrefUtils.authCookieExists(this)) {
            if (NetUtils.isConnected(LoginActivity.this)) {
                NetUtils.postLogin(mLoginResponseHandler, LoginActivity.this, null, null);
            }
        }

        mUsernameField = (EditText) findViewById(R.id.username);
        mPasswordField = (EditText) findViewById(R.id.password);
        mPasswordField.setTypeface(Typeface.DEFAULT);
        mPasswordField.setTransformationMethod(new PasswordTransformationMethod());
        mLoginFlipper = (ViewFlipper) findViewById(R.id.loginflipper);
        mLoginFlipper.setOutAnimation(LoginActivity.this, android.R.anim.fade_out);
        mLoginFlipper.setInAnimation(LoginActivity.this, android.R.anim.fade_in);

        Cursor cursor = getContentResolver().query(InfoTable.CONTENT_URI, new String[] {InfoTable.USERNAME}, null, null, InfoTable.USERNAME + " LIMIT 1");
        if (cursor.moveToNext()) {
            mUsernameField.setText(cursor.getString(cursor.getColumnIndex(InfoTable.USERNAME)));
            mPasswordField.requestFocus();
        }
        cursor.close();

        Button login = (Button) findViewById(R.id.login);

        login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                hideKeyboard();
                String username = mUsernameField.getText().toString();
                String password = mPasswordField.getText().toString();
                if (NetUtils.isConnected(LoginActivity.this)) {
                    NetUtils.postLogin(mLoginResponseHandler, LoginActivity.this, username, password);
                }
            }
        });

        Button signup = (Button) findViewById(R.id.signup);

        signup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(NetUtils.SIGNUP_URI));
                startActivity(intent);
            }
        });


    }

    protected void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mPasswordField.getWindowToken(), 0);
    }
}
