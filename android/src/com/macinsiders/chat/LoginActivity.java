package com.macinsiders.chat;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.macinsiders.chat.resource.Login;
import com.macinsiders.chat.rest.method.PostLoginRestMethod;
import com.macinsiders.chat.rest.method.RestMethod;
import com.macinsiders.chat.rest.method.RestMethodResult;
import com.macinsiders.chat.security.LoginManager;

public class LoginActivity extends Activity {

    protected ProgressBar mProgressSpinner;
    private EditText usernameField;
    private EditText passwordField;
    private OnLoginTaskCompleteListener mOnLoginTaskCompleteListener = new OnLoginTaskCompleteListener() {

        @Override
        public void onSuccess(Login login) {
            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
            mProgressSpinner.setVisibility(View.GONE);
            mLoginManager.save(login);
            setResult(Activity.RESULT_OK);
            finish();
        }

        @Override
        public void onError(String message, Exception e) {
            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
            mProgressSpinner.setVisibility(View.GONE);
        }
    };
    private LoginManager mLoginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        usernameField = (EditText) findViewById(R.id.username);
        passwordField = (EditText) findViewById(R.id.password);

        mProgressSpinner = (ProgressBar) findViewById(R.id.progress);

        Button loginButton = (Button) findViewById(R.id.btnLogin);
        loginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mProgressSpinner.setVisibility(View.VISIBLE);
                hideKeyboard();
                String username = usernameField.getText().toString();
                String password = passwordField.getText().toString();
                new LoginTask(mOnLoginTaskCompleteListener).execute(username, password);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoginManager = new LoginManager(this);
    }

    protected void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(passwordField.getWindowToken(), 0);
    }

    public interface OnLoginTaskCompleteListener {

        public void onSuccess(Login login);

        public void onError(String message, Exception e);
    }

    public class LoginTask extends AsyncTask<String, Void, RestMethodResult<Login>> {

        private final String TAG = LoginTask.class.getSimpleName();
        private OnLoginTaskCompleteListener mListener;

        public LoginTask(OnLoginTaskCompleteListener listener) {
            mListener = listener;
        }

        @Override
        protected RestMethodResult<Login> doInBackground(String... params) {

            Login login = new Login(params[0], params[1]);
            RestMethod<Login> postLoginRestMethod = new PostLoginRestMethod(getApplicationContext(), login);
            return postLoginRestMethod.execute();

        }

        @Override
        protected void onPostExecute(RestMethodResult<Login> result) {
            super.onPostExecute(result);

            Login login = result.getResource();
            // Log.d(TAG, login.getCookies().toString());

            if (login != null && login.getCookies() != null) {
                mListener.onSuccess(login);
            } else {
                mListener.onError("Login error", null);
            }

        }

    }
}
