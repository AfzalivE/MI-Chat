package com.afzaln.mi_chat.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.afzaln.mi_chat.R;
import com.afzaln.mi_chat.view.MyViewPager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by afzal on 2013-08-23.
 */
public class ImageActivity extends FragmentActivity {

    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_activity_layout);

        mViewPager = (MyViewPager) findViewById(R.id.image_pager);

        ArrayList<String> imgLinksList = getIntent().getExtras().getStringArrayList("imgLinksList");

        mViewPager.setAdapter(new ImagesPagerAdapter(imgLinksList));

//        Picasso.with(this)
//                .load(imgLinksList.get(0))
//                .placeholder(R.drawable.placeholder)
//                .error(R.drawable.error)
//                .into(imageView);
    }

    static class ImagesPagerAdapter extends PagerAdapter {

        private ArrayList<String> mImgLinksList;

        public ImagesPagerAdapter(ArrayList<String> imgLinksList) {
            mImgLinksList = imgLinksList;
        }

        @Override
        public int getCount() {
            return mImgLinksList.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            Picasso.with(container.getContext())
                   .load(mImgLinksList.get(position))
                   .error(R.drawable.error)
                   .into(photoView);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
