package wangbin.graduation.com.camera2demo.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;

import wangbin.graduation.com.camera2demo.Adapter.FolderAdapter;
import wangbin.graduation.com.camera2demo.R;

/**
 * Created by momo on 2018/3/9.
 */

public class TopPopup extends PopupWindow {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private LinearLayout mPopLL;
    private LayoutInflater mInflater = null;
    private FolderAdapter mFolderAdater;
    private int mWidth;
    private int mHeight;
    private View mMenuView;
    private Boolean mIsPop;

    public TopPopup(Context context, int width, int height,Boolean type,FolderAdapter folderAdapter){
        this.mContext = context;
        this.mWidth = width;
        this.mHeight = height;
        this.mIsPop = type;
        this.mFolderAdater = folderAdapter;
        initWidget();
        setPopup();
    }

    private void initWidget(){
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = mInflater.inflate(R.layout.top_popup,null);
        mRecyclerView = (RecyclerView) mMenuView.findViewById(R.id.recycle_pop);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mFolderAdater);
        mPopLL =(LinearLayout)mMenuView.findViewById(R.id.popup_layout);
    }

    private void setPopup(){

        this.setContentView(mMenuView);
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.MATCH_PARENT-1);
        this.setFocusable(true);
        ColorDrawable cd = new ColorDrawable(Color.WHITE);
        this.setBackgroundDrawable(cd);
//        this.setAnimationStyle(R.style.MyPopupWindow_anim_style);
    }
}
