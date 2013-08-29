package com.afzaln.mi_chat.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.afzaln.mi_chat.R;
import com.afzaln.mi_chat.view.MyViewPager;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

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
        String[] imgLinksList = extras.getStringArray("imgLinksList");
        int imgIndex = extras.getInt("imgIndex");
        int thumbnailTop = extras.getInt("top");
        int thumbnailLeft = extras.getInt("left");
        int thumbnailWidth = extras.getInt("width");
        int thumbnailHeight = extras.getInt("height");

        mViewPager.setAdapter(new ImagesPagerAdapter(imgLinksList, thumbnailTop, thumbnailLeft, thumbnailWidth, thumbnailHeight));
        mViewPager.setCurrentItem(imgIndex);

    }

    static class ImagesPagerAdapter extends PagerAdapter {

        private String[] mImgLinksList;
        private int mThumbnailTop;
        private int mThumbnailLeft;
        private int mThumbnailWidth;
        private int mThumbnailHeight;
        private int mLeftDelta;
        private int mTopDelta;
        private float mWidthScale;
        private float mHeightScale;


        public ImagesPagerAdapter(String[] imgLinksList, int thumbnailTop, int thumbnailLeft, int thumbnailWidth, int thumbnailHeight) {
            mImgLinksList = imgLinksList;
            mThumbnailTop = thumbnailTop;
            mThumbnailLeft = thumbnailLeft;
            mThumbnailWidth = thumbnailWidth;
            mThumbnailHeight = thumbnailHeight;

        }

        @Override
        public int getCount() {
            return mImgLinksList.length;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            photoView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            container.addView(photoView);

            UrlImageViewHelper.setUrlDrawable(photoView, mImgLinksList[position], R.drawable.placeholder, new UrlImageViewCallback() {
                @Override
                public void onLoaded(final ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
                    ViewTreeObserver observer = imageView.getViewTreeObserver();
                    if (observer != null) {
                        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                            @Override
                            public boolean onPreDraw() {
                                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                                int[] screenLocation = new int[2];
                                imageView.getLocationOnScreen(screenLocation);
                                mLeftDelta = mThumbnailLeft - screenLocation[0];
                                mTopDelta = mThumbnailTop - screenLocation[1];

                                mWidthScale = (float) mThumbnailWidth / imageView.getWidth();
                                mHeightScale = (float) mThumbnailHeight / imageView.getHeight();

                                runEnterAnimation(imageView);

                                return true;
                            }
                        });
                    }
//                    ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
//                    scale.setDuration(300);
//                    imageView.startAnimation(scale);
                }
            });

            return photoView;
        }

        private void runEnterAnimation(ImageView imageView) {
            final long duration = 500;

            imageView.setPivotX(0);
            imageView.setPivotY(0);
            imageView.setScaleX(mWidthScale);
            imageView.setScaleY(mHeightScale);
            imageView.setTranslationX(mLeftDelta);
            imageView.setTranslationY(mTopDelta);

            imageView.animate().setDuration(duration).
                    scaleX(1).scaleY(1).
                    translationX(0).translationY(0).
                    setInterpolator(new DecelerateInterpolator());

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