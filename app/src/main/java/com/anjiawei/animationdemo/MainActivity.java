package com.anjiawei.animationdemo;



import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.anjiawei.animationdemo.view.BlurringView;

public class MainActivity extends AppCompatActivity {
    private ImageView mBlurredView;
    private static int[] bgResArr = {R.drawable.bg_1, R.drawable.bg_2, R.drawable.bg_3, R.drawable.bg_4};
    private int mBgResIndex = 0;
    private BlurringView mBlurringView;

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
        mBlurringView.setOverlayColor(Color.TRANSPARENT);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setListener() {
        mBlurredView.getViewTreeObserver().addOnGlobalLayoutListener(() -> mBlurringView.invalidate());
    }
}
