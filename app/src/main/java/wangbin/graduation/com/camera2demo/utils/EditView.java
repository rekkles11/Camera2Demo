package wangbin.graduation.com.camera2demo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by momo on 2018/3/13.
 */

public class EditView extends android.support.v7.widget.AppCompatImageView {
    private Bitmap mNewBitmap = null;
    private Bitmap mOriginalBitmap = null;
    private int mScribbleColor = Color.BLUE;
    private float mStrokeWidth = 10f;
    private Boolean mIsScribble = false;
    private Boolean mIntoScribble = false;
    private Boolean mIntoDecall = false;
    private Boolean mIsDecall = false;
    //scribble
    private float mStartX = 0;
    private float mStartY = 0;
    private float mStopX = 0;
    private float mStopY = 0;
    private Canvas mCanvas;
    //Decal
    private float mDragX = 0;
    private float mDragY = 0;
    private int mDragTag = -1;
    private List<Decal> mDecalList = new ArrayList<>();
    //bitmap history
    private List<Bitmap> mBitmapList = new ArrayList<>();

    int bitmapLeftTopX;
    int bitmapLeftTopY;
    int bitmapRightBottomX;
    int bitmapRightBottomY;
    private int mBitmapW;
    private int mBitmapH;

    private boolean mIsDeleteDecal = false;
    private OnDeleteDecalListener mDeleteDecalListener;
    //图标正常状态
    public int STATE_NORMAL = 1;
    //图标可删除状态
    public int STATE_DELETING = 2;
    //图标已删除状态
    public int STATE_DELETED = 3;

    private Matrix mDownMatrix = new Matrix();
    private Matrix mMoveMatrix = new Matrix();

    public EditView(Context context) {
        super(context);
    }

    public EditView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public interface OnDeleteDecalListener {
        void onDeleteState(int state);
    }

    public void setOnDeleteDecalListener(OnDeleteDecalListener listener) {
        this.mDeleteDecalListener = listener;
    }

    public void setBitmapXY(Bitmap bitmap) {
        mBitmapW = bitmap.getWidth();
        mBitmapH = bitmap.getHeight();
        bitmapLeftTopX = (getWidth() - mBitmapW) / 2;
        bitmapLeftTopY = (getHeight() - mBitmapH) / 2;
        bitmapRightBottomX = bitmapLeftTopX + mBitmapW;
        bitmapRightBottomY = bitmapLeftTopY + mBitmapH;
    }

    public void setOriginalBitmap(Bitmap bitmap) {
        this.mOriginalBitmap = bitmap;
        mNewBitmap = bitmap.copy(Bitmap.Config.RGB_565, true);
        setMinimumWidth(mOriginalBitmap.getWidth());
        setMinimumHeight(mOriginalBitmap.getHeight());
        invalidate();
    }

    public void setScribbleColor(int color) {
        this.mScribbleColor = color;
    }
    public void undoBitmap(){
        if (mBitmapList.size()>0) {
            Bitmap bitmap = mBitmapList.get(mBitmapList.size() - 1);
            mBitmapList.remove(mBitmapList.size() - 1);
            setOriginalBitmap(bitmap);
            setImageBitmap(bitmap);
            invalidate();
        }
    }

    public void setIntoScribble(Boolean intoScribble) {
        mBitmapList.add(mNewBitmap);
        this.mIntoScribble = intoScribble;
    }

    public void setIntoDecal(Boolean intoDecall) {
        this.mIntoDecall = intoDecall;
    }

    public void addText(String text) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(mScribbleColor);
        paint.setAntiAlias(true);
        paint.setTextSize(20 * getResources().getDisplayMetrics().scaledDensity);
        float textWidth = paint.measureText(text);
        Bitmap bitmap = Bitmap.createBitmap((int) textWidth + 100, 60, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(text, 0, 50, paint);
        addDecalIcon(bitmap);

    }

    public Bitmap getNewBitmap() {
        return mNewBitmap;
    }
    public Bitmap getfinalBitmap(){
        Bitmap bitmap = Bitmap.createBitmap(mNewBitmap,0,0,mBitmapW,mBitmapH);
        Canvas canvas  = new Canvas(bitmap);
        for (Decal decal:mDecalList){
            Matrix matrix = decal.mMatrix;
            matrix.postTranslate(-bitmapLeftTopX,-bitmapLeftTopY);
            canvas.drawBitmap(decal.mBitmap,decal.mMatrix,null);
        }
        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Decal decal : mDecalList) {
            canvas.drawBitmap(decal.mBitmap, decal.mMatrix, null);
        }
        if (mNewBitmap != null && mIntoScribble) {
            scribble(mNewBitmap);
        }
    }

    private float[] getBitmapPoints(Decal decal) {
        float[] dst = new float[8];
        float[] src = new float[]{0, 0, decal.mBitmap.getWidth(), 0, 0, decal.mBitmap.getHeight()
                , decal.mBitmap.getWidth(), decal.mBitmap.getHeight()};
        decal.mMatrix.mapPoints(dst, src);
        return dst;
    }

    public void addDecalIcon(Bitmap bitmap) {
        Decal decal = new Decal();
        decal.mBitmap = bitmap;
        if (decal.mMatrix == null)
            decal.mMatrix = new Matrix();
        float transX = (getWidth() / 2 - 100);
        float transY = (getHeight() / 2 - 100);
        decal.mMatrix.postTranslate(transX, transY);
        mDecalList.add(decal);
        invalidate();
    }

    private void scribble(Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(mScribbleColor);
        paint.setStrokeWidth(mStrokeWidth);
        if (mIsScribble) {
            if ((mStartX >= bitmapLeftTopX) && (mStartX <= bitmapRightBottomX)
                    && (mStartY >= bitmapLeftTopY) && (mStartY <= bitmapRightBottomY)
                    && (mStopX >= bitmapLeftTopX) && (mStopX <= bitmapRightBottomX)
                    && (mStopY >= bitmapLeftTopY) && (mStopY <= bitmapRightBottomY))
                //判断落点在不在Bitmap的区域里面 不在就不进行绘制
                canvas.drawLine(mStartX - bitmapLeftTopX, mStartY - bitmapLeftTopY, mStopX - bitmapLeftTopX, mStopY - bitmapLeftTopY, paint);
        }
        mStartX = mStopX;
        mStartY = mStopY;
        setOriginalBitmap(bitmap);
        setImageBitmap(bitmap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("actiong", String.valueOf(event.getAction()));
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsDeleteDecal = false;
                if (mIntoScribble) {
                    mBitmapList.add(mNewBitmap);
                    mStartX = x;
                    mStartY = y;
                    mStopX = mStartX;
                    mStopY = mStartY;
                    return true;
                }
                if (mIntoDecall) {
                    mDragX = x;
                    mDragY = y;
                    mDragTag = getDecalIndex(mDragX, mDragY);
                    if (mDragTag != -1) {
                        mDownMatrix.set(mDecalList.get(mDragTag).mMatrix);
                    }
                    invalidate();
                    return true;
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (mIntoScribble) {
                    mStopX = x;
                    mStopY = y;
                    mIsScribble = true;
                    invalidate();
                    return true;
                }
                if (mIntoDecall && mDragTag != -1) {
                    mMoveMatrix.set(mDownMatrix);
                    float decalW = mDecalList.get(mDragTag).mBitmap.getWidth();
                    float decalH = mDecalList.get(mDragTag).mBitmap.getHeight();
                    if (x < bitmapLeftTopX || x > bitmapRightBottomX
                            || y < bitmapLeftTopY || y > bitmapRightBottomY) {
                        mIsDeleteDecal = true;
                        mDeleteDecalListener.onDeleteState(STATE_DELETING);
                    } else {
                        mIsDeleteDecal = false;
                        mDeleteDecalListener.onDeleteState(STATE_NORMAL);
                    }
                    mMoveMatrix.postTranslate(x - mDragX, y - mDragY);
                    mDecalList.get(mDragTag).mMatrix.set(mMoveMatrix);
                    //                        getParent().requestDisallowInterceptTouchEvent(true);
                    invalidate();
                    return true;
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (mIntoScribble){
                    mIsScribble = false;
                }
                if (mDragTag != -1) {
                    mDeleteDecalListener.onDeleteState(STATE_DELETED);
                    if (mIsDeleteDecal) {
                        deleteDecal(mDragTag);
                    }
                    mDragTag = -1;
                }
                return true;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void deleteDecal(int dragTag) {
        mDecalList.remove(dragTag);
    }

    private int getDecalIndex(float dragX, float dragY) {
        for (int i = 0; i < mDecalList.size(); i++) {
            if (belongToDecal(mDecalList.get(i), dragX, dragY))
                return i;
        }
        return -1;
    }

    //判断down的点是否在decal里面
    private boolean belongToDecal(Decal decal, float dragX, float dragY) {
        float[] dst = getBitmapPoints(decal);
        if (dragX > dst[0] && dragX < dst[6] && dragY > dst[1] && dragY < dst[7]) {
            return true;
        }
        return false;
    }

    private static class Decal {
        public Bitmap mBitmap;
        public Matrix mMatrix = new Matrix();

        private void release() {
            if (mBitmap != null) {
                mBitmap.recycle();
                mBitmap = null;
            }
            if (mMatrix != null) {
                mMatrix.reset();
                mMatrix = null;
            }
        }
    }
}
