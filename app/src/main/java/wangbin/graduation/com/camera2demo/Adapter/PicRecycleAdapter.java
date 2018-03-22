package wangbin.graduation.com.camera2demo.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import wangbin.graduation.com.camera2demo.Entity.Image;
import wangbin.graduation.com.camera2demo.R;

/**
 * Created by momo on 2018/3/9.
 */

public class PicRecycleAdapter extends RecyclerView.Adapter<PicRecycleAdapter.ViewHolder> {

    private Context mContext;
    private List<Integer> mPosList;
    private List<Image> mAllImageList;
    private OnImageClickListener mOnImageClickListener = null;
    private int selectedItem = 0;

    public PicRecycleAdapter(Context context, List<Integer> list, List<Image> allImageList, int selectedItem) {
        this.mContext = context;
        this.mPosList = list;
        this.mAllImageList = allImageList;
        setSelectedItem(selectedItem);
    }

    public static interface OnImageClickListener {
        void click(int preSelect);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preview_adapter,
                                                                     parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (mPosList == null || mPosList.size() <= 0 || mAllImageList == null || mAllImageList.size() <= 0) return;
        final String path = mAllImageList.get(mPosList.get(position)).getPath();
        if (selectedItem == mPosList.get(position)) {
            holder.mImageView.setSelected(true);
        } else {
            holder.mImageView.setSelected(false);
        }
        Glide.with(mContext)
             .load(path)
             .centerCrop()
             .into(holder.mImageView);
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnImageClickListener.click(mPosList.get(position));
            }
        });
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        mOnImageClickListener = listener;
    }

    /**
     * 设置当前展示的图片（ which 是相对mAllImageList）
     * @param which
     */
    public void setSelectedItem(int which) {
        if (isInMyPosList(which)) {
            selectedItem = which;
        } else {
            selectedItem = -1;
        }

    }

    /**
     * 判断当前选中图片的index 是否在 mPosList之中
     * @param index
     * @return
     */
    private boolean isInMyPosList(int index) {
        for (Integer i : mPosList) {
            if (i == index) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return mPosList == null ? 0 : mPosList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.pic_preview_adapter);
        }
    }

}
