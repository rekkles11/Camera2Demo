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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wangbin.graduation.com.camera2demo.Adapter.ImageAdapter;
import wangbin.graduation.com.camera2demo.Adapter.PicRecycleAdapter;
import wangbin.graduation.com.camera2demo.Adapter.PreviewPagerAdapter;
import wangbin.graduation.com.camera2demo.Entity.Image;
import wangbin.graduation.com.camera2demo.view.CircleSelectView;

/**
 * Created by momo on 2018/3/12.
 */

public class PreviewActivity extends Activity implements View.OnClickListener {
    //    private ImageView mPreImage;
    private CircleSelectView photoChoose;
    private ViewPager mViewPager;
    private RecyclerView mRecyclerView;
    private ArrayList<Integer> mSelectPosList = new ArrayList<>();
    private TextView mEdit;
    private PicRecycleAdapter mAdapter;
    private PreviewPagerAdapter mPagerAdapter;
    private int mSelectPos;
    private List<Image> mAllImageList = Collections.synchronizedList(new ArrayList<Image>());;
    private ImageView mBackView;
    private View mRecycleFL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        initData();
        initView();
    }

    private void initData() {
        mSelectPosList = ImageAdapter.mSelectList;
        mSelectPos = getIntent().getIntExtra("selectPos", -1);
        mAllImageList = (getIntent().getParcelableArrayListExtra("allImageList"));

        if (mSelectPos == -1) {
            finish();
        }
    }

    private void initView() {
        photoChoose = findViewById(R.id.photo_choose);
        photoChoose.setOnClickListener(this);
        mBackView = (ImageView) findViewById(R.id.back_preview);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_preview);
        findViewById(R.id.top_tab).setAlpha(0.95f);
        mRecycleFL = findViewById(R.id.recycle_fl);
        mRecycleFL.setAlpha(0.95f);
        if (mSelectPosList!=null&&mSelectPosList.size()>0){
            mRecycleFL.setVisibility(View.VISIBLE);
        }
        findViewById(R.id.bottom_tab).setAlpha(0.95f);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_preview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mEdit = (TextView) findViewById(R.id.edit_preview);
        mAdapter = new PicRecycleAdapter(this, mSelectPosList, mAllImageList, mSelectPos);
        mRecyclerView.setAdapter(mAdapter);
        mPagerAdapter = new PreviewPagerAdapter(this, mAllImageList);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(mSelectPos);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mSelectPos = position;
                if (mAdapter.setSelectedItem(position)) {
                    photoChoose.setNumber(hasSelected(position));
                    photoChoose.setChoose(true);
                } else {
                    photoChoose.setChoose(false);
                }
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
                photoChoose.setNumber(hasSelected(mSelectPos));
                photoChoose.setChoose(true);
                mAdapter.notifyDataSetChanged();
                mViewPager.setCurrentItem(preSelect);
            }
        });
        mEdit.setOnClickListener(this);
        mBackView.setOnClickListener(this);

        int i = hasSelected(mSelectPos);
        if (i > 0) {
            photoChoose.setNumber(i);
            photoChoose.setChoose(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 101) {
            if (mSelectPosList.size() >= 9) {
                return;
            }
            String path = data.getStringExtra("imagePath");
            mAllImageList.get(mSelectPos).setPath(path);
            mPagerAdapter.setIsNewPath(mSelectPos);
            mPagerAdapter.notifyDataSetChanged();
            mViewPager.setCurrentItem(mSelectPos);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_preview:
                PreviewActivity.this.finish();
                break;
            case R.id.edit_preview:
                Intent intent = new Intent(PreviewActivity.this, EditImageActivity.class);
                intent.putExtra("editImage", mAllImageList.get(mViewPager.getCurrentItem()).getPath());
                startActivityForResult(intent, 100);
                break;
            case R.id.photo_choose:
                if (photoChoose.isSelected()) {
                    int index = photoChoose.getNumber();
                    if (index != -1) {
                        mSelectPosList.remove(index - 1);
                        photoChoose.setChoose(false);
                        mAdapter.setSelectedItem(mViewPager.getCurrentItem());
                    }
                } else {
                    if (mSelectPosList.size() >= 9) {
                        return;
                    }
                    mSelectPos = mViewPager.getCurrentItem();
                    mSelectPosList.add(mSelectPos);
                    photoChoose.setNumber(mSelectPosList.size());
                    photoChoose.setChoose(true);
                    mAdapter.setSelectedItem(mSelectPos);
                }
                if (mSelectPosList.size()>0&&mSelectPosList!=null){
                    mRecycleFL.setVisibility(View.VISIBLE);
                }else {
                    mRecycleFL.setVisibility(View.GONE);
                }
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    private int hasSelected(int position) {
        for (int i = 0; i < mSelectPosList.size(); i++) {
            if (position == mSelectPosList.get(i)) {
                return i + 1;
            }
        }
        return -1;
    }
}
