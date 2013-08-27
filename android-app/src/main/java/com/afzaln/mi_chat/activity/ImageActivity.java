package com.afzaln.mi_chat.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ViewAnimator;

import com.afzaln.mi_chat.R;
import com.afzaln.mi_chat.view.MyViewPager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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
            ProgressBar progressBar = new ProgressBar(container.getContext());
            progressBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

            final ViewAnimator photoAnimator = new ViewAnimator(container.getContext());
            photoAnimator.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            photoAnimator.addView(photoView);
            photoAnimator.addView(progressBar);
            photoAnimator.setDisplayedChild(1);

            container.addView(photoAnimator);
            Picasso picasso = Picasso.with(container.getContext());
            picasso.load(mImgLinksList.get(position))
                    .error(R.drawable.error)
                    .resizeDimen(R.dimen.default_image_size, R.dimen.default_image_size)
                    .centerInside()
                    .into(photoView, new Callback.EmptyCallback() {
                        @Override
                        public void onSuccess() {
                            photoAnimator.setDisplayedChild(0);
                        }

                        @Override
                        public void onError() {
                            photoAnimator.setDisplayedChild(0);
                        }
                    });

            return photoAnimator;
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
