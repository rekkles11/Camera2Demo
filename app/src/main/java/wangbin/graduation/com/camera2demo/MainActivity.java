package wangbin.graduation.com.camera2demo;

import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import wangbin.graduation.com.camera2demo.Adapter.ViewPagerAdapter;
import wangbin.graduation.com.camera2demo.utils.PermissionUtil;

public class MainActivity extends FragmentActivity  {

    private ViewPager mViewPager;
    private AlbumFragment mAlbumFragment;
    private Camera2Fragment mCamera2Fragment;
    private List<Fragment> mFragmentList = new ArrayList<>();
    private ViewPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        if (PermissionUtil.checkAndOpenPermissions(this,Camera2Fragment.VIDEO_PERMISSIONS))
            init();
    }
    private void initView(){
        mViewPager = (ViewPager)findViewById(R.id.viewpager);
    }
    private void init(){
        mAdapter = new ViewPagerAdapter(this.getFragmentManager(),mFragmentList);
        mCamera2Fragment = Camera2Fragment.newInstance();
        mAlbumFragment =  AlbumFragment.newInstance();
        mFragmentList.add(mAlbumFragment);
        mFragmentList.add(mCamera2Fragment);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtil.onRequestPermissionsResult(requestCode,permissions,grantResults).size()>0) {
            PermissionUtil.gotoSystemSetting(this);
        }else {
            init();
        }
    }
}
