package com.anjiawei.animationdemo;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.anjiawei.animationdemo.view.BlurringView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final long ANIMATION_DURATION = 300L;
    private ImageView mBlurredView;
    private static int[] bgResArr = {R.drawable.bg_1, R.drawable.bg_2, R.drawable.bg_3, R.drawable.bg_4, R.drawable.sample_map_background};
    private int mBgResIndex = 0;
    private BlurringView mBlurringView;
    private ImageView mDragBar;
    private RelativeLayout mPanelLayout;
    private LinearLayout mDataThirdLine;

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
        mDataThirdLine = findViewById(R.id.data_third_line);
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
}
