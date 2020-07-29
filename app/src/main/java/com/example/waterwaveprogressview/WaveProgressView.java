package com.example.waterwaveprogressview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
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
    /**
     * 圆环paint
     */
    private Paint circlePaint;
    /** 圆环paint */
    private Paint ringPaint;
    /**
     * 文本paint
     */
    private Paint textPaint;
    private Path wavePath;
    private int waveWidth;
    /** 波浪幅度 */
    private float waveHeight;
    /** 水面波浪数，在为偶数的情况下，波浪才会按周期平移 */
    private final int waveCount = 2;
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
    private float moveDistance;
    /** 缓存bitmap */
    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private int ringWidth;
    /**
     * 当前量
     */
    private int num;

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
        wavePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(getResources().getColor(R.color.green));

        ringPaint = new Paint();
        ringPaint.setAntiAlias(true);
        ringPaint.setColor(getResources().getColor(R.color.deep_green));
        ringPaint.setStyle(Paint.Style.STROKE);
        ringWidth = dp2px(getContext(), 12);
        ringPaint.setStrokeWidth(ringWidth);

        textPaint = new Paint();
        textPaint.setColor(getResources().getColor(R.color.white));
        textPaint.setAntiAlias(true);

        wavePath = new Path();

        setValue(634, 1024);  //设置当前流量和总流量
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        waveWidth = getMeasuredWidth()/waveCount;
        waveHeight = dp2px(getContext(), 18);
        rectSize = getMeasuredWidth();
        moveDistance = rectSize;

        setMeasuredDimension(rectSize, rectSize);  //宽高大小一致
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBitmap(canvas);

        RectF rectF = new RectF(ringWidth/2, ringWidth/2, rectSize-ringWidth/2, rectSize-ringWidth/2);
        canvas.drawArc(rectF, 0, 360, false, ringPaint);

        drawContentText(canvas);
    }

    private Path setWavePath() {
        wavePath.reset();

        wavePath.moveTo(-moveDistance, rectSize*(1-percent));  //起始点，y值为水流的高度

        //绘制多段波浪
        for (int i=0;i<waveCount*2;i++) {
            wavePath.rQuadTo(waveWidth/2, waveHeight, waveWidth, 0);
            wavePath.rQuadTo(waveWidth/2, -waveHeight, waveWidth, 0);
        }

        wavePath.lineTo(rectSize, rectSize);
        wavePath.lineTo(0, rectSize);
        wavePath.close();

        return wavePath;
    }

    /**
     * 缓存bitmap，将两个canvas重叠，选取显示前景形状，背景颜色的模式
     */
    private void drawBitmap(Canvas canvas) {
        bitmap = Bitmap.createBitmap(rectSize, rectSize, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        bitmapCanvas.drawCircle(rectSize/2, rectSize/2, rectSize/2-ringWidth, circlePaint);
        bitmapCanvas.drawPath(setWavePath(), wavePaint);

        canvas.drawBitmap(bitmap, 0, 0, null);
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

        runWithAnimation(curNum);
    }

    /**
     * 水波上涨动画
     */
    public void setProgressAnim(int num) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, num);
        valueAnimator.setDuration(ANIM_DURATION);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
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
        ValueAnimator valueAnimator = ValueAnimator.ofInt(100, 0);
        valueAnimator.setDuration(ANIM_DURATION);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE); //无限循环
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                movePercent = (int) animation.getAnimatedValue();

                moveDistance = (float)movePercent/100*rectSize;

                postInvalidate();
            }
        });
        valueAnimator.start();
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
        content = "共" + (float)totalNum / 1024 + "GB";
        textPaint.getTextBounds(content, 0, content.length(), rect);
        canvas.drawText(content, getWidth() / 2 - rect.width() / 2, getHeight() / 2 + numHeight + dp2px(getContext(), 25), textPaint);
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
