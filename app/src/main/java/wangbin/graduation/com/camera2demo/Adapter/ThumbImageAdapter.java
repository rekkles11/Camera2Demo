package wangbin.graduation.com.camera2demo.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
    private int mItemW;
    private int mBlackWidth;
    private Boolean mIsBlack = false;
    public ThumbImageAdapter(Context context, List<Bitmap> list,int itemW){
        this.mContext = context;
        this.mList = list;
        this.mItemW = itemW;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_thumb_image,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position>0&&position<mList.size()+1) {
            if (mIsBlack&&position == mList.size()){
                ViewGroup.LayoutParams layoutParams = holder.mThumbImage.getLayoutParams();
                layoutParams.width = mBlackWidth;
                holder.mThumbImage.setImageDrawable(AppCompatResources.getDrawable(mContext, R.color.backgroundAlbum));
            }else {
                holder.mThumbImage.setImageBitmap(mList.get(position - 1));
            }
        }else {

            holder.mThumbImage.setImageDrawable(AppCompatResources.getDrawable(mContext, R.color.backgroundAlbum));
        }
    }

    @Override
    public int getItemCount() {
        return mList == null? 0 : mList.size()+2;
    }

    public void addView(int width){
        if (width>0) {
            if (mIsBlack) {
                mList.remove(mList.size() - 1);
            }
            Bitmap bitmap = mList.get(0).copy(Bitmap.Config.RGB_565, true);
            mList.add(bitmap);
            mIsBlack = true;
            mBlackWidth = width;
        }else {
            if (mIsBlack) {
                mList.remove(mList.size() - 1);
            }
            mIsBlack = false;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView mThumbImage;
        public ViewHolder(View itemView) {
            super(itemView);
            mThumbImage = (ImageView) itemView.findViewById(R.id.image_adapter_thumb);
            ViewGroup.LayoutParams layoutParams = mThumbImage.getLayoutParams();
            layoutParams.width = mItemW;
        }
    }
}
