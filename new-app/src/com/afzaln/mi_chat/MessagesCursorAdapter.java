package com.afzaln.mi_chat;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afzaln.mi_chat.R.color;
import com.afzaln.mi_chat.provider.ProviderContract.MessagesTable;

public class MessagesCursorAdapter extends CursorAdapter {

    private static final String TAG = MessagesCursorAdapter.class.getSimpleName();
    private Context mContext;

    public MessagesCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
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
        View view = super.getView(position, convertView, parent);
        if (position % 2 == 1) {
            view.setBackgroundColor(mContext.getResources().getColor(R.color.alt_bg));
        } else {
            view.setBackgroundColor(Color.WHITE);
        }

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
        if (userRole == 2) {
            holder.userNameView.setTextColor(mContext.getResources().getColor(color.mod_name));
        } else {
            holder.userNameView.setTextColor(Color.rgb(77, 77, 77));
        }
        holder.timestampView.setText(getDate(timestamp));
        holder.messageView.setText(Html.fromHtml(message));
    }

    private String getDate(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(date);
    }

    static class ViewHolder {
        TextView userNameView;
        TextView timestampView;
        TextView messageView;
    }
}
