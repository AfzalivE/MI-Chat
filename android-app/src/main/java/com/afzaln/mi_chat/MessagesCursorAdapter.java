package com.afzaln.mi_chat;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afzaln.mi_chat.R.color;
import com.afzaln.mi_chat.activity.ImageActivity;
import com.afzaln.mi_chat.provider.ProviderContract.MessagesTable;
import com.afzaln.mi_chat.resource.Message;
import com.afzaln.mi_chat.view.MessageListView;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;

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
        holder.imagesButton = (Button) listItemView.findViewById(R.id.show_images);
        holder.imageContainer = (LinearLayout) listItemView.findViewById(R.id.image_container);

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
    public void bindView(final View view, final Context context, Cursor cursor) {
        // Get username, timestamp and message from cursor
        String userName = cursor.getString(cursor.getColumnIndex(MessagesTable.USERNAME));
        int userRole = cursor.getInt(cursor.getColumnIndex(MessagesTable.USERROLE));
        String message = cursor.getString(cursor.getColumnIndex(MessagesTable.MESSAGE));
        String imgLinks = cursor.getString(cursor.getColumnIndex(MessagesTable.IMGLINKS));
        long timestamp = cursor.getLong(cursor.getColumnIndex(MessagesTable.DATETIME));

        final ViewHolder holder = (ViewHolder) view.getTag();
        setTextViews(holder, userName, userRole, message, timestamp);

        final ArrayList<String> imgLinksList = new ArrayList<String>();
        // TODO adjust action_list_item layout to accomodate images nicely
        if (getItemViewType(cursor) == Message.NORMAL_TYPE) {
            if (imgLinks != null) {
                Collections.addAll(imgLinksList, StringUtils.split(imgLinks, "|"));
                holder.imagesButton.setVisibility(View.VISIBLE);
                holder.imagesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.imageContainer.getVisibility() == View.VISIBLE) {
                            holder.imageContainer.setVisibility(View.GONE);
                        } else {
                            showImages(holder, imgLinksList, context);
                        }
                    }
                });

                holder.imagesButton.setText(context.getResources().getQuantityString(R.plurals.numberOfImages, imgLinksList.size(), imgLinksList.size()));
            } else {
                holder.imagesButton.setVisibility(View.GONE);
                holder.imageContainer.setVisibility(View.GONE);
            }
        }
    }

    private void setTextViews(ViewHolder holder, String userName, int userRole, String message, long timestamp) {
        holder.userNameView.setText(userName);
        formatUsername(userRole, holder.userNameView);
        holder.timestampView.setText(getDate(timestamp));
        if (message.isEmpty()) {
            holder.messageView.setVisibility(View.GONE);
        } else {
            holder.messageView.setVisibility(View.VISIBLE);
            holder.messageView.setText(Html.fromHtml(message));
        }
    }

    private void showImages(final ViewHolder holder, final ArrayList<String> imgLinksList, Context context) {
        holder.imageContainer.setVisibility(View.VISIBLE);
        holder.imageContainer.removeAllViews();

        // Trigger the download of the URL asynchronously into the image view.
        for (int i = 0; i < imgLinksList.size(); i++) {
            final int imgIndex = i;
            ImageView image = new ImageView(context);
            image.setLayoutParams(new ViewGroup.LayoutParams(200, 200));

            holder.imageContainer.addView(image);

            UrlImageViewHelper.setUrlDrawable(image, imgLinksList.get(i), R.drawable.placeholder, new UrlImageViewCallback() {
                @Override
                public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
                    ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
                    scale.setDuration(300);
                    scale.setInterpolator(new OvershootInterpolator());
                    imageView.startAnimation(scale);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, ImageActivity.class);
                            Bundle extras = new Bundle();
                            extras.putStringArrayList("imgLinksList", imgLinksList);
                            extras.putInt("imgIndex", imgIndex);
                            intent.putExtras(extras);
                            mContext.startActivity(intent);
                        }
                    });
                }
            });
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
        LinearLayout imageContainer;
    }

}
