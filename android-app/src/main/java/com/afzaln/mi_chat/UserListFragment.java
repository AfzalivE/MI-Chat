package com.afzaln.mi_chat;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.afzaln.mi_chat.provider.ProviderContract.UsersTable;

public class UserListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int USER_LOADER = 0;

    UsersCursorAdapter mAdapter;

    private View mUserList;
    private TextView mUsersOnline;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mUserList = inflater.inflate(R.layout.user_list, null);

        getLoaderManager().initLoader(USER_LOADER, null, this);

        return mUserList;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView userListView = (ListView) mUserList.findViewById(R.id.userlist);

        mAdapter = new UsersCursorAdapter(getActivity(), null, 0);
        userListView.setAdapter(mAdapter);

        mUsersOnline = (TextView) mUserList.findViewById(R.id.users_online);
        mUsersOnline.setText(getResources().getQuantityString(R.plurals.numberofUsers, 0, 0));

    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        switch (loaderId) {
            case USER_LOADER:
                return new CursorLoader(getActivity(), UsersTable.CONTENT_URI, new String[] {UsersTable._ID, UsersTable.USERNAME, UsersTable.USERROLE}, null, null, null);
            default:
                // invalid id was passed
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
        mUsersOnline.setText(getResources().getQuantityString(R.plurals.numberofUsers, mAdapter.getCount(), mAdapter.getCount()));
    }

    /*
     * Clears out the adapter's reference to the Cursor. This prevents memory
     * leaks.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> cursor) {
        mAdapter.changeCursor(null);
    }
}
