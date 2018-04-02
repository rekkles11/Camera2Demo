package wangbin.graduation.com.camera2demo.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import java.util.List;

import wangbin.graduation.com.camera2demo.R;

/**
 * Created by momo on 2018/3/30.
 */

public class ThumbImageAdapter extends RecyclerView.Adapter<ThumbImageAdapter.ViewHolder> {


    private Context mContext;
    private List<Bitmap> mList;
    private static int mItemW;
    private int mLeftBlack = 0;
    private int mRightBlack =0;
    private int mBlackNum =0;
    public ThumbImageAdapter(Context context, List<Bitmap> list,int itemW){
        this.mContext = context;
        this.mList = list;
        this.mItemW = itemW;
        this.mLeftBlack = itemW;
        this.mRightBlack = itemW;
        this.mBlackNum = 2;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_thumb_image,parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        if (position > 0 && position < mList.size()+1) {
            holder.mThumbImage.setImageBitmap(mList.get(position - 1));
        } else if (position==0){
            ViewGroup.LayoutParams layoutParams = holder.mThumbImage.getLayoutParams();
            layoutParams.width = mLeftBlack;
            holder.mThumbImage.setImageDrawable(AppCompatResources.getDrawable(mContext, R.color.backgroundAlbum));

        }else if (position == mList.size()+1){
            ViewGroup.LayoutParams layoutParams = holder.mThumbImage.getLayoutParams();
            layoutParams.width = mRightBlack;
            holder.mThumbImage.setImageDrawable(AppCompatResources.getDrawable(mContext, R.color.backgroundAlbum));
        }else {
            holder.mThumbImage.setImageDrawable(AppCompatResources.getDrawable(mContext, R.color.backgroundAlbum));
        }
    }

    @Override
    public int getItemCount() {
        return mList == null? 0 : mList.size()+mBlackNum;
    }


    public void addView(int left,int right){
        mLeftBlack = mItemW+left;
        mRightBlack = mItemW +right;
    }

    public  static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView mThumbImage;
        public ViewHolder(View itemView) {
            super(itemView);
            mThumbImage = (ImageView) itemView.findViewById(R.id.image_adapter_thumb);
            ViewGroup.LayoutParams layoutParams = mThumbImage.getLayoutParams();
            layoutParams.width = mItemW;
        }
    }
}
