package com.demo.mediaprojectiondemo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.demo.mediaprojectiondemo.R;
import com.demo.mediaprojectiondemo.databinding.LayoutProjectionViewBinding;
import com.demo.mediaprojectiondemo.service.MediaProjectionService;
import com.demo.mediaprojectiondemo.util.WindowHelper;

public class ProjectionView extends FrameLayout {
    private LayoutProjectionViewBinding binding;
    @Nullable
    private ScreenshotView.ILayoutListener mListener;

    private static boolean isSurfaceCreated = false;

    public ProjectionView(@NonNull Context context) {
        super(context);
        init();
    }

    public ProjectionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProjectionView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        binding = LayoutProjectionViewBinding.inflate(LayoutInflater.from(getContext()), this);
        this.setBackgroundResource(R.drawable.bg_projection_view);
        binding.surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                isSurfaceCreated = true;
                MediaProjectionService.createProjectionVirtualDisplay();
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                isSurfaceCreated = false;
            }
        });
        DisplayMetrics realMetrics = WindowHelper.getRealMetrics();
        binding.surfaceView.getHolder().setFixedSize(realMetrics.widthPixels, realMetrics.heightPixels);
        binding.surfaceView.setOnTouchListener(new OnTouchListener() {
            private float mDownX = 0F;
            private float mDownY = 0F;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mDownX = event.getX();
                        mDownY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mListener != null) {
                            int x = (int) (event.getRawX() - mDownX);
                            int y = (int) (event.getRawY() - mDownY);
                            mListener.onLayout(x, y);
                        }
                }
                return true;
            }
        });
    }

    public static boolean isSurfaceCreated() {
        return isSurfaceCreated;
    }

    public Surface getSurface() {
        return binding.surfaceView.getHolder().getSurface();
    }

    public void setLayoutListener(ScreenshotView.ILayoutListener listener) {
        mListener = listener;
    }

    public interface ILayoutListener {
        void onLayout(int x, int y);
    }
}
