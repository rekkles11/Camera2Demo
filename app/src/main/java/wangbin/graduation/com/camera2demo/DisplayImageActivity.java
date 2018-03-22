package wangbin.graduation.com.camera2demo;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;


/**
 * Created by momo on 2018/3/21.
 */

public class DisplayImageActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        String file = "/sdcard/Pictures/"+"pic"+".jpg";
        ((ImageView)findViewById(R.id.image_display)).setImageBitmap(BitmapFactory.decodeFile(file));
    }
}
