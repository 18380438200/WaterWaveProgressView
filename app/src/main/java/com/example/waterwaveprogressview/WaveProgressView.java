package com.example.waterwaveprogressview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.annotation.Nullable;

import javax.security.auth.login.LoginException;

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
    private final int waveCount = 5;
    /** 当前数值 */
    private int curNum;
    /** 总数值 */
    private int totalNum;
    /** 最终进度比例 */
    private float percent;
    /** 当前轮水波移动比例 */
    private int movePercent;
    /** 该view尺寸大小 */
    private int rectSize;
    private final int ANIM_DURATION = 2000;

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

        wavePath.moveTo(0, rectSize*(1-percent));  //起始点，y值为水流的高度

        //绘制多段波浪
        for (int i=0;i<waveCount;i++) {
            wavePath.rQuadTo(waveWidth/2, waveHeight, waveWidth, 0);
            wavePath.rQuadTo(waveWidth/2, -waveHeight, waveWidth, 0);
        }

        wavePath.lineTo(rectSize, rectSize);
        wavePath.lineTo(0, rectSize);
        wavePath.close();

        return wavePath;
    }

    /**
     * 设置当前值和总值，并开启动画
     * @param curNum
     * @param totalNum
     */
    public void setValue(int curNum, int totalNum) {
        this.totalNum = totalNum;
        setProgressAnim(curNum);

        setWaveMoveAnim();
    }

    /**
     * 进度更新动画
     */
    public void setProgressAnim(int num) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, num);
        valueAnimator.setDuration(ANIM_DURATION);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                curNum = (int) animation.getAnimatedValue();
                percent = (float) curNum/totalNum;  //实时更新进度状态
            }
        });
        valueAnimator.start();
    }

    /**
     * 波浪平移动画
     */
    private void setWaveMoveAnim() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
        valueAnimator.setDuration(ANIM_DURATION);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE); //无限循环
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                movePercent = (int) animation.getAnimatedValue();

                Log.i("minfo", "movePercent" + movePercent);

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
