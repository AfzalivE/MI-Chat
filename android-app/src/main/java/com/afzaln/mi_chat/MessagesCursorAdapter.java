package com.afzaln.mi_chat;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.afzaln.mi_chat.R.color;
import com.afzaln.mi_chat.provider.ProviderContract.MessagesTable;
import com.afzaln.mi_chat.resource.Message;
import com.afzaln.mi_chat.view.MessageListView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessagesCursorAdapter extends CursorAdapter {

    private static final String TAG = MessagesCursorAdapter.class.getSimpleName();

    private static final int MOD_USER_ROLE = 2;
    private static final int ADMIN_USER_ROLE = 3;

    private final int mAdminNameColor;
    private final int mModNameColor;
    private final int mUserameColor;

    public MessagesCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mAdminNameColor = context.getResources().getColor(color.admin_name);
        mModNameColor = context.getResources().getColor(color.mod_name);
        mUserameColor = context.getResources().getColor(color.normal_text);
    }

    private int getItemViewType(Cursor cursor) {
        return cursor.getInt(cursor.getColumnIndex(MessagesTable.TYPE));
    }

    public long getItemDateTime(int position) {
        Cursor cursor = (Cursor) getItem(position);
        return cursor.getLong(cursor.getColumnIndex(MessagesTable.DATETIME));
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = (Cursor) getItem(position);
        return getItemViewType(cursor);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View listItemView = null;

        ViewHolder holder = new ViewHolder();

        switch (getItemViewType(cursor)) {
            case Message.NORMAL_TYPE:
                listItemView = LayoutInflater.from(context).inflate(R.layout.message_list_item, parent, false);
                break;
            case Message.ACTION_TYPE:
                listItemView = LayoutInflater.from(context).inflate(R.layout.action_list_item, parent, false);
                break;
        }

        holder.userNameView = (TextView) listItemView.findViewById(R.id.username);
        holder.timestampView = (TextView) listItemView.findViewById(R.id.timestamp);
        holder.messageView = (TextView) listItemView.findViewById(R.id.message);
        holder.imagesButton = (Button) listItemView.findViewById(R.id.images);

        listItemView.setTag(holder);
        return listItemView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO API 9 compatibility
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
        String imgLinks = cursor.getString(cursor.getColumnIndex(MessagesTable.IMGLINKS));
        long timestamp = cursor.getLong(cursor.getColumnIndex(MessagesTable.DATETIME));

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.userNameView.setText(userName);
        formatUsername(userRole, holder.userNameView);
        holder.timestampView.setText(getDate(timestamp));
        holder.messageView.setText(Html.fromHtml(message));

        List<String> imgLinksList = new ArrayList<String>();
        // TODO remove this condition when ACTION_TYPE can also display images
        if (getItemViewType(cursor) == Message.NORMAL_TYPE) {
            if (imgLinks != null) {
                String[] imgLinksArr = StringUtils.split(imgLinks, "|");
                Collections.addAll(imgLinksList, imgLinksArr);
                holder.imagesButton.setVisibility(View.VISIBLE);
                holder.imagesButton.setText(context.getResources().getQuantityString(R.plurals.numberOfImages, imgLinksList.size(), imgLinksList.size()));
            } else {
                holder.imagesButton.setVisibility(View.GONE);
            }
        }
    }

    private void formatUsername(int userRole, TextView userNameView) {
        switch (userRole) {
            case ADMIN_USER_ROLE:
                userNameView.setTextColor(mAdminNameColor);
                break;
            case MOD_USER_ROLE:
                userNameView.setTextColor(mModNameColor);
                break;
            default:
                userNameView.setTextColor(mUserameColor);
                break;
        }
    }

    private CharSequence getDate(long timestamp) {
        return DateUtils.getRelativeTimeSpanString(timestamp, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);
    }

    static class ViewHolder {
        TextView userNameView;
        TextView timestampView;
        TextView messageView;
        Button imagesButton;
    }
}
