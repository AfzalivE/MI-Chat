package com.afzaln.mi_chat;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afzaln.mi_chat.R.color;
import com.afzaln.mi_chat.provider.ProviderContract.UsersTable;

public class UsersCursorAdapter extends CursorAdapter {

    private final int mUserameColor;
    private final int mModNameColor;

    public UsersCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mModNameColor = context.getResources().getColor(color.mod_name);
        mUserameColor = context.getResources().getColor(color.normal_text);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View listItemView = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);

        ViewHolder holder = new ViewHolder();
        holder.userNameView = (TextView) listItemView.findViewById(R.id.user_row);

        listItemView.setTag(holder);
        return listItemView;
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String userName = cursor.getString(cursor.getColumnIndex(UsersTable.USERNAME));
        int userRole = cursor.getInt(cursor.getColumnIndex(UsersTable.USERROLE));

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.userNameView.setText(userName);
        if (userRole == 2) {
            holder.userNameView.setTextColor(mModNameColor);
        } else {
            holder.userNameView.setTextColor(mUserameColor);
        }
    }

    static class ViewHolder {
        TextView userNameView;
    }

}
