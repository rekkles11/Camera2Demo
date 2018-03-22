package wangbin.graduation.com.camera2demo.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

public class KeyboradUtil {

    // For more information, see https://code.google.com/p/android/issues/detail?id=5497
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.

    public static KeyboradUtil assistActivity(Activity activity, keyboardHeightChangedListener listener) {
        KeyboradUtil bug5497Workaround = new KeyboradUtil(activity);
        bug5497Workaround.listener = listener;
        return bug5497Workaround;
    }

    private keyboardHeightChangedListener listener;
    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;

    private KeyboradUtil(Activity activity) {
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        if (mChildOfContent == null || mChildOfContent.getViewTreeObserver() == null) {
            return;
        }
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if (mChildOfContent != null)
                    possiblyResizeChildOfContent();
            }
        });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        Log.e("infoo", "usableHeightNow = " + usableHeightNow + " usableHeightPrevious = " + usableHeightPrevious);
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                // keyboard probably just became visible
                //frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
            } else {
                // keyboard probably just became hidden
                //frameLayoutParams.height = usableHeightSansKeyboard;
            }
            listener.heightChanged(usableHeightPrevious, usableHeightNow);
//            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }

    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }

    public interface keyboardHeightChangedListener {
        public void heightChanged(int previewHeight, int nowHeight);
    }

    public void release() {
        listener = null;
        mChildOfContent = null;
        frameLayoutParams = null;
    }
}