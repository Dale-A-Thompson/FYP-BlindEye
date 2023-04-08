package views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.mad1.blindeye.R;

public class ColourQuantityBar extends View {
    protected static final float DEFAULT_BAR_WIDTH = 6;
    protected static final float DEFAULT_BAR_OVER_WIDTH = 6;
    protected static final float DEFAULT_THUMB_RADIUS = 7;
    protected static final float DEFAULT_VALUE = 0.6f;

    protected float mBarWidth;
    protected int mBarColour;
    protected Paint mBarPaint;

    protected float mBarOverWidth;
    protected int mBarOverColour;
    protected Paint mBarOverPaint;

    protected float mThumbRadius;
    protected int mThumbColour;
    protected Paint mThumbPaint;
    protected PointF mThumbCenter;

    protected RectF mBounds;
    protected float mValue;

    public ColourQuantityBar(Context context) {
        super(context);
        initView(context);
    }

    public ColourQuantityBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(context);
    }

    public ColourQuantityBar(Context context, AttributeSet attributeSet, int styleAttr) {
        super(context, attributeSet, styleAttr);
        initView(context);
    }

    public ColourQuantityBar(Context context, AttributeSet attributeSet, int styleAttr, int styleRes) {
        super(context, attributeSet, styleAttr, styleRes);
        initView(context);
    }

    protected void initView(Context context) {
        mBarColour = ContextCompat.getColor(context, R.color.colour_quantity_bar_default_bar);
        mBarOverColour = ContextCompat.getColor(context, R.color.colour_quantity_bar_default_bar_over);
        mThumbColour = ContextCompat.getColor(context, R.color.colour_quantity_bar_default_bar_thumb);

        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        mBarWidth = dipToPixel(displayMetrics, DEFAULT_BAR_WIDTH);
        mBarOverWidth = dipToPixel(displayMetrics, DEFAULT_BAR_OVER_WIDTH);

        mBarPaint = new Paint();
        mBarPaint.setColor(mBarColour);
        mBarPaint.setStyle(Paint.Style.STROKE);
        mBarPaint.setStrokeWidth(mBarWidth);

        mBarOverPaint = new Paint();
        mBarOverPaint.setColor(mBarOverColour);
        mBarOverPaint.setStyle(Paint.Style.STROKE);
        mBarOverPaint.setStrokeWidth(mBarOverWidth);

        mThumbRadius = dipToPixel(displayMetrics, DEFAULT_THUMB_RADIUS);
        mThumbPaint = new Paint();
        mThumbPaint.setColor(mThumbColour);
        mThumbPaint.setStyle(Paint.Style.FILL);
        mThumbPaint.setAntiAlias(true);

        mBounds = new RectF(0, 0, 0, 0);
        mThumbCenter = new PointF(0, 0);
        mValue = DEFAULT_VALUE;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int left = (int) (getPaddingLeft() + mThumbRadius);
        final int top = getPaddingTop();
        final int right = (int) (getMeasuredWidth() - getPaddingRight() - mThumbRadius);
        final int bottom = getMeasuredHeight() - getPaddingBottom();


        mBounds.set(left, top, right, bottom);
        updateThumbPos();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBar(canvas);
        drawThumb(canvas);
    }

    //Set Colour of over bar, method parameter is the new colour
    public void setBarOverColour(int barOverColour) {
        mBarOverColour = barOverColour;
        mBarOverPaint.setColor(mBarOverColour);
    }

    //Set colour of bar, method parameter is the new colour
    public void setBarColour(int barColour) {
        mBarColour = barColour;
        mBarPaint.setColor(mBarColour);
    }

    //set colour of thumb
    public void setThumbColour(int thumbColour) {
        mThumbColour = thumbColour;
        mThumbPaint.setColor(mThumbColour);
    }

    //Set value of ColourQuantityBar, method parameter is a value between 0f-1f
    public void setValue(float value) {
        mValue = value;
        updateThumbPos();
        invalidate();
    }

    private void drawThumb(Canvas canvas) {
        canvas.drawCircle(mThumbCenter.x, mThumbCenter.y, mThumbRadius, mThumbPaint);
    }

    private void drawBar(Canvas canvas) {
        canvas.drawLine(mBounds.left, mBounds.centerY(), mBounds.right, mBounds.centerY(), mBarPaint);
        canvas.drawLine(0, mBounds.centerY(), mBounds.width() * mValue, mBounds.centerY(), mBarOverPaint);
    }

    private void updateThumbPos() {
        mThumbCenter.x = mBounds.left + mBounds.width() * mValue;
        mThumbCenter.y = mBounds.centerY();
    }

    private static int dipToPixel(DisplayMetrics displayMetrics, float dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, displayMetrics);
    }
}
