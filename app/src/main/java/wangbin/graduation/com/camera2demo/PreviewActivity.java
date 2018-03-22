package wangbin.graduation.com.camera2demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import wangbin.graduation.com.camera2demo.Adapter.PicRecycleAdapter;
import wangbin.graduation.com.camera2demo.Adapter.PreviewPagerAdapter;
import wangbin.graduation.com.camera2demo.Entity.Image;

/**
 * Created by momo on 2018/3/12.
 */

public class PreviewActivity extends Activity {
//    private ImageView mPreImage;
    private ViewPager mViewPager;
    private RecyclerView mRecyclerView;
    private ArrayList<Integer> mSelectPosList = new ArrayList<>();
    private TextView mEdit;
    private PicRecycleAdapter mAdapter;
    private String mPrePath;
    private PreviewPagerAdapter mPagerAdapter;
    private int mSelectPos;
    private List<Image> mAllImageList = new ArrayList<>();
    private ImageView mBackView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        initData();
        initView();
    }

//    private void initView() {
//        mBackView = (ImageView) findViewById(R.id.back_preview);
//        mSelectPosList = (ArrayList<Integer>) getIntent().getIntegerArrayListExtra("selectPosList");
//        mSelectPos = getIntent().getIntExtra("selectPos",-1);
//        mAllImageList = (List<Image>) getIntent().getSerializableExtra("allImageList");
//        mViewPager = (ViewPager)findViewById(R.id.viewpager_preview);
//        mEdit =findViewById(R.id.edit_preview);
//        initData();
//    }/storage/emulated/0/DCIM/pic.jpg

    private void initData() {
        mSelectPosList = (ArrayList<Integer>) getIntent().getIntegerArrayListExtra("selectPosList");
        mSelectPos = getIntent().getIntExtra("selectPos", -1);
        mAllImageList = (getIntent().getParcelableArrayListExtra("allImageList"));
    }

    private void initView() {
        mBackView = (ImageView) findViewById(R.id.back_preview);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_preview);
        findViewById(R.id.top_tab).setAlpha(0.95f);
        findViewById(R.id.recycle_fl).setAlpha(0.95f);
        findViewById(R.id.bottom_tab).setAlpha(0.95f);
        mRecyclerView=(RecyclerView) findViewById(R.id.recycle_preview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mEdit = (TextView) findViewById(R.id.edit_preview);
        mAdapter = new PicRecycleAdapter(this, mSelectPosList, mAllImageList,mSelectPos);
        mRecyclerView.setAdapter(mAdapter);
        mPagerAdapter = new PreviewPagerAdapter(this,mAllImageList);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(mSelectPos);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mAdapter.setSelectedItem(position);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mAdapter.setOnImageClickListener(new PicRecycleAdapter.OnImageClickListener() {
            @Override
            public void click(int preSelect) {
                mSelectPos = preSelect;
                mAdapter.setSelectedItem(mSelectPos);
                mAdapter.notifyDataSetChanged();
                mViewPager.setCurrentItem(preSelect);
            }
        });
        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PreviewActivity.this,EditImageActivity.class);
                intent.putExtra("editImage",mAllImageList.get(mViewPager.getCurrentItem()).getPath());
                startActivityForResult(intent,100);
            }
        });
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreviewActivity.this.finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==100&&resultCode ==101){
            String path = data.getStringExtra("imagePath");
            Image image =new Image();
            image.setName(path);
            image.setPath(path);
            int index = 0;
            for (int i =0;i<mSelectPosList.size();i++){
                mSelectPosList.set(i,mSelectPosList.get(i)+1);
            }
            mSelectPosList.add(index,index);
            mAllImageList.add(index,image);
            mSelectPos = index;
            mAdapter.setSelectedItem(index);
            mAdapter.notifyDataSetChanged();
            mPagerAdapter.notifyDataSetChanged();
            mViewPager.setCurrentItem(index);
        }
    }
}
