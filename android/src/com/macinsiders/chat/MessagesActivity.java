package com.macinsiders.chat;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import com.macinsiders.chat.LoginActivity.OnLoginTaskCompleteListener;
import com.macinsiders.chat.provider.ProviderContract.MessagesTable;
import com.macinsiders.chat.resource.Login;
import com.macinsiders.chat.resource.Messages;
import com.macinsiders.chat.rest.method.PostLogoutRestMethod;
import com.macinsiders.chat.rest.method.RestMethod;
import com.macinsiders.chat.rest.method.RestMethodResult;
import com.macinsiders.chat.security.LoginManager;
import com.macinsiders.chat.service.ServiceHelper;

public class MessagesActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOGIN_REQUEST_CODE = 10;

    private static final String TAG = MessagesActivity.class.getSimpleName();

    private Long requestId;

    // The loader's unique id. Loader ids are specific to the Activity or
    // Fragment in which they reside.
    private static final int LOADER_ID = 0;

    // The callbacks through which we will interact with the LoaderManager.
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;

    // The adapter that binds our data to the ListView
    // TODO
    private MessagesCursorAdapter mAdapter;

    private ServiceHelper mServiceHelper;

    private BroadcastReceiver requestReceiver;
    private IntentFilter filter = new IntentFilter(ServiceHelper.ACTION_REQUEST_RESULT);

    private LoginManager mLoginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestId = mServiceHelper.deleteMessages();

        this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(false);
        // TODO
        setContentView(R.layout.activity_messages);
        getWindow().setBackgroundDrawable(null);

        mLoginManager = new LoginManager(this);

        this.requestReceiver = new Receiver();
        mAdapter = new MessagesCursorAdapter(this, null, 0);
        setListAdapter(mAdapter);

        // The Activity (which implements the LoaderCallbacks<Cursor>
        // interface) is the callbacks object through which we will interact
        // with the LoaderManager. The LoaderManager uses this object to
        // instantiate the Loader and to notify the client when data is made
        // available/unavailable.
        mCallbacks = this;

        // Initialize the Loader with id "0" and callbacks "mCallbacks".
        // If the loader doesn't already exist, one is created. Otherwise,
        // the already created Loader is reused. In either case, the
        // LoaderManager will manage the Loader across the Activity/Fragment
        // lifecycle, will receive any new loads once they have completed,
        // and will report this new data back to the "mCallbacks" object.
        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID, null, mCallbacks);

        ViewServer.get(this).addWindow(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        this.registerReceiver(this.requestReceiver, this.filter);

        mServiceHelper = new ServiceHelper(this);

        if (requestId == null) {
            if (mLoginManager.isLoggedIn()) {
                Log.d(TAG, "requestID is null, initiating request");
                requestId = mServiceHelper.getMessages();
                // requestId = mServiceHelper.postEvent();
                // show progress
                setProgressBarIndeterminateVisibility(true);
            } else {
                Log.d(TAG, "not logged in, getting LoginActivity");
                Intent login = new Intent(this, LoginActivity.class);
                startActivityForResult(login, LOGIN_REQUEST_CODE);
            }
        } else if (mServiceHelper.isRequestPending(requestId)) {
            Log.d(TAG, "requestID is pending");
            // show progress
            setProgressBarVisibility(true);
        } else {
            Log.d(TAG, "requestID is not null");
            setProgressBarVisibility(false);
        }

        ViewServer.get(this).setFocusedWindow(this);
    }

    //    @Override
    //    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //        if (requestCode == LOGIN_REQUEST_CODE) {
    //            if (resultCode == RESULT_OK) {
    //                if (requestId == null) {
    //                    requestId = mServiceHelper.getMessages();
    //                    // requestId = mServiceHelper.postEvent();
    //                    // show progress
    //                    setProgressBarIndeterminateVisibility(true);
    //                } else if (mServiceHelper.isRequestPending(requestId)) {
    //                    // show progress
    //                    setProgressBarVisibility(true);
    //                } else {
    //                    setProgressBarVisibility(false);
    //                }
    //            }
    //        }
    //    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            this.unregisterReceiver(requestReceiver);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Likely receiver wasn't registered, ok to ignore");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViewServer.get(this).removeWindow(this);
    }

    class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long resultRequestId = intent.getLongExtra(ServiceHelper.EXTRA_REQUEST_ID, 0);

            Log.d(TAG, "Received intent " + intent.getAction() + ", request ID: " + resultRequestId);

            if (resultRequestId == requestId) {
                MessagesActivity.this.setProgressBarIndeterminateVisibility(false);
                Log.d(TAG, "Result is for our request ID");

                int resultCode = intent.getIntExtra(ServiceHelper.EXTRA_RESULT_CODE, 0);

                Log.d(TAG, "Result code = " + resultCode);

                if (resultCode == 200) {
                    Log.d(TAG, "Request successful");
                } else {
                    Log.w(TAG, "Request failed" + resultCode);
                }
            } else {
                // not the request we need, but the request we deserved. :(
                Log.d(TAG, "Result is NOT for our request ID");

            }

        }

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor cursor = (Cursor) getListAdapter().getItem(position);
        String messageId = cursor.getString(cursor.getColumnIndex(MessagesTable._ID));
        startManagingCursor(cursor);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_messages, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.menu_login);

        if (mLoginManager.isLoggedIn()) {
            item.setTitle("Logout");
        } else {
            item.setTitle("Login");
        }

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_login:
                if (item.getTitle().toString().equalsIgnoreCase("Login")) {
                    Intent login = new Intent(this, LoginActivity.class);
                    startActivityForResult(login, LOGIN_REQUEST_CODE);
                } else {
                    mLoginManager.logout();
                    new LogoutTask(new OnLoginTaskCompleteListener() {

                        @Override
                        public void onSuccess(Login login) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onError(String message, Exception e) {
                            // TODO Auto-generated method stub

                        }
                    }).execute();
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                    Intent login = new Intent(this, LoginActivity.class);
                    startActivityForResult(login, LOGIN_REQUEST_CODE);
                }
                return true;
            case R.id.menu_refresh:
                requestId = mServiceHelper.getMessages();
                setProgressBarIndeterminateVisibility(true);
                return true;
            case R.id.menu_clear:
                requestId = mServiceHelper.deleteMessages();
                setProgressBarIndeterminateVisibility(true);
            default:
                return false;
        }
    }

    public class LogoutTask extends AsyncTask<Void, Void, RestMethodResult<Login>> {

        private final String TAG = LogoutTask.class.getSimpleName();

        private OnLoginTaskCompleteListener mListener;

        public LogoutTask(OnLoginTaskCompleteListener listener) {
            mListener = listener;
        }

        @Override
        protected RestMethodResult<Login> doInBackground(Void... params) {

            //            Login login = new Login(params[0], params[1]);
            RestMethod<Login> postLogoutRestMethod = new PostLogoutRestMethod(getApplicationContext());
            return postLogoutRestMethod.execute();

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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(MessagesActivity.this, Messages.CONTENT_URI, MessagesTable.DISPLAY_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case LOADER_ID:
                // The asynchronous load is complete and the data
                // is now available for use. Only now can we associate
                // the queried Cursor with the SimpleCursorAdapter.
                mAdapter.swapCursor(cursor);
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // For whatever reason, the Loader's data is now unavailable.
        // Remove any references to the old data by replacing it with
        // a null Cursor.
         mAdapter.swapCursor(null);
    }

}
