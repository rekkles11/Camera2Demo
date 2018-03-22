package wangbin.graduation.com.camera2demo.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;

import java.util.ArrayList;
import java.util.List;

import wangbin.graduation.com.camera2demo.R;

/**
 * Created by chenxin on 2018/3/21.
 */

public class ColorChooseView extends View {

    private List<ColorView> colorList;
    private int checkedColor = 0;//当前选中的颜色小球
    private int changedX = 0;//用来动态改变X轴坐标值，来达到抖动的效果
    private int itemW;//根据小球的个数将View平分之后的宽度
    private int itemColorWidth;//颜色小球的半径
    private OnItemClickListener listener;

    public ColorChooseView(Context context) {
        super(context);
        init();
    }

    public ColorChooseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorChooseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setClickable(true);
        addColor(new int[]{getResources().getColor(R.color.purple, null),
                getResources().getColor(R.color.green, null),
                getResources().getColor(R.color.red, null),
                getResources().getColor(R.color.blue, null),
                getResources().getColor(R.color.yellow, null)});
        colorList.get(0).checked = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        itemW = w / colorList.size() > h ? h : w / colorList.size();
        itemColorWidth = itemW / colorList.size() * (colorList.size() - 1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        ColorView firstView = colorList.get(0);
        firstView.x = (getWidth() - itemW * colorList.size()) / 2 + itemW / 2 + (firstView.checked ? changedX : 0);
        firstView.y = itemW / 2;
        firstView.w = itemColorWidth / 2;
        firstView.ring = 10;
        for (int i = 1; i < colorList.size(); i++) {
            ColorView v = colorList.get(i);
            v.x = colorList.get(i - 1).x + itemW + (v.checked ? changedX : 0);
            v.y = itemW / 2;
            v.w = itemColorWidth / 2;
            v.ring = 10;
        }

        for (int i = 0; i < colorList.size(); i++) {
            colorList.get(i).draw(canvas);
        }
    }

    public void addColor(int[] colors) {
                colorList = new ArrayList<>(colors.length);
        for (int i = 0; i < colors.length; i++) {
            colorList.add(new ColorView(colors[i]));
        }
    }

    public void showAnimator() {
        int nextDuration = 100;
        for (final ColorView v : colorList) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    v.show();
                }
            }, 50 + (nextDuration += 100));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                findChosen(event.getX(), event.getY());
                break;
            default:
        }
        return super.onTouchEvent(event);
    }

    private void findChosen(float x, float y) {
        int radius = itemW / 2;
        for (int i = 0; i < colorList.size(); i++) {
            ColorView v = colorList.get(i);
            if ((x >= (v.x - radius))
                    && (x < (v.x + radius))
                    && (y >= (v.y - radius)
                    && y <= (v.y + radius))) {
                colorList.get(checkedColor).checked = false;
                v.checked = true;
                checkedColor = i;
                if (listener != null) {
                    listener.onItemClick(v.color, i);
                }
                v.showFingerUP();
                return;
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    class MyInterpolate implements Interpolator {
        @Override
        public float getInterpolation(float input) {
            //return (float) Math.sin(input * 2 * Math.PI);
            return input;
        }
    }

    class ColorView {
        int color;
        int x;
        int y;
        int w;//颜色小球的半径
        int ring;//圆环的半径
        int wChange = 0;//半径的改变值，动画需要。
        int animatorDuration = 800;
        boolean checked = false;
        boolean animating = false;
        Paint paint;
        ValueAnimator animator;
        ValueAnimator animatorFingerUP;

        ColorView(int color) {
            this.color = color;
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.BLUE);

            //改变w的动画
            animator = new ValueAnimator();
            animator.setInterpolator(new MyInterpolate());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float v = (float) animation.getAnimatedValue();
                    wChange = (int) ((v - 1) * w);
                    if (animation.getCurrentPlayTime() + 10 >= animation.getDuration()) {
                        if (checked) {
                            wChange = (int) (w * 0.2);
                        }
                        animating = false;
                    }

                    invalidate();
                }
            });
            animator.setFloatValues(0.8f, 1.3f, 1.0f);
            animator.setDuration(animatorDuration);

            //改变x的动画
            animatorFingerUP = new ValueAnimator();
            animatorFingerUP.setInterpolator(new MyInterpolate());
            animatorFingerUP.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float v = (float) animation.getAnimatedValue();
                    changedX = (int) ((v - 1) * w * 1.5);
                    invalidate();
                }
            });
            animatorFingerUP.setFloatValues(0.8f, 1.2f, 0.8f, 1.2f, 1.1f, 1.0f);
            animatorFingerUP.setDuration(animatorDuration);
        }

        void show() {
            animating = true;
            animator.start();
        }

        void showFingerUP() {
            animating = true;
            animator.start();
            animatorFingerUP.start();
        }

        void draw(Canvas canvas) {
            //画圆
            paint.setXfermode(null);
            //paint.setAlpha(255);
            paint.setColor(color);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(x, y, w + (checked || animating ? wChange : 0), paint);
            //画环
            if (checked || animating) {
                // paint.setAlpha(0);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                paint.setStrokeWidth(4);
                setLayerType(LAYER_TYPE_SOFTWARE, null);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.TRANSPARENT);
                canvas.drawCircle(x, y, w, paint);
            }

        }
    }

    public interface OnItemClickListener {
        void onItemClick(int color, int position);
    }

}
