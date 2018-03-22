package wangbin.graduation.com.camera2demo.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import wangbin.graduation.com.camera2demo.Entity.Folder;
import wangbin.graduation.com.camera2demo.Entity.Image;
import wangbin.graduation.com.camera2demo.PreviewActivity;
import wangbin.graduation.com.camera2demo.R;

/**
 * Created by momo on 2018/3/12.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private Context mContext;
    private List<Image> mImageList;
    private ArrayList<Integer> mSelectList = new ArrayList<>();
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
                Intent intent = new Intent(mContext, PreviewActivity.class);
                intent.putIntegerArrayListExtra("selectPosList", mSelectList);
                intent.putExtra("allImageList", (Serializable) mAllImageList);
                intent.putExtra("selectPos", mPreFolderImages + position);
                ((Activity) mContext).startActivity(intent);
            }
        });

        if (hasSelected(position)) {
            holder.mSelectImage.setSelected(true);
            Glide.with(mContext).load(R.drawable.full_circle).into(holder.mSelectImage);
        } else {
            holder.mSelectImage.setSelected(false);
            Glide.with(mContext).load(R.drawable.circle).into(holder.mSelectImage);
        }

        holder.mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.isSelected()) {
                    mSelectList.remove((Object) (mPreFolderImages + position));
                    view.setSelected(false);
                    Glide.with(mContext).load(R.drawable.circle).into(holder.mSelectImage);
                } else {
                    mSelectList.add(mPreFolderImages + position);
                    view.setSelected(true);
                    Glide.with(mContext).load(R.drawable.full_circle).into(holder.mSelectImage);
                }
            }
        });
    }

    private void preFolderImageNum() {
        for (int i = 0; i < mFolderPos; i++) {
            mPreFolderImages += mFolderList.get(i).getImageList().size();
        }
    }

    private boolean hasSelected(int position) {
        for (Integer i : mSelectList) {
            if (position == i) {
                return true;
            }
        }
        return false;
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
        private ImageView mSelectImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.pic_image_adapter);
            ViewGroup.LayoutParams layoutParams = mImageView.getLayoutParams();
            layoutParams.width = mItemW;
            layoutParams.height = mItemW;
            mSelectImage = (ImageView) itemView.findViewById(R.id.circle_image_adapter);
        }
    }

}