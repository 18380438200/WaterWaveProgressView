package com.example.waterwaveprogressview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

/**
 * create by libo
 * create on 2020/7/28
 * description 水波进度加载view
 */
public class WaterWaveProgressView extends View {
    /**
     * 圆环paint
     */
    private Paint circlePaint;
    /**
     * 文本paint
     */
    private Paint textPaint;
    /**
     * 波浪paint
     */
    private Paint wavePaint;
    private int circleWidth;
    private int padding;
    /**
     * 当前量
     */
    private int num;
    /**
     * 总量
     */
    private float totalNum = 2000;
    private Path wavePath;
    /** 波浪的高度 */
    private int waveRadius = 50;

    public WaterWaveProgressView(Context context) {
        super(context);
        init();
    }

    public WaterWaveProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        circlePaint = new Paint();
        circlePaint.setColor(getResources().getColor(R.color.deep_green));
        circlePaint.setStyle(Paint.Style.STROKE);
        circleWidth = dp2px(getContext(), 12);
        padding = circleWidth;
        circlePaint.setStrokeWidth(circleWidth);
        circlePaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(getResources().getColor(R.color.white));
        textPaint.setAntiAlias(true);

        wavePaint = new Paint();
        wavePaint.setColor(getResources().getColor(R.color.mask_green));
        wavePaint.setStyle(Paint.Style.FILL);
        wavePaint.setAntiAlias(true);

        wavePath = new Path();

        runWithAnimation(872);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        RectF rectF = new RectF(padding/2, padding/2, getWidth() - padding/2, getHeight() - padding/2);
        canvas.drawArc(rectF, -90, 360, false, circlePaint);

        drawWave(canvas);

        drawContentText(canvas);
    }

    /**
     * 绘制圆内文字以及设置到准确位置
     */
    private void drawContentText(Canvas canvas) {
        Rect rect = new Rect();
        String content;

        textPaint.setTextSize(160);
        content = num + "M";
        textPaint.getTextBounds(content, 0, content.length(), rect);
        int numHeight = rect.height();
        canvas.drawText(content, getWidth() / 2 - rect.width() / 2, getHeight() / 2 + numHeight / 2, textPaint);

        textPaint.setTextSize(60);
        content = "剩余流量";
        textPaint.getTextBounds(content, 0, content.length(), rect);
        canvas.drawText(content, getWidth() / 2 - rect.width() / 2, getHeight() / 2 - numHeight, textPaint);

        textPaint.setTextSize(55);
        content = "共" + totalNum / 1000 + "GB";
        textPaint.getTextBounds(content, 0, content.length(), rect);
        canvas.drawText(content, getWidth() / 2 - rect.width() / 2, getHeight() / 2 + numHeight + dp2px(getContext(), 25), textPaint);
    }

    /**
     * 绘制波浪
     */
    private void drawWave(Canvas canvas) {

        wavePath.moveTo(padding, getHeight() / 2);
        wavePath.quadTo(getWidth() / 4, getHeight() / 2 - waveRadius, getWidth() / 2, getHeight() / 2);
        wavePath.quadTo(getWidth() * 3 / 4, getHeight() / 2 + waveRadius, getWidth()-padding, getHeight() / 2);

        RectF rectF = new RectF(padding, padding, getWidth()-padding, getHeight()-padding);
        wavePath.addArc(rectF, 0, 180);

        canvas.drawPath(wavePath, wavePaint);
    }

    /**
     * 设置文字滚动动画
     *
     * @param number
     */
    public void runWithAnimation(int number) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(this, "num", 0, number);
        objectAnimator.setDuration(1000);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();

        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                postInvalidate();
            }
        });
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    private int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
