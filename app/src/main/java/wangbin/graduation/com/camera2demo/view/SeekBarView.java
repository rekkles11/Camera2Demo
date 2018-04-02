package wangbin.graduation.com.camera2demo.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by momo on 2018/3/30.
 */

public class SeekBarView extends View {
    private Context mContext;
    private Paint mRetcPaint;
    private Paint mLinePaint;
    private PointF mLeftTopPoint;
    private PointF mRightBottomPoint;
    private float mDownX;
    private Boolean mStartMove = false;
    private float mDistance;
    private int mWidth;
    private int mHight;
    private Boolean CheckNum1 = false;
    private OnUpOrDownListener mOnUpOrDownListener = null;

    public SeekBarView(Context context) {
        super(context);
        init(context);
    }

    public SeekBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SeekBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public SeekBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    public interface OnUpOrDownListener{
        void isUp(Boolean b,int left,int right);
    }

    public void setOnUpOrDownListener(OnUpOrDownListener onUpOrDownListener) {
        this.mOnUpOrDownListener = onUpOrDownListener;
    }

    private void init(Context context) {
        this.mContext = context;
        mRetcPaint = new Paint();
        mLinePaint = new Paint();
        mRetcPaint.setStrokeWidth(10);
        mRetcPaint.setColor(Color.WHITE);
        mRetcPaint.setStyle(Paint.Style.STROKE);
        mRetcPaint.setAntiAlias(true);

        mLinePaint.setStrokeWidth(20);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(Color.WHITE);
        mLinePaint.setAntiAlias(true);


        mLeftTopPoint = new PointF();
        mRightBottomPoint = new PointF();

    }
    public void setSize(int w ,int h,int time){
        this.mWidth = w;
        this.mHight = h;
        if (time>10000){
            mDistance = w;
        }else {
            mDistance = ((float)time/10000.0f)*w;
        }
        mLeftTopPoint.x = 0;
        mLeftTopPoint.y = 0;
        mRightBottomPoint.x = mDistance;
        mRightBottomPoint.y = mHight;
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(mLeftTopPoint.x,mLeftTopPoint.y,mRightBottomPoint.x,mRightBottomPoint.y,mRetcPaint);
        canvas.drawLine(mLeftTopPoint.x,mLeftTopPoint.y,mLeftTopPoint.x,mRightBottomPoint.y,mLinePaint);
        canvas.drawLine(mRightBottomPoint.x,mLeftTopPoint.y,mRightBottomPoint.x,mRightBottomPoint.y,mLinePaint);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (x<=mLeftTopPoint.x+20&&x>=mLeftTopPoint.x-20&&
                        y<=mRightBottomPoint.y+mHight&&y>mLeftTopPoint.y){
                    CheckNum1 = true;
                    mStartMove = true;
                    mDownX = x;
                    mOnUpOrDownListener.isUp(false,0,0);
                    return true;
                }else if (x<=mRightBottomPoint.x+20&&x>=mRightBottomPoint.x-20&&
                        y<=mRightBottomPoint.y&&y>mLeftTopPoint.y-mHight){
                    CheckNum1 = false;
                    mStartMove = true;
                    mDownX = x;
                    mOnUpOrDownListener.isUp(false,0,0);
                    return true;
                }else {
                    mStartMove = false;
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if (mStartMove){
                    float distance = x - mDownX;
                    if (CheckNum1){
                        if ((mRightBottomPoint.x-mLeftTopPoint.x)<(mWidth/10*3)&&distance>0){
                            return true;
                        }
                        mLeftTopPoint.x += distance;
                        if (mLeftTopPoint.x<=20){
                            mLeftTopPoint.x =20;
                        }
                    }else {
                        if ((mRightBottomPoint.x-mLeftTopPoint.x)<(mWidth/10*3)&&distance<0){
                            return true;
                        }
                        mRightBottomPoint.x += distance;
                        if (mRightBottomPoint.x>(mWidth-20)){
                            mRightBottomPoint.x = (mWidth-20);
                        }
                    }
                    mDownX = x;
                    invalidate();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mStartMove) {
                    int mStartTime = (int) (mLeftTopPoint.x / ((float) mWidth)*10000.0f);
                    int mEndTime = (int) (mRightBottomPoint.x / ((float) mWidth)*10000.0f);
                    mOnUpOrDownListener.isUp(true, mStartTime, mEndTime);
                    mStartMove =false;
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
