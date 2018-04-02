package wangbin.graduation.com.camera2demo.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

import wangbin.graduation.com.camera2demo.Adapter.ThumbImageAdapter;
import wangbin.graduation.com.camera2demo.EditVideoActivity;
import wangbin.graduation.com.camera2demo.R;
import wangbin.graduation.com.camera2demo.utils.VideoCutUtils;

/**
 * Created by momo on 2018/3/30.
 */

public class EditVideoView extends FrameLayout implements View.OnClickListener{

    private Context mContext;
    public SeekBarView mSeekBarView;
    private SeekBar mSeekBar;
    private RecyclerView mRecyclerView;
    private int mVideoTime = 100;
    private int mVideoStartTime = 0;
    private int mVideoEndTime = mVideoTime;
    private int mRecycleViewStartTime = 0;
    private int mSeekBarMaxTime =10000;
    private int mProgressStart =0;
    private int mProgressEnd = 0;
    private VideoView mVideoView;
    private ThumbImageAdapter mImageAdapter;
    private int mSeekBarViewX;
    private int mSeekBarViewWidth;
    private int mSeekBarViewHeight;
    private ValueAnimator mValueAnimator;
    private int mProgress=0;
    private Boolean isScrolling = false;
    private int mItemW;
    String path =
//            "/storage/emulated/0/DCIM/Video/V80330-165538.mp4";
            "/storage/emulated/0/DCIM/Camera/VID_20180330_152851.mp4";
//            "/storage/emulated/0/immomo/MOMO/9352b5ceae401a71729641b961368e97.mp4";
    private List<Bitmap> mBitmapList = new ArrayList<>();
    public EditVideoView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public EditVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EditVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public EditVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        path = EditVideoActivity.mVideoPath;
        this.mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.view_edit_video,this,true);
        mSeekBarView = (SeekBarView) findViewById(R.id.view_seek_bar);
        mSeekBar = (SeekBar)findViewById(R.id.seek_bar);
        mVideoView = (VideoView)findViewById(R.id.view_video);
        mRecyclerView = (RecyclerView)findViewById(R.id.thumb_recycle_view);
        findViewById(R.id.ok_edit_video).setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mBitmapList = VideoCutUtils.getFrames(path,1000);
        mVideoTime  = mBitmapList.size()*1000;
        setSeekBarMaxTime(mVideoTime);
//        mImageAdapter = new ThumbImageAdapter(mContext,mBitmapList,mSeekBarViewWidth);
//        mRecyclerView.setAdapter(mImageAdapter);
        mRecyclerView.addOnScrollListener(mOnScrollListener);
        mSeekBarView.setOnUpOrDownListener(mOnUpOrDownListener);
        mSeekBar.setEnabled(false);
        DealVideo();
    }

    public void setVideoPath(String path){
        this.path = path;
    }
    private void setSeekBarMaxTime(int time){
        if (time>10000){
            mSeekBarMaxTime = 10000;
        }else {
            mSeekBarMaxTime = time;
        }
        mVideoEndTime = mSeekBarMaxTime;
        mProgressEnd = mSeekBarMaxTime;
    }

    private void setSeekBarPostion() {
        mSeekBar.setProgress(mProgress);
    }

    private void setSeekBarMax() {
        mSeekBar.setMax(10000);
    }
    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState !=0&&!isScrolling){
                mVideoView.pause();
                mValueAnimator.cancel();
                mSeekBar.setVisibility(GONE);
                isScrolling = true;
            }
            if (newState ==0&&isScrolling){
                isScrolling = false;
                mSeekBar.setVisibility(VISIBLE);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
                int first = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                int during = mVideoEndTime - mVideoStartTime;
                mRecycleViewStartTime = first*1000;
                mVideoStartTime = mRecycleViewStartTime +mProgressStart;
                mVideoEndTime = mRecycleViewStartTime+mProgressEnd;
                mVideoView.seekTo(mVideoStartTime);
                mVideoView.start();
                mValueAnimator.start();
            }
        };

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };
    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener =
            new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mProgress = (int)animation.getAnimatedValue();
                    setSeekBarPostion();
                }
            };
    private void DealVideo() {
        setAnimator(mValueAnimator,0,mSeekBarMaxTime);
        mVideoView.setVideoURI(Uri.parse(path));
        mVideoView.requestFocus();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                VideoPrepare(mp);
                mVideoView.start();
                mValueAnimator.start();
                mVideoStartTime = 0;
                setSeekBarMax();
                setSeekBarPostion();
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                VideoCompletion(mp);
                mVideoView.start();
                mValueAnimator.start();
            }
        });
    }
    private void setAnimator(ValueAnimator animator,int start,int end){
        mValueAnimator = ValueAnimator.ofInt(start,end);
        mValueAnimator.addUpdateListener(mAnimatorUpdateListener);
        mValueAnimator.setDuration(end-start);
        mValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mVideoView.pause();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mVideoView.pause();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void VideoCompletion(MediaPlayer mp) {

    }

    private void VideoPrepare(MediaPlayer mp) {
        ViewGroup.LayoutParams layoutParams = mVideoView.getLayoutParams();
        float playerW = mp.getVideoWidth();
        float playerH = mp.getVideoHeight();
        float playerProportion = playerW/playerH;
        float viewW =  mVideoView.getWidth();
        float viewH = mVideoView.getHeight();
        float viewProportion = viewW/viewH;
        if (playerProportion>viewProportion){
            layoutParams.width = (int)viewW;
            layoutParams.height = (int)(viewW/playerProportion);
        }else {
            layoutParams.width = (int) (viewH*playerProportion);
            layoutParams.height = (int)viewH;
        }
        mVideoView.setLayoutParams(layoutParams);
        mVideoTime = (mVideoView.getDuration()/1000)*1000;
        mSeekBarView.setSize(mSeekBarViewWidth,mSeekBarViewHeight,mVideoTime);
        initSeekBar();
    }

    private void initSeekBar() {
        mVideoView.seekTo(0);

    }


    private SeekBarView.OnUpOrDownListener mOnUpOrDownListener = new SeekBarView.OnUpOrDownListener() {
        @Override
        public void isUp(Boolean up,int s,int e) {
            if (up){
                Log.e("cut---","up"+s+"   "+e+"   "+mRecycleViewStartTime);
                //松开时播放视频  s 开始时间， e 结束时间
                mSeekBarMaxTime = e-s;
                mProgress = s;
                setSeekBarPostion();
                mSeekBar.setVisibility(VISIBLE);
                mProgressStart =s;
                mProgressEnd = e;
                mVideoStartTime = s+mRecycleViewStartTime;
                mVideoEndTime = e+mRecycleViewStartTime;
                int left = (int)(mItemW*(((float)(s-0 ))/1000.0f));
                int right= (int)(mItemW*(((float)(10000-e ))/1000.0f));
                mImageAdapter.addView(left,right);
                mImageAdapter.notifyDataSetChanged();
                setAnimator(mValueAnimator,s,e);
                mValueAnimator.start();
                mVideoView.seekTo(mVideoStartTime);
                mVideoView.start();

            }else {
                //按下时暂停视频  seekBar归0
                mVideoView.pause();
                mSeekBar.setVisibility(GONE);
                mValueAnimator.cancel();
                mSeekBar.setVisibility(GONE);
            }
        }
    };

    private void SeekBarStart() {

    }


    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        int[] locations = new int[2];
        mSeekBarViewX = locations[0];
//        mSeekBarViewWidth = mSeekBarView.getWidth();
//        mSeekBarViewHeight = mSeekBarView.getHeight();
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int mScreenW = metrics.widthPixels;
        mItemW = mScreenW/12;
        ViewGroup.LayoutParams layoutParams = mSeekBarView.getLayoutParams();
        layoutParams.width = mItemW*10;
        mSeekBarViewWidth = layoutParams.width;
        mSeekBarViewHeight = layoutParams.height;
        mImageAdapter = new ThumbImageAdapter(mContext,mBitmapList,mItemW);
        mRecyclerView.setAdapter(mImageAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ok_edit_video:
                //裁剪视频
                String outPath =
                        "/storage/emulated/0/immomo/MOMO/";
                Log.e("cut----",mVideoStartTime+"  "+mVideoEndTime);
                VideoCutUtils.trimVideo(mContext,path,outPath,mVideoStartTime,mVideoEndTime);
                break;
            case R.id.cancel_edit_video:
                break;
        }
    }
}
