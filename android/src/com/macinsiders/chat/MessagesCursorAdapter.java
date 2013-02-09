package com.macinsiders.chat;

import com.macinsiders.chat.provider.ProviderContract.MessagesTable;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class MessagesCursorAdapter extends CursorAdapter{

    public MessagesCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View listItemView = LayoutInflater.from(context).inflate(R.layout.message_list_item, parent, false);

        ViewHolder holder = new ViewHolder();
        holder.messageIdView = (TextView) listItemView.findViewById(R.id.id);
        holder.userIdView = (TextView) listItemView.findViewById(R.id.userid);
        holder.channelIdView = (TextView) listItemView.findViewById(R.id.channelid);
        holder.userRoleView = (TextView) listItemView.findViewById(R.id.userrole);
        holder.datetimeView = (TextView) listItemView.findViewById(R.id.datetime);
        holder.usernameView = (TextView) listItemView.findViewById(R.id.username);
        holder.messageView = (TextView) listItemView.findViewById(R.id.message);

        listItemView.setTag(holder);
        return listItemView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String messageId = Long.toString(cursor.getLong(cursor.getColumnIndex(MessagesTable.REF_ID)));
        String userId = Long.toString(cursor.getLong(cursor.getColumnIndex(MessagesTable.USERID)));
        String channelId = Integer.toString(cursor.getInt(cursor.getColumnIndex(MessagesTable.CHANNELID)));
        String userRole = Integer.toString(cursor.getInt(cursor.getColumnIndex(MessagesTable.USERROLE)));
        // Why is datetime TEXT? why not long?
        String datetime = cursor.getString(cursor.getColumnIndex(MessagesTable.DATETIME));
        String username = cursor.getString(cursor.getColumnIndex(MessagesTable.USERNAME));
        String message = cursor.getString(cursor.getColumnIndex(MessagesTable.MESSAGE));

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.messageIdView.setText(messageId);
        holder.userIdView.setText(userId);
        holder.channelIdView.setText(channelId);
        holder.userRoleView.setText(userRole);
        holder.datetimeView.setText(datetime);
        holder.usernameView.setText(username);
        holder.messageView.setText(message);
    }

    static class ViewHolder {
        TextView messageIdView;
        TextView userIdView;
        TextView channelIdView;
        TextView userRoleView;
        TextView datetimeView;
        TextView usernameView;
        TextView messageView;
    }


}
