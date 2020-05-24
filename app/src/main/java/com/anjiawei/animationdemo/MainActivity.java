package com.anjiawei.animationdemo;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.anjiawei.animationdemo.view.BlurringView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity_anjiawei";
    private static final long ANIMATION_DURATION = 300L;
    private ImageView mBlurredView;
    private static int[] bgResArr = {R.drawable.bg_1, R.drawable.bg_2, R.drawable.bg_3, R.drawable.bg_4, R.drawable.sample_map_background};
    private int mBgResIndex = 0;
    private BlurringView mBlurringView;
    private ImageView mDragBar;
    private RelativeLayout mPanelLayout;
    private LinearLayout mDataThirdLine;
    private ViewGroup mRootView;

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
        });
        mBlurringView = findViewById(R.id.blurring_view);
        mBlurringView.setBlurredView(mBlurredView);
        //mBlurringView.setOverlayColor(Color.TRANSPARENT);
        mDragBar = findViewById(R.id.drag_bar);
        mPanelLayout = findViewById(R.id.blurring_view_layout);
        mDataThirdLine = findViewById(R.id.data_third_line);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListener() {
        mBlurredView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            mBlurringView.invalidate();
            Log.i(TAG, "mBlurringView.getHeight() = " + mBlurringView.getHeight());
            Log.i(TAG, "mBlurredView.getHeight() = " + mBlurredView.getHeight());
            if (mPanelLayout.getHeight() == 0) {
                mPanelLayout.getLayoutParams().height = mBlurredView.getHeight() / 2;
                mPanelLayout.requestLayout();
            }
        });
        mDragBar.setOnTouchListener(new View.OnTouchListener() {
            private int mLastY;
            private boolean mIsGoingUp = false;
            private static final float MIN_HEIGHT_RATIO = 0.2F;
            private static final float MAX_HEIGHT_RATIO = 0.8F;
            private static final float DEFAULT_HEIGHT_RATIO = 0.5F;
            private int mDefaultHeight;
            private int mMaxHeight;
            private int mMinHeight;
            private int mBlurredViewHeight;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mLastY = (int) event.getRawY();
                        mBlurredViewHeight = mBlurredView.getHeight();
                        mDefaultHeight = (int) (mBlurredViewHeight * DEFAULT_HEIGHT_RATIO);
                        mMaxHeight = (int) (mBlurredViewHeight * MAX_HEIGHT_RATIO);
                        mMinHeight = (int) (mBlurredViewHeight * MIN_HEIGHT_RATIO);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int offsetY = (int) (mLastY - event.getRawY());
                        if (Math.abs(offsetY) >= 10) {
                            mIsGoingUp = offsetY > 0;
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mPanelLayout.getLayoutParams();
                            params.height = mBlurredView.getHeight() - ((int) event.getRawY()) + mDragBar.getHeight();
                            mPanelLayout.setLayoutParams(params);
                        }
                        height = mPanelLayout.getHeight();
                        if (mIsGoingUp) {
                            if (height >= mDefaultHeight && height <= mMaxHeight) {
                                if (mDataThirdLine.getVisibility() != View.VISIBLE) {
                                    mDataThirdLine.setVisibility(View.VISIBLE);
                                }
                                float alpha = ((float)(height - mDefaultHeight) / (float) (mMaxHeight - mDefaultHeight));
                                mDataThirdLine.setAlpha(alpha);
                            } else if (height >= mMinHeight && height < mDefaultHeight) {
                                mDataThirdLine.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            if (height >= mDefaultHeight && height <= mMaxHeight) {
                                float alpha = ((float)(height - mDefaultHeight) / (float) (mMaxHeight - mDefaultHeight));
                                mDataThirdLine.setAlpha(alpha);
                            } else if (height >= mMinHeight && height < mDefaultHeight) {
                                mDataThirdLine.setVisibility(View.INVISIBLE);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        height = mPanelLayout.getHeight();
                        if (mIsGoingUp) {
                            if (height >= 0 && height < mMinHeight) {
                                performAnimation(height, mMinHeight);
                            } else if (height >= mMinHeight && height < mDefaultHeight) {
                                performAnimation(height, mDefaultHeight);
                            } else if (height >= mDefaultHeight && height < mMaxHeight) {
                                performAnimation(height, mMaxHeight);
                                float alpha = ((float) (height - mDefaultHeight) / (float) (mMaxHeight - mDefaultHeight));
                                changeAlpha(alpha, 1F);
                            } else if (height >= mMaxHeight && height <= mBlurredViewHeight) {
                                performAnimation(height, mMaxHeight);
                            }
                        } else {
                            if (height >= 0 && height < mMinHeight) {
                                performAnimation(height, mMinHeight);
                            } else if (height >= mMinHeight && height < mDefaultHeight) {
                                performAnimation(height, mMinHeight);
                            } else if (height >= mDefaultHeight && height <= mMaxHeight) {
                                performAnimation(height, mDefaultHeight);
                                float alpha = ((float) (height - mDefaultHeight) / (float) (mMaxHeight - mDefaultHeight));
                                changeAlpha(alpha, 0F);
                            } else if (height >= mMaxHeight && height <= mBlurredViewHeight) {
                                performAnimation(height, mMaxHeight);
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
        va.setDuration(ANIMATION_DURATION);
        va.addUpdateListener((animation) -> {
            int height = (int) animation.getAnimatedValue();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mPanelLayout.getLayoutParams();
            params.height = height;
            mPanelLayout.setLayoutParams(params);
        });
        va.start();
    }

    private void changeAlpha(float start, float end) {
        ObjectAnimator oa = ObjectAnimator.ofFloat(mDataThirdLine, "alpha", start, end);
        oa.setDuration(ANIMATION_DURATION);
        oa.start();
    }

    /**
     * 关于点击数据区域切换到所有数据类型面板的动画实现：
     *
     * 本来想用Transition Framework来实现，但是看了一下，不太适合和这里的场景
     * 在切换的过程中有一下几个动画：
     *
     * 1. mPanelLayout 的高度变高，覆盖整个屏幕
     * 2. 被点击的数据从原来的位置移动到屏幕顶部，同时字体大小逐渐增大
     * 3. 原先 mPanelLayout 中的其他View全部fade out隐藏
     * 4. 所有数据类型的面板中的元素fade in 出现
     *
     * 上面几个动画直线型的顺序是：
     * 1，2 同时开始，同时结束
     * 3 在 1，2开始后开始，在 1，2结束前结束
     * 4 在 3开始后开始，跟 1，2同时结束
     *
     * 从所有数据类型面板返回数据展示区则是上面顺序的reverse操作即可
     *
     * 实现上考虑采用AnimatorSet 来统一管理多个动画的执行
     */
}
