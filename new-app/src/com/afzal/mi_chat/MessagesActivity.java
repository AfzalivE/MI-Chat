package com.afzal.mi_chat;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.afzal.mi_chat.processor.ProcessorFactory;
import com.afzal.mi_chat.processor.ResourceProcessor;
import com.afzal.mi_chat.provider.ProviderContract.MessagesTable;
import com.afzal.mi_chat.service.ServiceContract;
import com.afzal.mi_chat.utils.NetUtils;

public class MessagesActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MESSAGE_LOADER = 0;

    MessagesCursorAdapter mAdapter;

    private ListView mListView;

    private static final String TAG = MessagesActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages);
        getWindow().setBackgroundDrawable(null);

        getSupportLoaderManager().initLoader(MESSAGE_LOADER, null, this);

        setSlidingActionBarEnabled(true);

        mListView = (ListView) findViewById(R.id.messagelist);

        mAdapter = new MessagesCursorAdapter(this, null, 0);

        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        ResourceProcessor processor = ProcessorFactory.getInstance(this).getProcessor(ServiceContract.RESOURCE_TYPE_PAGE);

        processor.getResource();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                ResourceProcessor processor = ProcessorFactory.getInstance(this).getProcessor(ServiceContract.RESOURCE_TYPE_PAGE);
                processor.getResource();
                setProgressBarIndeterminateVisibility(true);
                return true;
            case R.id.action_clearprefs:
                NetUtils.getCookieStoreInstance(MessagesActivity.this).clear();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        switch (loaderId) {
            case MESSAGE_LOADER:
                Log.d(TAG, "Loading messages");
                return new CursorLoader(this, MessagesTable.CONTENT_URI, new String[] {
                        MessagesTable._ID,
                        MessagesTable.DATETIME,
                        MessagesTable.USERROLE,
                        MessagesTable.USERNAME,
                        MessagesTable.MESSAGE }, null, null, null);
            default:
                // invalid id was passed
                return null;

        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
    }

    /*
     * Clears out the adapter's reference to the Cursor. This prevents memory
     * leaks.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> cusror) {
        mAdapter.changeCursor(null);
    }
}
