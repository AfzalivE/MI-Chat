package com.afzaln.mi_chat.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.afzaln.mi_chat.AlarmReceiver;
import com.afzaln.mi_chat.MessagesCursorAdapter;
import com.afzaln.mi_chat.R;
import com.afzaln.mi_chat.R.id;
import com.afzaln.mi_chat.processor.ProcessorFactory;
import com.afzaln.mi_chat.processor.ResourceProcessor;
import com.afzaln.mi_chat.provider.ProviderContract.MessagesTable;
import com.afzaln.mi_chat.resource.Message;
import com.afzaln.mi_chat.service.ServiceContract;
import com.afzaln.mi_chat.utils.NetUtils;
import com.afzaln.mi_chat.view.MessageListView;
import com.afzaln.mi_chat.view.MessageListView.OnSizeChangedListener;
import com.google.analytics.tracking.android.EasyTracker;
import com.loopj.android.http.XmlHttpResponseHandler;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.Calendar;

public class MessagesActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MESSAGE_LOADER = 0;
    private static final int DEFAULT_REFRESH_INTERVAL = 3000;
    private static final String TAG = MessagesActivity.class.getSimpleName();
    private static int mRefreshInterval = DEFAULT_REFRESH_INTERVAL;
    private MessagesCursorAdapter mAdapter;
    private MessageListView mListView;
    private EditText mEditText;
    private ImageButton mSubmitButton;
    private Menu mMenu;
    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;
    private boolean mManualRefresh = false;
    private XmlHttpResponseHandler mLogoutResponseHandler = new XmlHttpResponseHandler() {
        @Override
        public void onStart() {
//            Log.d(TAG, "onStart");
        }

        @Override
        public void onSuccess(Document response) {
//            Log.d(TAG, "onSuccess");
            Node info = response.getElementsByTagName("info").item(0);
            if (info.getAttributes().getNamedItem("type").getTextContent().equals("logout")) {
                NetUtils.getCookieStoreInstance(MessagesActivity.this).clear();
                Intent i = new Intent(MessagesActivity.this, LoginActivity.class);
                MessagesActivity.this.finish();
                startActivity(i);
            };
        }

        @Override
        public void onFailure(Throwable e, Document response) {
//            Log.d(TAG, "onFailure");
            e.printStackTrace();
            // Response failed :(
        }

        @Override
        public void onFinish() {
//            Log.d(TAG, "onFinish");
            // Completed the request (either success or failure)
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawable(null);

        getSupportLoaderManager().initLoader(MESSAGE_LOADER, null, this);
        mListView = (MessageListView) findViewById(R.id.messagelist);
        mAdapter = new MessagesCursorAdapter(this, null, 0);
        mListView.setAdapter(mAdapter);
        // TODO API 9 compatibility
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        registerForContextMenu(mListView);

        mListView.setOnSizeChangedListener(new OnSizeChangedListener() {
            public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
                mListView.setSelection(mAdapter.getCount() - 1);
            }
        });

        mEditText = (EditText) findViewById(id.text_editor);
        mSubmitButton = (ImageButton) findViewById(id.submit_msg);
        mSubmitButton.setEnabled(false);

        mEditText.addTextChangedListener(new TextWatcher() {
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
        });

        mSubmitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("message", mEditText.getText().toString());

                ResourceProcessor processor = ProcessorFactory.getInstance(MessagesActivity.this).getProcessor(ServiceContract.RESOURCE_TYPE_MESSAGE);
                processor.postResource(bundle);

                setProgressBarIndeterminateVisibility(true);
                mMenu.findItem(id.action_refresh).setVisible(false);

                mEditText.setText("");
                mSubmitButton.setEnabled(false);
                mAlarmManager.cancel(mPendingIntent);
            }
        });

        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

//        ViewServer.get(this).addWindow(this);
    }

    @Override
    public void onResume() {
        super.onResume();
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
        mManualRefresh = false;
        mAlarmManager.cancel(mPendingIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ResourceProcessor processor = ProcessorFactory.getInstance(this).getProcessor(ServiceContract.RESOURCE_TYPE_PAGE);
        Intent i;
        switch (item.getItemId()) {
            case R.id.action_refresh:
                processor.getResource();
                setProgressBarIndeterminateVisibility(true);
                item.setVisible(false);
                mManualRefresh = true;
                return true;
            case R.id.action_prefs:
                i = new Intent(MessagesActivity.this, PreferencesActivity.class);
                startActivity(i);
                break;
            case R.id.action_clearmessages:
                processor.deleteResource();
                showRefreshProgressBar(true);
                break;
            case R.id.action_logout:
                NetUtils.postLogout(mLogoutResponseHandler);
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
                mEditText.setText("@" + username + " ");
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

    private void copyToClipboard(CharSequence message) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        // TODO API 9 compatibility
        ClipData data = ClipData.newPlainText("message", message);
        clipboard.setPrimaryClip(data);
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
        boolean isListAtEnd = mListView.getLastVisiblePosition() == (mAdapter.getCount() - 1);
        mAdapter.changeCursor(cursor);
        showRefreshProgressBar(false);

        // if refresh was manual or if list was scrolled to the
        // bottom and the content new content is more than
        // 10 rows below, jump to it, otherwise smooth scroll
        int newCount = mAdapter.getCount();
        if (mManualRefresh || isListAtEnd) {
            if (newCount - mListView.getLastVisiblePosition() > 10) {
                mListView.setSelection(newCount - 1);
            } else {
                mListView.smoothScrollToPosition(newCount - 1);
            }
        }

        // Refresh backoff code
        if (newCount > 0) {
            long latestItem = mAdapter.getItemDateTime(newCount - 1);
            if (Calendar.getInstance().getTimeInMillis() - latestItem > 60000) {
                mRefreshInterval = 5000;
            } else if (Calendar.getInstance().getTimeInMillis() - latestItem > 120000) {
                mRefreshInterval = 10000;
            } else if (Calendar.getInstance().getTimeInMillis() - latestItem > 150000) {
                mRefreshInterval = 20000;
            } else if (Calendar.getInstance().getTimeInMillis() - latestItem > 180000) {
                mRefreshInterval = 40000;
            } else {
                mRefreshInterval = DEFAULT_REFRESH_INTERVAL;
            }
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
        setProgressBarIndeterminateVisibility(value);
    }
}
