package wangbin.graduation.com.camera2demo;

import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import wangbin.graduation.com.camera2demo.Adapter.ViewPagerAdapter;
import wangbin.graduation.com.camera2demo.utils.PermissionUtil;
import wangbin.graduation.com.camera2demo.utils.VideoCutUtils;
import wangbin.graduation.com.camera2demo.view.ViewPagerIndicator;

public class MainActivity extends FragmentActivity {

    private ViewPager mViewPager;
    private AlbumFragment mAlbumFragment;
    private Camera2Fragment mCamera2Fragment;
    private List<Fragment> mFragmentList = new ArrayList<>();
    private ViewPagerAdapter mAdapter;
    private ViewPagerIndicator indicator;
    List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        VideoCutUtils.trimVideo(this, "/storage/emulated/0/Pictures/Screenshots/SVID_20170809_054411.mp4", "/storage/emulated/0/Pictures/videocut/", 0, 5000);
        setFullscreen();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        if (PermissionUtil.checkAndOpenPermissions(this, Camera2Fragment.VIDEO_PERMISSIONS))
            init();
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        indicator = findViewById(R.id.indicator);
        list.add("相册");
        list.add("拍照");
        indicator.setTextArray(list);
        indicator.setViewPager(mViewPager);
        indicator.setCurrentItem(1);
    }

    public void setFullscreen() {
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                |View.SYSTEM_UI_FLAG_FULLSCREEN;
        // hide status bar
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            uiFlags |= 0x00001000;
        } else {
            uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }
        getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                  WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏s
    }

    private void init() {
        mAdapter = new ViewPagerAdapter(this.getFragmentManager(), mFragmentList);
        mCamera2Fragment = Camera2Fragment.newInstance();
        mAlbumFragment = AlbumFragment.newInstance();
        mFragmentList.add(mAlbumFragment);
        mFragmentList.add(mCamera2Fragment);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults).size() > 0) {
            PermissionUtil.gotoSystemSetting(this);
        } else {
            init();
        }
    }
}
