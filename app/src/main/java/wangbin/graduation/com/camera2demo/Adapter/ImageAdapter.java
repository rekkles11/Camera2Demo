package wangbin.graduation.com.camera2demo.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import wangbin.graduation.com.camera2demo.EditVideoActivity;
import wangbin.graduation.com.camera2demo.Entity.Folder;
import wangbin.graduation.com.camera2demo.Entity.Image;
import wangbin.graduation.com.camera2demo.PreviewActivity;
import wangbin.graduation.com.camera2demo.R;
import wangbin.graduation.com.camera2demo.view.CircleSelectView;
import wangbin.graduation.com.camera2demo.view.EditVideoView;

/**
 * Created by momo on 2018/3/12.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private Context mContext;
    private List<Image> mImageList;
    public static ArrayList<Integer> mSelectList = new ArrayList<>();
    private List<Folder> mFolderList = new ArrayList<>();
    private int mFolderPos;
    private static int mItemW;
    private boolean isSelect = false;
    private int mPreFolderImages = 0;
    private List<Image> mAllImageList = new ArrayList<>();

    public ImageAdapter(Context context, int screenW, int num) {
        this.mContext = context;
        this.mItemW = screenW / num;
        preFolderImageNum();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_image_adapter, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Glide.with(mContext)
             .load(mImageList.get(position).getPath())
             .centerCrop()
             .override(mItemW, mItemW)
             .into(holder.mImageView);
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mImageList.get(position).isVideo()){

                    Intent intent = new Intent(mContext, EditVideoActivity.class);
                    intent.putExtra("videoPath", mImageList.get(position).getPath());
                    ((Activity) mContext).startActivity(intent);
                }else {
                    Intent intent = new Intent(mContext, PreviewActivity.class);
                    intent.putExtra("allImageList", (Serializable) mAllImageList);
                    intent.putExtra("selectPos", mPreFolderImages + position);
                    ((Activity) mContext).startActivity(intent);
                }
            }
        });

        if (mImageList.get(position).isVideo()) {
            holder.mTv.setText(mImageList.get(position).getDuration());
            holder.mSelectImage.setVisibility(View.GONE);
        } else {
            holder.mTv.setText("");
            holder.mSelectImage.setVisibility(View.VISIBLE);
        }

        int num = hasSelected(position);
        if (num > 0) {
            holder.mSelectImage.setNumber(num);
            holder.mSelectImage.setChoose(true);
        } else {
            holder.mSelectImage.setChoose(false);
        }

        holder.mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.isSelected()) {
                    mSelectList.remove((Object) (mPreFolderImages + position));
                    ((CircleSelectView) view).setChoose(false);
                    notifyDataSetChanged();
                } else {
                    if (mSelectList.size() >= 9) {
                        return;
                    }
                    ((CircleSelectView) view).setNumber(mSelectList.size() + 1);
                    mSelectList.add(mPreFolderImages + position);
                    ((CircleSelectView) view).setChoose(true);
                }
            }
        });
    }

    private void preFolderImageNum() {
        for (int i = 0; i < mFolderPos; i++) {
            mPreFolderImages += mFolderList.get(i).getImageList().size();
        }
    }

    private int hasSelected(int position) {
        for (int i = 0; i < mSelectList.size(); i++) {
            if (position == mSelectList.get(i)) {
                return i + 1;
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return mImageList == null ? 0 : mImageList.size();
    }

    public void setData(List<Image> imageList, int pos) {
        mFolderPos = pos;
        if (imageList != null && imageList.size() > 0)
            mImageList = imageList;
        else
            mImageList.clear();
        notifyDataSetChanged();
    }

    public void setFolderList(List<Folder> list) {
        this.mFolderList = list;
    }

    public void setAllImageList(List<Image> imageList) {
        this.mAllImageList = imageList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private CircleSelectView mSelectImage;
        private TextView mTv;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.pic_image_adapter);
            mTv = itemView.findViewById(R.id.tv);
            ViewGroup.LayoutParams layoutParams = mImageView.getLayoutParams();
            layoutParams.width = mItemW;
            layoutParams.height = mItemW;
            mSelectImage = (CircleSelectView) itemView.findViewById(R.id.circle_image_adapter);
        }
    }

}