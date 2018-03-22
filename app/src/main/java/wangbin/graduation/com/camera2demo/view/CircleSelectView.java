package wangbin.graduation.com.camera2demo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import wangbin.graduation.com.camera2demo.R;

/**
 * Created by chenxin on 2018/3/21.
 */

public class CircleSelectView extends View {

    private Paint mPaint;
    private Paint mTextPaint;
    private String number = "";

    public CircleSelectView(Context context) {
        super(context);
        init();
    }

    public CircleSelectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleSelectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(34);
    }

    public void setNumber(int number) {
        if (number > 0) {
            this.number = String.valueOf(number);
            Log.e("infoo", "number == " + number);
        }
    }

    public int getNumber() {
        try {
            return Integer.parseInt(number);
        } catch (Exception e) {
            return -1;
        }
    }

    public void setChoose(boolean selected) {
        if (selected == isSelected()) {
            invalidate();
        } else {
            setSelected(selected);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int radius = getWidth() > getHeight() ? getHeight() / 2 : getWidth() / 2;
        mPaint.setStrokeWidth(4);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(radius, radius, radius - 4, mPaint);
        if (isSelected()) {
            mPaint.setStrokeWidth(1);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(getResources().getColor(R.color.DeepSkyBlue, null));
            canvas.drawCircle(radius, radius, radius - 5, mPaint);

            //写字
            mTextPaint.setColor(Color.WHITE);
            float textWidth = mTextPaint.measureText(number);
            float Textx = radius - textWidth / 2;
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            float dy = (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;
            float Texty = radius + dy;
            canvas.drawText(number, Textx, Texty, mTextPaint);
        }

    }

}
