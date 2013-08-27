package com.afzaln.mi_chat.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.afzaln.mi_chat.R;
import com.afzaln.mi_chat.view.MyViewPager;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

import static android.view.ViewGroup.LayoutParams;

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

        Bundle extras = getIntent().getExtras();
        ArrayList<String> imgLinksList = extras.getStringArrayList("imgLinksList");
        int imgIndex = extras.getInt("imgIndex");

        mViewPager.setAdapter(new ImagesPagerAdapter(imgLinksList));
        mViewPager.setCurrentItem(imgIndex);

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
            photoView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            container.addView(photoView);

            UrlImageViewHelper.setUrlDrawable(photoView, mImgLinksList.get(position), R.drawable.placeholder, new UrlImageViewCallback() {
                @Override
                public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
                    ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
                    scale.setDuration(300);
                    imageView.startAnimation(scale);
                }
            });

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
