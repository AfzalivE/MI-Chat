package com.afzaln.mi_chat.handler;

import android.content.Intent;
import android.util.Log;

import com.afzaln.mi_chat.activity.LoginActivity;
import com.afzaln.mi_chat.activity.MessagesActivity;
import com.afzaln.mi_chat.utils.PrefUtils;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by anajam on 8/26/13.
 */
public class LoginResponseHandler extends TextHttpResponseHandler {

    private static final String TAG = LoginResponseHandler.class.getSimpleName();
    LoginActivity mActivity;

    public LoginResponseHandler(LoginActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onStart() {
        mActivity.mLoginFlipper.showNext();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String response) {
        if (PrefUtils.authCookieExists(mActivity)) {
            Intent i = new Intent(mActivity, MessagesActivity.class);
            mActivity.finish();
            mActivity.startActivity(i);
        } else {
            mActivity.mLoginFlipper.showPrevious();
            Crouton.makeText(mActivity, "Username/password incorrect", Style.ALERT).show();
        }
        Log.d(TAG, "onSuccess");
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String response, Throwable error) {
        Log.d(TAG, "onFailure");
        mActivity.mLoginFlipper.showPrevious();
        Crouton.makeText(mActivity, "Couldn't sign in, please try again", Style.ALERT).show();
    }

    @Override
    public void onFinish() {
        Log.d(TAG, "onFinish");
    }

}
