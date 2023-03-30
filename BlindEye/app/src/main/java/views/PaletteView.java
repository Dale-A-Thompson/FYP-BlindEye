package views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

import data.ColourItem;
import data.Palette;

//A View that will display a Palette, all ColourItem(s) of the Palette are shown in a horizontal rectangle
public class PaletteView extends View {
    //Palette that is being displayed
    private Palette mPalette;

    //RectF that will be used for drawing the ColourItem(s)
    private RectF mRectF;

    //RectF that will be used for computing the drawing area bound(s)
    private RectF mRectFBounds;

    //Paint that will be used for the drawing of the ColourItem(s)
    private Paint mColourItemPaint;

    public PaletteView(Context context) {
        super(context);
        init();
    }

    public PaletteView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public PaletteView(Context context, AttributeSet attributeSet, int defStyleAttribute) {
        super(context, attributeSet, defStyleAttribute);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PaletteView(Context context, AttributeSet attributeSet, int defStyleAttribute, int defStyleRes) {
        super(context, attributeSet, defStyleAttribute, defStyleRes);
        init();
    }

    //Setting the Palette that will be displayed
    public void setPalette(Palette palette) {
        mPalette = palette;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int top = getPaddingTop();
        final int right = getPaddingRight();
        final int bottom = getPaddingBottom();
        final int left = getPaddingLeft();

        //Here we are computing the drawing area which corresponds to the size of the palette view excluding its padding
        mRectFBounds.set(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mPalette == null) {
            //If no Palette exists to be drawn, draw the white rect
            mColourItemPaint.setColor(Color.WHITE);
            canvas.drawRect(mRectFBounds, mColourItemPaint);
        } else {
            //Draw rec for each ColourItem in the Palette
            final List<ColourItem> colourItemList = mPalette.getColours();
            final float widthOfColour = mRectFBounds.width() / colourItemList.size();
            mRectF.set(mRectFBounds.left, mRectFBounds.top, mRectFBounds.left + widthOfColour, mRectFBounds.bottom);

            for (ColourItem colourItem : colourItemList) {
                mColourItemPaint.setColor(colourItem.getColour());
                canvas.drawRect(mRectF, mColourItemPaint);
                mRectF.left = mRectF.right;
                mRectF.right += widthOfColour;
            }
        }
    }

    //Initializing Palette View, much be called once in each of the constructors above
    private void init() {
        mRectF = new RectF();
        mRectFBounds = new RectF();

        mColourItemPaint = new Paint();
        mColourItemPaint.setStyle(Paint.Style.FILL);
    }
}
