package wangbin.graduation.com.camera2demo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DebugUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wangbin.graduation.com.camera2demo.R;
import wangbin.graduation.com.camera2demo.utils.DensityUtil;

/**
 * Created by chenxin on 2018/3/27.
 */

public class ViewPagerIndicator extends ViewGroup implements ViewPager.OnPageChangeListener {

    RecyclerView recyclerView;
    ViewPager viewPager;
    private int indicatorHeight = 30;
    private int indicatorWidth = 15;
    private int leftMargin;
    private int itemWidth;
    private int currentItem = 0;
    private int lastItem = 0;
    private int offsetWidth;
    private float lastOffSet = -1;
    private int state = 0;
    boolean isRight = true;
    boolean flag = false;
    private float screenWidth;
    private Paint mPaint;

    private List<String> textArray = new ArrayList<>();

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        recyclerView = new RecyclerView(getContext());
        recyclerView.setAdapter(new Adapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        recyclerView.setLayoutParams(params);
        addView(recyclerView);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(indicatorWidth);

        itemWidth = DensityUtil.dip2px(getContext(), 60);
        screenWidth = DensityUtil.getScreenWidth(getContext());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        recyclerView.measure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), recyclerView.getMeasuredHeight() + indicatorHeight);
        leftMargin = getMeasuredWidth() / 2;
        offsetWidth = currentItem * itemWidth + itemWidth / 2;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutRecyclerView();
    }

    private void layoutRecyclerView() {

        //Log.e("infoo", "recyclerView layout()   offsetWidth == " + offsetWidth);
        // Log.e("infoo","l = "+(leftMargin - offsetWidth)+"t = 0 "+"r = "+(leftMargin - offsetWidth + recyclerView.getMeasuredWidth())+" b = "+(recyclerView.getMeasuredHeight()));
        recyclerView.layout(leftMargin - offsetWidth, 0, leftMargin - offsetWidth + recyclerView.getMeasuredWidth(), recyclerView.getMeasuredHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(getWidth() / 2, getHeight() - indicatorHeight, getWidth() / 2, getHeight(), mPaint);
    }

    public void setTextArray(List<String> textArray) {
        this.textArray = textArray;
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
        this.viewPager.addOnPageChangeListener(this);
    }

    public void setCurrentItem(int item) {
        lastItem = currentItem = item;

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // Log.e("infoo", "onPageScrolled position-> " + position + "  offset -> " + (double) positionOffset + " offsetPixels ->" + positionOffsetPixels);
        //Log.e("infoo", "onPageScrolled  offset == " + positionOffset);

        if (lastOffSet > 0) {
            if (lastOffSet > positionOffsetPixels) {
                if (!flag) {
                    isRight = true;
                    flag = true;
                    Log.e("infoo", "向右边滑动");
                }

            } else {
                if (!flag) {
                    isRight = false;
                    flag = true;
                    Log.e("infoo", "向左边滑动");
                }

            }

            if (isRight) {
                //  Log.e("infoo", "v == " + ((1f - positionOffset) * (float) itemWidth));
                int temp = (int) ((1f - positionOffset) * (float) itemWidth);
                if (positionOffset > 0) {
                    offsetWidth = lastItem * itemWidth + itemWidth / 2 - temp;
                    layoutRecyclerView();
                }
                // Log.e("infoo", "right  offsetWidth == " + offsetWidth);
            } else {
                int temp = (int) (positionOffset * (float) itemWidth);
                if (temp > itemWidth) {
                    temp = itemWidth;
                }
                // Log.e("infoo", "v == " + ((positionOffset) * (float) itemWidth));
                if (positionOffset > 0) {
                    offsetWidth = lastItem * itemWidth + itemWidth / 2 + temp;
                    layoutRecyclerView();
                }
                //  Log.e("infoo", "left  offsetWidth == " + offsetWidth);
            }

        }
        lastOffSet = positionOffsetPixels;
    }

    @Override
    public void onPageSelected(int position) {
        // Log.e("infoo", "onPageSelected ");
        if (state == 2) {
            if (isRight) {
                currentItem--;
                if (currentItem < 0)
                    currentItem = 0;
            } else {
                currentItem++;
                if (currentItem >= textArray.size())
                    currentItem = textArray.size() - 1;
            }
            Log.e("infoo", "item == " + currentItem);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //Log.e("infoo","state == "+state);
        this.state = state;
        if (state == 0) {
            lastOffSet = -1;
            flag = false;
            lastItem = currentItem;
            //            offsetWidth = currentItem * itemWidth + itemWidth / 2;
            Log.e("infoo", "onPageScrollStateChanged  v == " + offsetWidth);
                        layoutRecyclerView();
        }

    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.listitem_viewpager_indicator, null));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.textView.setText(textArray.get(position));
        }

        @Override
        public int getItemCount() {
            return textArray.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}
