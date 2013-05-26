package com.afzal.mi_chat;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.afzal.mi_chat.R.id;
import com.afzal.mi_chat.processor.ProcessorFactory;
import com.afzal.mi_chat.processor.ResourceProcessor;
import com.afzal.mi_chat.provider.ProviderContract.MessagesTable;
import com.afzal.mi_chat.service.ServiceContract;
import com.afzal.mi_chat.utils.NetUtils;

public class MessagesActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MESSAGE_LOADER = 0;

    private MessagesCursorAdapter mAdapter;
    private int itemCount;
    private ListView mListView;
    private EditText mEditText;
    private Button mSubmitButton;
    private Menu mMenu;

    private static final String TAG = MessagesActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.messages);
        getWindow().setBackgroundDrawable(null);

        getSupportLoaderManager().initLoader(MESSAGE_LOADER, null, this);
        mListView = (ListView) findViewById(R.id.messagelist);
        itemCount = 0;
        mAdapter = new MessagesCursorAdapter(this, null, 0);
        mListView.setAdapter(mAdapter);

        mEditText = (EditText) findViewById(id.textbox);
        mSubmitButton = (Button) findViewById(id.submitmsg);

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }

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
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        setProgressBarIndeterminateVisibility(true);

        // TODO use the Service for this
        // IntentService doesn't work with async-http client because you can't run AsyncTask from it
        // Use normal Service class maybe
        ResourceProcessor processor = ProcessorFactory.getInstance(this).getProcessor(ServiceContract.RESOURCE_TYPE_PAGE);
        processor.getResource();
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
        switch (item.getItemId()) {
            case R.id.action_refresh:
                processor.getResource();
                setProgressBarIndeterminateVisibility(true);
                item.setVisible(false);
                return true;
            case R.id.action_cleardata:
                processor.deleteResource();
                setProgressBarIndeterminateVisibility(true);
                break;
            case R.id.action_clearsession:
                NetUtils.getCookieStoreInstance(MessagesActivity.this).clear();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        Log.d(TAG, "Loading messages");
        switch (loaderId) {
            case MESSAGE_LOADER:
                return new CursorLoader(this, MessagesTable.CONTENT_URI, new String[] {
                        MessagesTable._ID,
                        MessagesTable.DATETIME,
                        MessagesTable.USERROLE,
                        MessagesTable.USERNAME,
                        MessagesTable.MESSAGE}, null, null, null);
            default:
                // invalid id was passed
                return null;

        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
        setProgressBarIndeterminateVisibility(false);
        if (mMenu != null) {
            mMenu.findItem(id.action_refresh).setVisible(true);
        }

        // animate scrolling to bottom if less than 10 new items added
        // otherwise jump
        // not sure if the best idea but it works for now I guess
        int newCount = mAdapter.getCount();
        if (newCount - itemCount > 0) {
            if (newCount - itemCount > 10) {
                mListView.setSelection(newCount - 1);
            } else {
                mListView.smoothScrollToPosition(newCount - 1);
            }
            itemCount = newCount;
        }
    }

    /*
     * Clears out the adapter's reference to the Cursor. This prevents memory
     * leaks.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> cusror) {
        setProgressBarIndeterminateVisibility(false);
        mAdapter.changeCursor(null);
    }
}
