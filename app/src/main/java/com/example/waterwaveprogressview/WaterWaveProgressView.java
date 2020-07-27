package com.example.waterwaveprogressview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
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
    private Paint circlePaint;
    private Paint textPaint;
    private int circleWidth;
    private int num;

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
        circlePaint.setStrokeWidth(circleWidth);
        circlePaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(getResources().getColor(R.color.white));
        textPaint.setAntiAlias(true);

        runWithAnimation(872);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        RectF rectF = new RectF(circleWidth, circleWidth, getWidth()-circleWidth, getHeight()-circleWidth);
        canvas.drawArc(rectF, -90, 360, false, circlePaint);

        //绘制圆内文字以及设置到准确位置
        Rect rect = new Rect();
        String content;

        textPaint.setTextSize(160);
        content = num + "M";
        textPaint.getTextBounds(content, 0, content.length(), rect);
        int numHeight = rect.height();
        canvas.drawText(content, getWidth()/2-rect.width()/2, getHeight()/2+numHeight/2, textPaint);

        textPaint.setTextSize(60);
        content = "剩余流量";
        textPaint.getTextBounds(content, 0, content.length(), rect);
        canvas.drawText(content, getWidth()/2-rect.width()/2, getHeight()/2-numHeight, textPaint);

        textPaint.setTextSize(55);
        content = "共2GB";
        textPaint.getTextBounds(content, 0, content.length(), rect);
        canvas.drawText(content, getWidth()/2-rect.width()/2, getHeight()/2+numHeight+dp2px(getContext(), 25), textPaint);
    }

    /** *
     * 设置文字滚动动画
     * @param number
     */
    public void runWithAnimation(int number){
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
