package com.afzaln.mi_chat.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.afzaln.mi_chat.R;

/**
 * Created by afzal on 2013-09-03.
 */
public class AboutActivity extends ActionBarActivity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.about_activity);

        getSupportActionBar().setDisplayOptions(
                ActionBar.DISPLAY_SHOW_HOME |
                ActionBar.DISPLAY_SHOW_TITLE |
                ActionBar.DISPLAY_SHOW_CUSTOM |
                ActionBar.DISPLAY_HOME_AS_UP);

        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
        }

        String versionName = packageInfo.versionName;
        TextView versionView = (TextView) findViewById(R.id.version);
        versionView.setText("version " + versionName);

        TextView legalView = (TextView)findViewById(R.id.legal);
        legalView.setText(getResources().getString(R.string.legal));

        TextView authorEmailView = (TextView) findViewById(R.id.author_email);
        Linkify.addLinks(authorEmailView, Linkify.EMAIL_ADDRESSES);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_email:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getResources().getString(R.string.author_email), null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Macinsiders Chat app");
                startActivity(Intent.createChooser(emailIntent, "Email developer"));
                return true;
            default:
                return false;
        }
    }
}