package com.afzaln.mi_chat;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afzaln.mi_chat.R.color;
import com.afzaln.mi_chat.provider.ProviderContract.MessagesTable;

public class MessagesCursorAdapter extends CursorAdapter {

    private static final String TAG = MessagesCursorAdapter.class.getSimpleName();
    private static final int MOD_USER_ROLE = 2;
    private final int mUserameColor;
    private final int mModNameColor;

    public MessagesCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mModNameColor = context.getResources().getColor(color.mod_name);
        mUserameColor = context.getResources().getColor(color.normal_text);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View listItemView = LayoutInflater.from(context).inflate(R.layout.message_list_item, parent, false);

        ViewHolder holder = new ViewHolder();

        holder.userNameView = (TextView) listItemView.findViewById(R.id.username);
        holder.timestampView = (TextView) listItemView.findViewById(R.id.timestamp);
        holder.messageView = (TextView) listItemView.findViewById(R.id.message);

        listItemView.setTag(holder);
        return listItemView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ((MessageListView) parent).setItemChecked(position, true);
        View view = super.getView(position, convertView, parent);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Get username, timestamp and message from cursor
        String userName = cursor.getString(cursor.getColumnIndex(MessagesTable.USERNAME));
        int userRole = cursor.getInt(cursor.getColumnIndex(MessagesTable.USERROLE));
        String message = cursor.getString(cursor.getColumnIndex(MessagesTable.MESSAGE));
        long timestamp = cursor.getLong(cursor.getColumnIndex(MessagesTable.DATETIME));

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.userNameView.setText(userName);
        formatUsername(userRole, holder.userNameView);
        holder.timestampView.setText(getDate(timestamp));
        holder.messageView.setText(Html.fromHtml(message));
    }

    private void formatUsername(int userRole, TextView userNameView) {
        if (userRole == MOD_USER_ROLE) {
            userNameView.setTextColor(mModNameColor);
        } else {
            userNameView.setTextColor(mUserameColor);
        }
    }


    private CharSequence getDate(long timestamp) {
        return DateUtils.getRelativeTimeSpanString(timestamp, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);
    }

    static class ViewHolder {
        TextView userNameView;
        TextView timestampView;
        TextView messageView;
    }
}
