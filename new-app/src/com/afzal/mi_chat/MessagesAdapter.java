package com.afzal.mi_chat;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MessagesAdapter extends CursorAdapter {

    public MessagesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View listItemView = LayoutInflater.from(context).inflate(R.layout.message_list_item, parent, false);

        ViewHolder holder = new ViewHolder();

        holder.usernameView = (TextView) listItemView.findViewById(R.id.username);
        holder.timestampView = (TextView) listItemView.findViewById(R.id.timestamp);
        holder.messageView = (TextView) listItemView.findViewById(R.id.message);

        listItemView.setTag(holder);
        return listItemView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Get username, timestamp and message from cursor
        String username = "username";
        String timestamp = "22:22:00";
        String message = "message";

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.usernameView.setText(username);
        holder.timestampView.setText(timestamp);
        holder.messageView.setText(message);
    }

    static class ViewHolder {
        TextView usernameView;
        TextView timestampView;
        TextView messageView;
    }
}
