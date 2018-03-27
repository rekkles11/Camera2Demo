package wangbin.graduation.com.camera2demo.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.mtp.MtpEvent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by momo on 2018/3/26.
 */

public class CircleView extends View {

    private Paint mPaint;
    private Paint mBackgroundPaint;
    private PointF mCenter;
    private float mSweepAngle;
    private RectF mRectF;
    private OnRecordOverListener mOnRecordOverListener;
    private OnTakePictureListener mOnTakePictureListener;
    private Shader mShader;
    private Boolean mStartRecord = false;
    private ValueAnimator mValueAnimator;
    private Boolean isDown =false;
    public CircleView(Context context) {
        super(context);
        init();
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public interface OnRecordOverListener{
        void over(Boolean isOk);
    }
    public interface OnTakePictureListener{
        void takePic(Boolean b);
    }
    public void setOnTakePictureListener(OnTakePictureListener listener){
        this.mOnTakePictureListener = listener;
    }
    public void setOnRecordOverListener(OnRecordOverListener listener){
        this.mOnRecordOverListener = listener;
    }

    private void init() {
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setStrokeWidth(12);
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setDither(true);
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setColor(Color.WHITE);
        mPaint = new Paint();
        mPaint.setStrokeWidth(12);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mValueAnimator = ValueAnimator.ofFloat(0,360);
        mValueAnimator.setDuration(30000);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSweepAngle = (float)animation.getAnimatedValue();
//                if (mSweepAngle>=5&&!mStartRecord){
//                    mStartRecord = true;
                    //开始录制视频
//                    mOnTakePictureListener.takePic(false);
//                }
                invalidate();
            }

        });
        mValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (mSweepAngle>15) {
                    mOnRecordOverListener.over(true);
                    mStartRecord =false;
                }

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }
    public void reset(){
        mSweepAngle = 0;
        invalidate();
    }

    public void setCircleCentere(int w,int h){
        mCenter = new PointF(w/2,h/2);
        mShader = new LinearGradient(0,w/2,h,w/2,Color.parseColor("#E91E63"),
                Color.parseColor("#2196F3"),Shader.TileMode.CLAMP);
        mPaint.setShader(mShader);
        mRectF  = new RectF(20.0f, 20.0f, ((float) w) - 20.0f, ((float) h) - 20.0f);

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mCenter.x,mCenter.y,getWidth()/2-20,mBackgroundPaint);
        if (mStartRecord) {
            canvas.drawArc(mRectF,-90,mSweepAngle,false,mPaint);
        }
    }


    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (!mStartRecord&&!isDown) {
                    isDown = true;
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int a = event.getAction();
                            if (a == MotionEvent.ACTION_UP) {
                                Log.e("action","post - action up");
                                mOnTakePictureListener.takePic(true);

                            } else if (a == MotionEvent.ACTION_MOVE){
                                Log.e("action","post - action move");
                                mOnTakePictureListener.takePic(false);
                                mStartRecord = true;
                                mValueAnimator.start();
                            }
                            isDown = false;

                        }
                    }, 100);
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.e("action--",String.valueOf("movetion up"));
                if (mStartRecord) {
                    if (mSweepAngle < 36) {
                        Log.e("action--",String.valueOf("movetion up<"));
                        mOnRecordOverListener.over(false);
                    } else {
                        Log.e("action--",String.valueOf("movetion up>"));
                        mOnRecordOverListener.over(true);
                    }
                    mStartRecord = false;
                    mValueAnimator.cancel();
                }
                return true;
        }
        return true;
    }
}