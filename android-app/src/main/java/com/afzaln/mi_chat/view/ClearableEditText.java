/**
 * Copyright 2013 Alex Yanchenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.afzaln.mi_chat.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.afzaln.mi_chat.R;

/**
 * To change clear icon, set
 * <p/>
 * <pre>
 * android:drawableRight="@drawable/custom_icon"
 * </pre>
 */
public class ClearableEditText extends EditText {
    // TODO replace mBtnClear Drawable with a Button
    private Drawable mBtnClear;

    public ClearableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClearableEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        mBtnClear = getCompoundDrawables()[2];
        if (mBtnClear == null) {
            mBtnClear = getResources().getDrawable(R.drawable.cancel_button_selector);
        }
        mBtnClear.setBounds(0, 0, mBtnClear.getIntrinsicWidth(), mBtnClear.getIntrinsicHeight());
        setClearIconVisible(false);
        addTextChangedListener(mTextWatcher);
        setOnTouchListener(mTouchListener);
        setOnFocusChangeListener(mFocusChangeListener);
    }

    private void setClearIconVisible(boolean visible) {
        Drawable x = visible ? mBtnClear : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], x, getCompoundDrawables()[3]);
    }

    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (isFocused()) {
                setClearIconVisible(!s.toString().isEmpty());
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private OnTouchListener mTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (getCompoundDrawables()[2] != null) {
                boolean tappedX = event.getX() > (getWidth() - getPaddingRight() - mBtnClear
                        .getIntrinsicWidth());
                if (tappedX) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        setText("");
                    }
                    return true;
                }
            }

            return false;
        }
    };
    private OnFocusChangeListener mFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                setClearIconVisible(!getText().toString().isEmpty());
            } else {
                setClearIconVisible(false);
            }
        }
    };

}