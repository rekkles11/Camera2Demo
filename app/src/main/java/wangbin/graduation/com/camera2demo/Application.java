package wangbin.graduation.com.camera2demo;

import android.content.Context;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

/**
 * Created by chenxin on 2018/3/29.
 */

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initFFmpegBinary(this);
    }

    private void initFFmpegBinary(Context context) {

        try {
            FFmpeg.getInstance(context).loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                }
            });

        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        }
    }

}
