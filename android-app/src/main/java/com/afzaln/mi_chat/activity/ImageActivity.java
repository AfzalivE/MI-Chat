package com.afzaln.mi_chat.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.ViewAnimator;

import com.afzaln.mi_chat.R;
import com.afzaln.mi_chat.view.MyViewPager;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by afzal on 2013-08-23.
 */
public class ImageActivity extends FragmentActivity {

    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        mViewPager = (MyViewPager) findViewById(R.id.image_pager);

        Bundle extras = getIntent().getExtras();
        String[] imgLinksList = extras.getStringArray("imgLinksList");
        int imgIndex = extras.getInt("imgIndex");

        mViewPager.setAdapter(new ImagesPagerAdapter(imgLinksList));
        mViewPager.setCurrentItem(imgIndex);

    }

    static class ImagesPagerAdapter extends PagerAdapter {

        private String[] mImgLinksList;

        public ImagesPagerAdapter(String[] imgLinksList) {
            mImgLinksList = imgLinksList;
        }

        @Override
        public int getCount() {
            return mImgLinksList.length;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {

            final ViewAnimator viewAnim = (ViewAnimator) LayoutInflater.from(container.getContext()).inflate(R.layout.item_photo, container, false);
            PhotoView photoView = (PhotoView) viewAnim.findViewById(R.id.image);
            container.addView(viewAnim);
            viewAnim.setDisplayedChild(0);

            UrlImageViewHelper.setUrlDrawable(photoView, mImgLinksList[position], new UrlImageViewCallback() {
                @Override
                public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
                    if (loadedBitmap == null) {
                        viewAnim.setDisplayedChild(2);
                        return;
                    }
                    viewAnim.setDisplayedChild(1);

                    ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
                    scale.setDuration(300);
                    imageView.startAnimation(scale);
                }
            });

            return viewAnim;
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