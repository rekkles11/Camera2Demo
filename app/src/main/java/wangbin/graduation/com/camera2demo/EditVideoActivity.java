package wangbin.graduation.com.camera2demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import wangbin.graduation.com.camera2demo.view.EditVideoView;

/**
 * Created by momo on 2018/3/30.
 */

public class EditVideoActivity extends Activity {

    private EditVideoView mEditVideoView;
    public static String mVideoPath;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVideoPath = getIntent().getStringExtra("videoPath");
        setContentView(R.layout.activity_edit_video);
        mEditVideoView = (EditVideoView)findViewById(R.id.view_edit_video);
    }
}
