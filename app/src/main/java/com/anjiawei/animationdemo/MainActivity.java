package com.anjiawei.animationdemo;



import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.anjiawei.animationdemo.view.BlurringView;

public class MainActivity extends AppCompatActivity {
    private ImageView mBlurredView;
    private static int[] bgResArr = {R.drawable.bg_1, R.drawable.bg_2, R.drawable.bg_3, R.drawable.bg_4, R.drawable.sample_map_background};
    private int mBgResIndex = 0;
    private BlurringView mBlurringView;
    private ImageView mDragBar;
    private RelativeLayout mPanelLayout;
    private int mDefaultHeight;
    private int mMaxHeight;
    private int mMinHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setListener();
    }

    private void initViews() {
        mBlurredView = findViewById(R.id.blurred_view);
        mBlurredView.setOnClickListener((view) -> {
            mBgResIndex = (mBgResIndex + 1) % bgResArr.length;
            mBlurredView.setImageResource(bgResArr[mBgResIndex]);
            Log.i("MainActivity", mBlurringView.getTop() + "");
        });
        mBlurringView = findViewById(R.id.blurring_view);
        mBlurringView.setBlurredView(mBlurredView);
        //mBlurringView.setOverlayColor(Color.TRANSPARENT);
        mDragBar = findViewById(R.id.drag_bar);
        mPanelLayout = findViewById(R.id.blurring_view_layout);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListener() {
        mBlurredView.getViewTreeObserver().addOnGlobalLayoutListener(() -> mBlurringView.invalidate());
        mDragBar.setOnTouchListener(new View.OnTouchListener() {
            private int mLastY;
            private boolean mIsGoingUp = false;
            private static final float MIN_HEIGHT_RATIO = 0.2F;
            private static final float MAX_HEIGHT_RATIO = 0.8F;
            private static final float DEFAULT_HEIGHT_RATIO = 0.5F;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mLastY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int offsetY = (int) (mLastY - event.getRawY());
                        if (Math.abs(offsetY) >= 10) {
                            mIsGoingUp = offsetY > 0;
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mPanelLayout.getLayoutParams();
                            params.height = mBlurredView.getHeight() - ((int) event.getRawY()) + mDragBar.getHeight();
                            mPanelLayout.setLayoutParams(params);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        int height = mPanelLayout.getHeight();
                        int blurredViewHeight = mBlurredView.getHeight();
                        int defaultHeight = (int) (blurredViewHeight * DEFAULT_HEIGHT_RATIO);
                        int maxHeight = (int) (blurredViewHeight * MAX_HEIGHT_RATIO);
                        int minHeight = (int) (blurredViewHeight * MIN_HEIGHT_RATIO);
                        if (mIsGoingUp) {
                            if (height >= 0 && height < minHeight) {
                                performAnimation(height, minHeight);
                            } else if (height >= minHeight && height < defaultHeight) {
                                performAnimation(height, defaultHeight);
                            } else if (height >= defaultHeight && height < maxHeight) {
                                performAnimation(height, maxHeight);
                            } else if (height >= maxHeight && height <= blurredViewHeight) {
                                performAnimation(height, maxHeight);
                            }
                        } else {
                            if (height >= 0 && height < minHeight) {
                                performAnimation(height, minHeight);
                            } else if (height >= minHeight && height < defaultHeight) {
                                performAnimation(height, minHeight);
                            } else if (height >= defaultHeight && height <= maxHeight) {
                                performAnimation(height, defaultHeight);
                            } else if (height >= maxHeight && height <= blurredViewHeight) {
                                performAnimation(height, maxHeight);
                            }
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void performAnimation(int start, int end) {
        ValueAnimator va = ValueAnimator.ofInt(start, end);
        va.setDuration(300);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int height = (int) animation.getAnimatedValue();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mPanelLayout.getLayoutParams();
                params.height = height;
                mPanelLayout.setLayoutParams(params);
            }
        });
        va.start();
    }
}
