package wangbin.graduation.com.camera2demo.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import wangbin.graduation.com.camera2demo.Entity.Image;

/**
 * Created by momo on 2018/3/12.
 */

public class PreviewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<Image> mImageList;
    public PreviewPagerAdapter(Context context,List<Image> list){
        this.mContext = context;
        this.mImageList = list;
    }

    @Override
    public int getCount() {
        return mImageList == null?0:mImageList.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ImageView imageView = (ImageView)object;
        container.removeView(imageView);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        Display display = ((Activity)mContext).getWindowManager().getDefaultDisplay();
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(display.getWidth(),display.getHeight());
        imageView.setLayoutParams(layoutParams);
        Glide.with(mContext)
                .load(mImageList.get(position).getPath())
                .into(imageView);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (View) object;
    }
}
