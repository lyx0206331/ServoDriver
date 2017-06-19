package com.adrian.wavesview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * @author kangjian
 * @version 1.0
 * @title WaveViewDraw
 * @description 实现图片浪的滚动动画操作
 * 通过动画的方式实现
 * @created 2017/3/24 22:21
 * @changeRecord [修改记录] <br/>
 */

public class WavePicView extends FrameLayout {

    private static final String TAG = WavePicView.class.getSimpleName();

    private Context context;

    private FrameLayout leftWaveFL;
    private FrameLayout centerWaveFL;

    public WavePicView(Context context) {
        super(context);
        this.context = context;
        initWavePicView();
    }

    public WavePicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
        initWavePicView();
    }

    public WavePicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
        initWavePicView();
    }

    private void initWavePicView() {
        this.setClickable(false);
        this.setFocusable(false);
        this.setAnimationCacheEnabled(false);

        leftWaveFL = new FrameLayout(context);
        LayoutParams leftLp = new LayoutParams(1920, LayoutParams.WRAP_CONTENT, Gravity.LEFT | Gravity.BOTTOM);
        leftLp.leftMargin = -1920 * 1;
        leftWaveFL.setLayoutParams(leftLp);
        ImageView leftBackIV = new ImageView(context);
        LayoutParams leftBackLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        leftBackIV.setLayoutParams(leftBackLp);
        leftBackIV.setImageResource(R.drawable.ic_wave_back);
        ImageView leftFrontIV = new ImageView(context);
        LayoutParams leftFrontLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        leftFrontIV.setLayoutParams(leftFrontLp);
        leftFrontIV.setImageResource(R.drawable.ic_wave_front);
        leftWaveFL.addView(leftBackIV);
        leftWaveFL.addView(leftFrontIV);
        leftWaveFL.setTag("left");

        centerWaveFL = new FrameLayout(context);
        LayoutParams centerLp = new LayoutParams(1920, LayoutParams.WRAP_CONTENT, Gravity.CENTER | Gravity.BOTTOM);
        centerWaveFL.setLayoutParams(centerLp);
        ImageView centerBackIV = new ImageView(context);
        LayoutParams centerBackLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        centerBackIV.setLayoutParams(centerBackLp);
        centerBackIV.setImageResource(R.drawable.ic_wave_back);
        ImageView centerFrontIV = new ImageView(context);
        LayoutParams centerFrontLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        centerFrontIV.setLayoutParams(centerFrontLp);
        centerFrontIV.setImageResource(R.drawable.ic_wave_front);
        centerWaveFL.addView(centerBackIV);
        centerWaveFL.addView(centerFrontIV);
        centerWaveFL.setTag("center");

        addView(leftWaveFL);
        addView(centerWaveFL);
    }

    protected void init(AttributeSet attrs) {
    }

    /**
     * 在这里执行动画
     *
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        animateDisplayWave();
    }

    /**
     * 这里找内部控件
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        if (mLeftTag != null) {
        leftWaveFL = (FrameLayout) findViewWithTag("left");
//        }
//        if (mCenterTag != null) {
        centerWaveFL = (FrameLayout) findViewWithTag("center");
//        }
    }

    /**
     * 动画
     */
    private void animateDisplayWave() {
        if (leftWaveFL != null && centerWaveFL != null) {
            leftWaveFL.setAnimationCacheEnabled(false);
            centerWaveFL.setAnimationCacheEnabled(false);
            ObjectAnimator transX_waveLeft = ObjectAnimator.ofFloat(leftWaveFL, "translationX", 0, 1920);
            ObjectAnimator transX_waveCenter = ObjectAnimator.ofFloat(centerWaveFL, "translationX", 0, 1920);
            transX_waveLeft.setRepeatMode(ValueAnimator.RESTART);
            transX_waveLeft.setRepeatCount(Animation.INFINITE);
            transX_waveCenter.setRepeatMode(ValueAnimator.RESTART);
            transX_waveCenter.setRepeatCount(Animation.INFINITE);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(16000);
            animatorSet.setInterpolator(new LinearInterpolator());
            animatorSet.playTogether(transX_waveLeft, transX_waveCenter);
            animatorSet.start();
        }
    }
}
