package wangbin.graduation.com.camera2demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileOutputStream;

import wangbin.graduation.com.camera2demo.Entity.Image;
import wangbin.graduation.com.camera2demo.utils.KeyboradUtil;
import wangbin.graduation.com.camera2demo.utils.DensityUtil;
import wangbin.graduation.com.camera2demo.utils.EditView;
import wangbin.graduation.com.camera2demo.view.ColorChooseView;

/**
 * Created by momo on 2018/3/12.
 */

public class EditImageActivity extends Activity implements View.OnClickListener {
    private String mImage;
    private ColorChooseView mColorChooseView;
    private ColorChooseView mColorChooseViewText;
    private RelativeLayout relativeLayout;
    private EditView mEditImage;
    private View mFilterVeiw;
    private View mDecalView;
    private View mTextVeiw;
    private View mScribbleView;
    private View mBottomTab;
    private View mSelectDecalView;
    private EditText mEditText;
    private Bitmap mBitmap;
    private ImageView mBack;
    private ImageView mUndo;
    private ImageView mTrashCan;
    private String mTag = "";
    private String FILTER = "fileter";
    private String DECAL = "decal";
    private String TEXT = "text";
    private String SCRIBBLE = "scribble";
    private String BACK ="back";
    //图片大小
    private int mImageWidth = 0;
    private int mImageHeight = 0;
    private KeyboradUtil workaround;
    private Boolean mScaling = false;
    private Boolean mTrashScaling = false;

    private KeyboradUtil.keyboardHeightChangedListener keyboardHeightChangedListener = new KeyboradUtil.keyboardHeightChangedListener() {
        @Override
        public void heightChanged(int previewHeight, int nowHeight) {
            if (relativeLayout.getVisibility() != View.VISIBLE)
                return;
            int dh = DensityUtil.getScreenHeight(EditImageActivity.this) - nowHeight;
            if (dh > DensityUtil.dip2px(EditImageActivity.this, 100)) {
                keyBoradShow(dh);
            } else {
                keyBoardHidden();
            }
        }
    };

    private void keyBoradShow(int h) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) relativeLayout.getLayoutParams();
        params.bottomMargin = h;
        ViewGroup.MarginLayoutParams etp = (ViewGroup.MarginLayoutParams) mEditText.getLayoutParams();
        etp.bottomMargin = h>>1;
        relativeLayout.requestLayout();
    }

    private void keyBoardHidden() {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) relativeLayout.getLayoutParams();
        ViewGroup.MarginLayoutParams etp = (ViewGroup.MarginLayoutParams) mEditText.getLayoutParams();
        params.bottomMargin = 0;
        etp.bottomMargin = 0;
        mBottomTab.setVisibility(View.VISIBLE);
        relativeLayout.requestLayout();
        relativeLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setFullscreen();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        mImage =  getIntent().getStringExtra("editImage");
        initView();
        if (workaround == null)
            workaround = KeyboradUtil.assistActivity(this, keyboardHeightChangedListener);
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

    private void initView() {
        mColorChooseView = (ColorChooseView) findViewById(R.id.color_select_ccv);
        mColorChooseView.setOnItemClickListener(new ColorChooseView.OnItemClickListener() {
            @Override
            public void onItemClick(int color, int position) {
                mEditImage.setScribbleColor(color);
            }
        });

        mColorChooseViewText = findViewById(R.id.color_choose_view_text);
        mColorChooseViewText.setOnItemClickListener(new ColorChooseView.OnItemClickListener() {
            @Override
            public void onItemClick(int color, int position) {
                setEditTextColor(color);
            }
        });
        mUndo = (ImageView)findViewById(R.id.undo_edit_image);
        mTrashCan = (ImageView) findViewById(R.id.trash_edit_image);
        relativeLayout = findViewById(R.id.text_bottom);
        mBack = (ImageView) findViewById(R.id.cross_edit_image);
        mEditText = (EditText) findViewById(R.id.text_select_et);
        mEditImage = (EditView) findViewById(R.id.pic_edit);
        mSelectDecalView = findViewById(R.id.decal_select_ll);
        mBottomTab = findViewById(R.id.bottom_tab_edit);
        mFilterVeiw = findViewById(R.id.filter_fl);
        mDecalView = findViewById(R.id.decal_fl);
        mTextVeiw = findViewById(R.id.text_fl);
        mScribbleView = findViewById(R.id.scribble_fl);
        //decal
        findViewById(R.id.decal1_fl).setOnClickListener(this);
        findViewById(R.id.decal2_fl).setOnClickListener(this);
        findViewById(R.id.decal3_fl).setOnClickListener(this);
        findViewById(R.id.decal4_fl).setOnClickListener(this);

        findViewById(R.id.Ok_edit_image).setOnClickListener(this);
        mUndo.setOnClickListener(this);
        mFilterVeiw.setOnClickListener(this);
        mDecalView.setOnClickListener(this);
        mTextVeiw.setOnClickListener(this);
        mScribbleView.setOnClickListener(this);
        mBack.setOnClickListener(this);
        findViewById(R.id.top_tab_edit).setAlpha(0.95f);
        findViewById(R.id.bottom_tab_edit).setAlpha(0.95f);
        disPlayImage();
        mEditImage.setOnDeleteDecalListener(new EditView.OnDeleteDecalListener() {
            @Override
            public void onDeleteState(int state) {
                Log.e("state--", String.valueOf(state));
                if (state == 1) {
                    mTrashCan.setVisibility(View.VISIBLE);
                    mTrashCan.setBackgroundResource(R.drawable.shape_trash_normal);
                    if (mTrashScaling) {
                        trashscaleAnimation(1.5f, 1.0f);
                        mTrashScaling = false;
                    }
                    if (!mScaling) {
                        bottomscaleAnimation(1.0f, 0);
                        mScaling = true;
                    }

                } else if (state == 2) {
                    Log.e("state--v", String.valueOf(mTrashCan.getVisibility()));
                    if (mTrashCan.getVisibility() == View.VISIBLE) {
                        mTrashCan.setBackgroundResource(R.drawable.shape_trash_red);
                        if (!mTrashScaling) {
                            trashscaleAnimation(1.0f, 1.5f);
                            mTrashScaling = true;
                        }
                    }

                } else if (state == 3) {
                    mTrashCan.clearAnimation();
                    mTrashCan.setVisibility(View.GONE);
                    mColorChooseView.setVisibility(View.GONE);
                    mBottomTab.setVisibility(View.VISIBLE);
                    if (mScaling) {
                        bottomscaleAnimation(0, 1.0f);
                        mScaling = false;
                    }
                }
            }
        });

    }

    private void disPlayImage() {

        Glide.with(this)
             .load(mImage)
             .listener(new RequestListener<String, GlideDrawable>() {
                 @Override
                 public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                     return false;
                 }

                 @Override
                 public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                     mImageWidth = resource.getIntrinsicWidth();
                     mImageHeight = resource.getIntrinsicHeight();

                     Bitmap.Config config = resource.getOpacity() != PixelFormat.OPAQUE
                             ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
                     mBitmap = Bitmap.createBitmap(mImageWidth, mImageHeight, config);
                     Canvas canvas = new Canvas(mBitmap);
                     resource.setBounds(0, 0, mImageWidth, mImageHeight);
                     resource.draw(canvas);
                     mEditImage.setBitmapXY(mBitmap);
                     mEditImage.setOriginalBitmap(mBitmap);
                     return false;
                 }
             }).into(mEditImage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filter_fl:
                mTag = FILTER;
                mBitmap = toBlackAndWhite(mBitmap);
                break;
            case R.id.decal_fl:
                startDecal();
                break;
            case R.id.text_fl:
                startText();
                break;
            case R.id.scribble_fl:
                startScribble();
                break;
            case R.id.cross_edit_image:
                mTag = BACK;
                back();
                break;
            case R.id.undo_edit_image:
                undo();
                break;
            case R.id.Ok_edit_image:
                back();
                break;
            case R.id.decal1_fl:
                mEditImage.addDecalIcon(BitmapFactory.decodeResource(getResources(), R.drawable.decal_4));
                back();
                break;
            case R.id.decal2_fl:
                mEditImage.addDecalIcon(BitmapFactory.decodeResource(getResources(), R.drawable.decal_2));
                back();
                break;
            case R.id.decal3_fl:
                mEditImage.addDecalIcon(BitmapFactory.decodeResource(getResources(), R.drawable.decal_3));
                back();
                break;
            case R.id.decal4_fl:
                mEditImage.addDecalIcon(BitmapFactory.decodeResource(getResources(), R.drawable.decal_1));
                back();
                break;
            default:
                break;
        }
    }
    //撤销笔画
    private void undo() {
        mEditImage.undoBitmap();
    }

    private void dealBitmap() {
        Bitmap bitmap = mEditImage.getfinalBitmap();
        String state = Environment.getExternalStorageState();
        if(!state.equals(Environment.MEDIA_MOUNTED)){
            return;
        }
        String path ="/sdcard/Pictures/"+System.currentTimeMillis()+".jpg";
        File file  = new File(path);
        if (file.exists()){
            file.delete();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            //发送广播，通知扫描图片
            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(uri);
            this.sendBroadcast(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
        Intent intent = new Intent();
        intent.putExtra("imagePath",path);
        this.setResult(101,intent);
        this.finish();
//        startActivity(new Intent(this,DisplayImageActivity.class));
    }

    private void setEditTextColor(int color) {
        mEditText.setHintTextColor(color);
        mEditText.setTextColor(color);
        mEditImage.setScribbleColor(color);
    }

    private void startText() {
        relativeLayout.setVisibility(View.VISIBLE);
        mTag = TEXT;
        bottomTabAnimation(1.0f, 0);
        mBottomTab.setVisibility(View.GONE);
        mEditImage.setIntoDecal(true);
        mEditText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(mEditText, InputMethodManager.SHOW_FORCED, null);
        mColorChooseViewText.showAnimator();
        mEditText.setText("");
    }

    private void closeText() {
        relativeLayout.setVisibility(View.GONE);
        mBottomTab.setVisibility(View.VISIBLE);
        bottomTabAnimation(0, 1.0f);
        mEditImage.addText(mEditText.getText().toString());
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    private void back() {
        if (mTag.equals(SCRIBBLE)) {
            closeScribble();
            mTag ="";
        } else if (mTag.equals(DECAL)) {
            closeDecal();
            mTag ="";
        } else if (mTag.equals(TEXT)) {
            closeText();
            mTag ="";
        } else if (mTag.equals(BACK)){
            this.finish();
        }else {
            dealBitmap();
        }
    }

    private void closeDecal() {
        mSelectDecalView.setVisibility(View.GONE);
        mBottomTab.setVisibility(View.VISIBLE);
        bottomTabAnimation(0, 1.0f);
    }

    private void startDecal() {
        mTag = DECAL;
        bottomTabAnimation(1.0f, 0);
        mBottomTab.setVisibility(View.GONE);
        mSelectDecalView.setVisibility(View.VISIBLE);
        mEditImage.setIntoDecal(true);

    }

    private void startScribble() {
        mTag = SCRIBBLE;
        bottomTabAnimation(1.0f, 0);
        mBottomTab.setVisibility(View.GONE);
        mColorChooseView.setVisibility(View.VISIBLE);
        mColorChooseView.showAnimator();
        mEditImage.setIntoScribble(true);
        mEditImage.setOriginalBitmap(mBitmap);
    }

    private void closeScribble() {
        mColorChooseView.setVisibility(View.GONE);
        mBottomTab.setVisibility(View.VISIBLE);
        bottomTabAnimation(0, 1.0f);
        mEditImage.setIntoScribble(false);
        mBitmap = mEditImage.getNewBitmap();
    }

    private void trashscaleAnimation(float before, float after) {
        mTrashCan.clearAnimation();
        ScaleAnimation animation = new ScaleAnimation(before, after, before, after,
                                                      Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(200);
        animation.setFillAfter(true);
        mTrashCan.setAnimation(animation);
        animation.start();

    }

    private void bottomscaleAnimation(float before, float after) {
        ScaleAnimation animation = new ScaleAnimation(before, after, before, after,
                                                      Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(200);
        animation.setFillAfter(true);
        mBottomTab.setAnimation(animation);
        animation.start();
    }

    private void bottomTabAnimation(float before, float after) {
        Animation animation = new AlphaAnimation(before, after);
        animation.setDuration(200);
        animation.setFillAfter(true);
        mBottomTab.setAnimation(animation);
        animation.start();
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBottomTab.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public Bitmap toBlackAndWhite(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] pixels = new int[width * height];

        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF<<24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];

                int red = ((grey&0x00FF0000)>>16);
                int green = ((grey&0x0000FF00)>>8);
                int blue = ((grey&0x000000FF));

                grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
                grey = alpha|(grey<<16)|(grey<<8)|grey;
                pixels[width * i + j] = grey;
            }
        }
        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        mEditImage.setImageBitmap(newBitmap);
        mEditImage.setOriginalBitmap(newBitmap);
        return newBitmap;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            back();
            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
