package wangbin.graduation.com.camera2demo.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import wangbin.graduation.com.camera2demo.Entity.Folder;
import wangbin.graduation.com.camera2demo.R;

/**
 * Created by momo on 2018/3/9.
 */

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {

    private Context mContext;
    private List<Folder> mFolderList = new ArrayList<>();
    private OnItemClickListener mItemClickListener = null;
    public FolderAdapter(Context context){
        this.mContext = context;
    }

    public static interface OnItemClickListener{
        void OnItemClick(View view, int pos,Folder folder);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder_adapter,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener!=null)
                    mItemClickListener.OnItemClick(v,(int)v.getTag(),mFolderList.get((int)v.getTag()));
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        Folder folder = mFolderList.get(position);
        Glide.with(mContext).load(folder.getImageList().get(0).getPath())
                .centerCrop()
                .into(holder.mFolderCover);
        holder.mFolderName.setText(folder.getName());
        holder.mFolderPicNum.setText(String.valueOf(folder.getImageList().size())+"张");


    }

    @Override
    public int getItemCount() {
        return mFolderList == null?0:mFolderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView mFolderCover;
        public TextView mFolderName;
        public TextView mFolderPicNum;

        public ViewHolder(View itemView) {
            super(itemView);
            mFolderCover = (ImageView) itemView.findViewById(R.id.cover_folder);
            mFolderName = (TextView) itemView.findViewById(R.id.name_folder);
            mFolderPicNum =(TextView) itemView.findViewById(R.id.pic_num_folder);
        }
    }

    public void setData(List<Folder> list){
        if (list!=null&&list.size()>0)
            mFolderList = list;
        else
            mFolderList.clear();
        notifyDataSetChanged();
    }
}
