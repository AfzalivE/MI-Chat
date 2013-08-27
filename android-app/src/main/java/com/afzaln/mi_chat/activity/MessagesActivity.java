package com.afzaln.mi_chat.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afzaln.mi_chat.AlarmReceiver;
import com.afzaln.mi_chat.MessagesCursorAdapter;
import com.afzaln.mi_chat.R;
import com.afzaln.mi_chat.R.id;
import com.afzaln.mi_chat.processor.ProcessorFactory;
import com.afzaln.mi_chat.processor.ResourceProcessor;
import com.afzaln.mi_chat.provider.ProviderContract.MessagesTable;
import com.afzaln.mi_chat.resource.Message;
import com.afzaln.mi_chat.service.ServiceContract;
import com.afzaln.mi_chat.utils.BackoffUtils;
import com.afzaln.mi_chat.utils.MIChatApi;
import com.afzaln.mi_chat.utils.PrefUtils;
import com.afzaln.mi_chat.view.MessageListView;
import com.afzaln.mi_chat.view.MessageListView.OnSizeChangedListener;
import com.google.analytics.tracking.android.EasyTracker;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.apache.http.HttpStatus;

import java.util.Calendar;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class MessagesActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static int mRefreshInterval = BackoffUtils.DEFAULT_REFRESH_INTERVAL;

    private static final int MESSAGE_LOADER = 0;
    private static final String TAG = MessagesActivity.class.getSimpleName();

    private MessagesCursorAdapter mAdapter;
    private MessageListView mListView;
    private EditText mEditText;
    private ImageButton mSubmitButton;
    private ImageButton mSubmitImgButton;
    private Menu mMenu;

    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;
    private boolean mManualRefresh = false;
    private FutureCallback<Response<String>> mLogoutCallback = new FutureCallback<Response<String>>() {
        @Override
        public void onCompleted(Exception e, Response<String> response) {
            if (e != null) {
                // TODO show Crouton here instead of toast
                // Crouton is overlapped by the Action Bar right now
                Toast.makeText(MessagesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } else if (response.getHeaders().getResponseCode() == HttpStatus.SC_OK) {
                Log.d(TAG, response.getHeaders().getStatusLine());
                // TODO clear cookies
                Intent i = new Intent(MessagesActivity.this, LoginActivity.class);
                MessagesActivity.this.finish();
                startActivity(i);

            } else {
                Crouton.makeText(MessagesActivity.this, response.getHeaders().getStatusLine(), Style.ALERT).show();
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "calling onStart now");
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "calling onStop now");
        EasyTracker.getInstance().activityStop(this);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().trim().equals("")) {
                mSubmitButton.setEnabled(false);
            } else {
                mSubmitButton.setEnabled(true);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(null);

        getSupportLoaderManager().initLoader(MESSAGE_LOADER, null, this);
        initListView();

        mEditText = (EditText) findViewById(id.text_editor);
        mEditText.addTextChangedListener(textWatcher);

        initSubmitButton();

        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

//        ViewServer.get(this).addWindow(this);
    }

    private void initListView() {
        mListView = (MessageListView) findViewById(id.messagelist);
        mAdapter = new MessagesCursorAdapter(this, null, 0);
        mListView.setAdapter(mAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        }
        registerForContextMenu(mListView);

        mListView.setOnSizeChangedListener(new OnSizeChangedListener() {
            public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
                mListView.setSelection(mAdapter.getCount() - 1);
            }
        });
    }

    private void initSubmitButton() {
        mSubmitButton = (ImageButton) findViewById(id.submit_msg);
        mSubmitButton.setEnabled(false);

        mSubmitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlarmManager.cancel(mPendingIntent);
                Bundle bundle = new Bundle();
                bundle.putString("message", mEditText.getText().toString());

                ResourceProcessor processor = ProcessorFactory.getInstance(MessagesActivity.this).getProcessor(ServiceContract.RESOURCE_TYPE_MESSAGE);
                processor.postResource(bundle);

                showRefreshProgressBar(true);

                mEditText.setText("");
                mSubmitButton.setEnabled(false);

            }
        });

        mSubmitImgButton = (ImageButton) findViewById(id.submit_img);
        mSubmitImgButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlarmManager.cancel(mPendingIntent);
                Bundle bundle = new Bundle();
                bundle.putString("message", "[img]http://collider.com/wp-content/uploads/christina-hendricks-1.jpeg[/img][img]http://cdn.evilbeetgossip.com/wp-content/uploads/2013/01/christina_hendricks.jpg[/img]");

                ResourceProcessor processor = ProcessorFactory.getInstance(MessagesActivity.this).getProcessor(ServiceContract.RESOURCE_TYPE_MESSAGE);
                processor.postResource(bundle);

                showRefreshProgressBar(true);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "calling onResume now");
        showRefreshProgressBar(true);

        // TODO use the Service for this
        // IntentService doesn't work with async-http client because you can't run AsyncTask from it
        // Use normal Service class maybe
        ResourceProcessor processor = ProcessorFactory.getInstance(this).getProcessor(ServiceContract.RESOURCE_TYPE_PAGE);
        processor.getResource();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "calling onPause now");
        mManualRefresh = false;
        mAlarmManager.cancel(mPendingIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "calling onDestroy now");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            menu.findItem(id.action_prefs).setVisible(true);
        }
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ResourceProcessor processor = ProcessorFactory.getInstance(this).getProcessor(ServiceContract.RESOURCE_TYPE_PAGE);
        Intent i;
        switch (item.getItemId()) {
            case R.id.action_refresh:
                setSupportProgressBarIndeterminateVisibility(true);
                item.setVisible(false);
                mManualRefresh = true;
                processor.getResource();
                return true;
            case R.id.action_prefs:
                i = new Intent(MessagesActivity.this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.action_clearmessages:
                processor.deleteResource();
                showRefreshProgressBar(true);
                break;
            case R.id.action_logout:
                PrefUtils.clearCookiePrefs(MessagesActivity.this);
                Ion.getDefault(MessagesActivity.this).cancelAll();
                MIChatApi.logout(MessagesActivity.this, mLogoutCallback);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        CharSequence message;
        switch (item.getItemId()) {
            case id.menu_copytext:
                message = ((TextView) info.targetView.findViewById(id.message)).getText();
                copyToClipboard(message);
                return true;
            case id.menu_reply:
                CharSequence username = ((TextView) info.targetView.findViewById(id.username)).getText();
                makeReply(username, mAdapter.getItemViewType(info.position));
            default:
                return false;
        }
    }

    private void makeReply(CharSequence username, int itemType) {
        switch (itemType) {
            case Message.NORMAL_TYPE:
                mEditText.append("@" + username + " ");
                mEditText.setSelection(mEditText.getText().length());
                break;
            case Message.ACTION_TYPE:
                // TODO detect action type
                mEditText.setText("!!" + username + " ");
                mEditText.setSelection(mEditText.getText().length());
                break;
            default:
                break;
        }
    }

    @SuppressLint("NewApi")
    private void copyToClipboard(CharSequence message) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            @SuppressWarnings("deprecation")
            android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            if (clipboardManager != null) {
                clipboardManager.setText(message);
            }
        } else {
            android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            if (clipboardManager != null) {
                android.content.ClipData data = android.content.ClipData.newPlainText("message", message);
                clipboardManager.setPrimaryClip(data);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        Log.d(TAG, "Loading messages");
        switch (loaderId) {
            case MESSAGE_LOADER:
                return new CursorLoader(this, MessagesTable.CONTENT_URI, MessagesTable.DISPLAY_COLUMNS, null, null, null);
            default:
                // invalid id was passed
                return null;

        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int prevCount = mAdapter.getCount();
        boolean isListAtEnd = mListView.getLastVisiblePosition() == (prevCount - 1);
        mAdapter.changeCursor(cursor);
        showRefreshProgressBar(false);
        int newCount = mAdapter.getCount();
        boolean newMessagesExist = newCount - prevCount > 0;

        // Smooth scroll if the refresh was manual
        // or the user is within 10 rows of the new content
        if (newMessagesExist && (mManualRefresh || isListAtEnd)) {
            if (newCount - mListView.getLastVisiblePosition() > 10) {
                mListView.setSelection(newCount - 1);
            } else {
                mListView.smoothScrollToPosition(newCount - 1);
            }
        }

        if (newCount > 0) {
            long latestTimestamp = mAdapter.getItemDateTime(newCount - 1);
            mRefreshInterval = BackoffUtils.getRefreshInterval(newMessagesExist, latestTimestamp);
        }

        mManualRefresh = false;

        // Updates on regular intervals on the idea
        // that if the app is open, the user must be
        // expecting responses in quick succession
        mAlarmManager.set(AlarmManager.RTC, Calendar.getInstance().getTimeInMillis() + mRefreshInterval, mPendingIntent);
    }

    /*
     * Clears out the adapter's reference to the Cursor. This prevents memory
     * leaks.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> cusror) {
        showRefreshProgressBar(false);
        mAdapter.changeCursor(null);
    }

    private void showRefreshProgressBar(boolean value) {
        if (mMenu != null) {
            mMenu.findItem(id.action_refresh).setVisible(!value);
        }
        setSupportProgressBarIndeterminateVisibility(value);
    }
}
