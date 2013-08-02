package com.afzaln.mi_chat.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.afzaln.mi_chat.R;
import com.afzaln.mi_chat.UserListFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenedListener;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class BaseActivity extends SlidingFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.main_layout);

        SlidingMenu sm = getSlidingMenu();

        // check if the layout contains the menu frame
        if (findViewById(R.id.menu_frame) == null) {
            setBehindContentView(R.layout.menu_frame);
            sm.setSlidingEnabled(true);
            sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            sm.setOnOpenedListener(new OnOpenedListener() {
                @Override
                public void onOpened() {
                    hideKeyboard(getCurrentFocus());
                }
            });
        } else {
            // add a dummy view
            View v = new View(this);
            setBehindContentView(v);
            sm.setSlidingEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }

        // set the behind view fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.menu_frame, new UserListFragment())
                .commit();

        // customize the SlidingMenu
        sm.setShadowWidthRes(R.dimen.shadow_width);
        sm.setShadowDrawable(R.drawable.shadow);
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        sm.setFadeDegree(0.45f);

        setSlidingActionBarEnabled(false);

    }

    protected void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toggle();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
