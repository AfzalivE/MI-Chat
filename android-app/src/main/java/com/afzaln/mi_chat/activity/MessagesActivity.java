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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.ActionMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afzaln.mi_chat.AlarmReceiver;
import com.afzaln.mi_chat.BuildConfig;
import com.afzaln.mi_chat.MessagesCursorAdapter;
import com.afzaln.mi_chat.R;
import com.afzaln.mi_chat.handler.LogoutResponseHandler;
import com.afzaln.mi_chat.processor.ProcessorFactory;
import com.afzaln.mi_chat.processor.ResourceProcessor;
import com.afzaln.mi_chat.provider.ProviderContract.MessagesTable;
import com.afzaln.mi_chat.resource.Message;
import com.afzaln.mi_chat.utils.BackoffUtils;
import com.afzaln.mi_chat.utils.NetUtils;
import com.afzaln.mi_chat.utils.PrefUtils;
import com.afzaln.mi_chat.utils.ServiceContract;
import com.afzaln.mi_chat.view.MessageListView;
import com.afzaln.mi_chat.view.MessageListView.OnSizeChangedListener;
import com.google.analytics.tracking.android.EasyTracker;

import java.util.Calendar;

public class MessagesActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>, ActionMode.Callback {

    private static int mRefreshInterval = BackoffUtils.DEFAULT_REFRESH_INTERVAL;

    private static final int MESSAGE_LOADER = 0;
    private static final String TAG = MessagesActivity.class.getSimpleName();

    private boolean mManualRefresh = false;
    private MessagesCursorAdapter mAdapter;
    private MessageListView mListView;
    private EditText mEditText;
    private ImageButton mSubmitButton;
    private ImageButton mSubmitImgButton;

    private ActionMode mActionMode;
    private Menu mMenu;
    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;
    private LogoutResponseHandler mLogoutResponseHandler = new LogoutResponseHandler(MessagesActivity.this);
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
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(null);

        if (!PrefUtils.authCookieExists(this)) {
            Intent i = new Intent(MessagesActivity.this, LoginActivity.class);
            this.finish();
            startActivity(i);
        }

        getSupportLoaderManager().initLoader(MESSAGE_LOADER, null, this);
        initListView();

        mEditText = (EditText) findViewById(R.id.text_editor);
        mEditText.addTextChangedListener(textWatcher);

        initSubmitButton();

        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

//        ViewServer.get(this).addWindow(this);
    }


    private void initListView() {
        mListView = (MessageListView) findViewById(R.id.messagelist);
        mAdapter = new MessagesCursorAdapter(this, null, 0);
        mListView.setAdapter(mAdapter);

        mListView.setOnSizeChangedListener(new OnSizeChangedListener() {
            public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
                mListView.setSelection(mAdapter.getCount() - 1);
            }
        });

//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
//                  view.setTag("selected");
//                if (view.getTag() == "selected") {
//                    view.setSelected(false);
//                }
////                if (view.isSelected()) {
////                    view.setSelected(false);
////                    mActionMode.finish();
////                } else {
////                    view.setSelected(true);
////                    mActionMode = startSupportActionMode(MessagesActivity.this);
////                }
//            }
//        });

        registerForContextMenu(mListView);

    }

    private void initSubmitButton() {
        mSubmitButton = (ImageButton) findViewById(R.id.submit_msg);
        mSubmitButton.setEnabled(false);

        mSubmitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetUtils.isConnected(MessagesActivity.this)) {
                    mAlarmManager.cancel(mPendingIntent);

                    Bundle bundle = new Bundle();
                    bundle.putString("message", mEditText.getText().toString());

                    ResourceProcessor processor = ProcessorFactory.getInstance(MessagesActivity.this).getProcessor(ServiceContract.RESOURCE_TYPE_MESSAGE);
                    processor.postResource(bundle);

                    toggleProgressBar(true);
                    mEditText.setText("");
                    mSubmitButton.setEnabled(false);
                }
            }
        });

        if (BuildConfig.DEBUG) {
            mSubmitImgButton = (ImageButton) findViewById(R.id.submit_img);
            mSubmitImgButton.setVisibility(View.VISIBLE);
            mSubmitImgButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NetUtils.isConnected(MessagesActivity.this)) {
                        mAlarmManager.cancel(mPendingIntent);
                        Bundle bundle = new Bundle();
                        bundle.putString("message", "[img]http://collider.com/wp-content/uploads/christina-hendricks-1.jpeg[/img][img]http://cdn.evilbeetgossip.com/wp-content/uploads/2013/01/christina_hendricks.jpg[/img]");

                        ResourceProcessor processor = ProcessorFactory.getInstance(MessagesActivity.this).getProcessor(ServiceContract.RESOURCE_TYPE_MESSAGE);
                        processor.postResource(bundle);
                        toggleProgressBar(true);
                    }

                }
            });
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        // TODO use the Service for this
        // IntentService doesn't work with async-http client because you can't run AsyncTask from it
        // Use normal Service class maybe
        if (NetUtils.isConnected(MessagesActivity.this)) {
            ResourceProcessor processor = ProcessorFactory.getInstance(this).getProcessor(ServiceContract.RESOURCE_TYPE_PAGE);
            processor.getResource();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mManualRefresh = false;
        mAlarmManager.cancel(mPendingIntent);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messages, menu);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            menu.findItem(id.action_prefs).setVisible(true);
//        }
        mMenu = menu;
        if (NetUtils.isConnected(MessagesActivity.this)) {
            toggleProgressBar(true);
        } else {
            Toast.makeText(this, "No internet connection available", Toast.LENGTH_LONG).show();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ResourceProcessor processor = ProcessorFactory.getInstance(this).getProcessor(ServiceContract.RESOURCE_TYPE_PAGE);
        Intent i;
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if (NetUtils.isConnected(MessagesActivity.this)) {
                    toggleProgressBar(true);
                    mManualRefresh = true;
                    processor.getResource();
                } else {
                    Toast.makeText(this, "No internet connection available", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.action_prefs:
                i = new Intent(MessagesActivity.this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.action_clearmessages:
                processor.deleteResource();
                toggleProgressBar(true);
                break;
            case R.id.action_logout:
                if (NetUtils.isConnected(MessagesActivity.this)) {
                    NetUtils.postLogout(mLogoutResponseHandler);
                }
                break;
            case R.id.action_about:
                i = new Intent(MessagesActivity.this, AboutActivity.class);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        getMenuInflater().inflate(R.menu.message_item, menu);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        CharSequence message;
        switch (item.getItemId()) {
            case R.id.menu_copytext:
                TextView messageView = (TextView) info.targetView.findViewById(R.id.message);
                if (messageView.getVisibility() == View.VISIBLE) {
                    message = ((TextView) info.targetView.findViewById(R.id.message)).getText();
                    copyToClipboard(message);
                } else {
                    copyToClipboard("");
                }
                return true;
            case R.id.menu_reply:
                CharSequence username = ((TextView) info.targetView.findViewById(R.id.username)).getText();
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
                // Do action specific things
                mEditText.setText("@" + username + " ");
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
        toggleProgressBar(false);
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
        toggleProgressBar(false);
        mAdapter.changeCursor(null);
    }

    private void toggleProgressBar(boolean show) {
        if (mMenu != null) {
            MenuItem menuItem = mMenu.findItem(R.id.action_refresh);
            if (show) {
                MenuItemCompat.setActionView(menuItem, R.layout.progress);
                MenuItemCompat.expandActionView(menuItem);
            } else {
                MenuItemCompat.collapseActionView(menuItem);
                MenuItemCompat.setActionView(menuItem, null);
            }
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        actionMode.getMenuInflater().inflate(R.menu.message_item, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        actionMode.finish();
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        CharSequence message;
        switch (item.getItemId()) {
            case R.id.menu_copytext:
//                message = ((TextView) info.targetView.findViewById(id.message)).getText();
                copyToClipboard("test");
                actionMode.finish();
                return true;
            case R.id.menu_reply:
                CharSequence username = ((TextView) info.targetView.findViewById(R.id.username)).getText();
                makeReply(username, mAdapter.getItemViewType(info.position));
                actionMode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        Log.d(TAG, "destroying action mode");
    }
}
