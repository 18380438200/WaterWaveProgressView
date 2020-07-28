package com.example.waterwaveprogressview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.annotation.Nullable;

/**
 * create by libo
 * create on 2020/7/28
 * description
 */
public class WaveProgressView extends View {
    /**
     * 波浪paint
     */
    private Paint wavePaint;
    private Path wavePath;
    private int waveWidth;
    private int waveHeight;
    /** 水面波浪数 */
    private int waveCount = 5;
    private int curProgress;
    /** 该view尺寸大小 */
    private int rectSize;
    /** 水流深度 */
    private int flowDepth;

    public WaveProgressView(Context context) {
        super(context);
        init();
    }

    public WaveProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        wavePaint = new Paint();
        wavePaint.setColor(getResources().getColor(R.color.mask_green));
        wavePaint.setStyle(Paint.Style.FILL);
        wavePaint.setAntiAlias(true);

        wavePath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        waveWidth = getMeasuredWidth()/waveCount;
        waveHeight = dp2px(getContext(), 15);
        rectSize = getMeasuredWidth();


        setMeasuredDimension(rectSize, rectSize);  //宽高大小一致
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(setWavePath(), wavePaint);
    }

    private Path setWavePath() {
        wavePath.reset();

        wavePath.moveTo(0, waveHeight);  //起始点

        //绘制多段波浪
        for (int i=0;i<waveCount;i++) {
            wavePath.rQuadTo(waveWidth/2, waveHeight, waveWidth, 0);
            wavePath.rQuadTo(waveWidth/2, -waveHeight, waveWidth, 0);
        }

        wavePath.lineTo(getWidth(), getHeight());
        wavePath.lineTo(0, getHeight());
        wavePath.close();

        return wavePath;
    }

    /**
     * 进度更新动画
     * @param num
     * @param animDuration
     */
    public void setProgressAnim(int num, int animDuration) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, num);
        valueAnimator.setDuration(animDuration);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                curProgress = (int) animation.getAnimatedValue();

                postInvalidate();
            }
        });
        valueAnimator.start();
    }

    private int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}
