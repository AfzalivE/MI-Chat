package com.afzaln.mi_chat.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.afzaln.mi_chat.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by afzal on 2013-08-23.
 */
public class ImageActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_activity_layout);

        ArrayList<String> imgLinksList = getIntent().getExtras().getStringArrayList("imgLinksList");

        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        Picasso.with(this)
                .load(imgLinksList.get(0))
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(imageView);
    }
}
