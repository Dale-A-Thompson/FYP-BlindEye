package views;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import data.ColourItem;
import data.Palette;

//View for creating Palette(s).
public class PaletteCreatorView extends View {
    //Name of property that'll be used for width animation of items
    private static final String ITEM_WIDTH_NAME = "itemWidth";

    //Default animation duration of width of items
    private static final int ITEM_ANIM_DEFAULT_DUR = 300;

    //List of ColourItems that the user chose to create a new Palette
    private List<ColourItem> mColourItemList;

    //Item width
    private float mItemWidth;

    //RectF that'll be used for drawing the ColourItems
    private RectF mRectF;

    //ObjectAnimator that'll be used for the animation of the item width(s)
    private ObjectAnimator mObjectAnimator;

    //Paint that'll be used for the drawing of the ColourItem(s)
    private Paint mPaint;

    //Last size of ColourItem(s)
    private int mLSize;

    public PaletteCreatorView(Context context) {
        super(context);
        init();
    }

    public PaletteCreatorView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public PaletteCreatorView(Context context, AttributeSet attributeSet, int defStyleAttribute) {
        super(context, attributeSet, defStyleAttribute);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PaletteCreatorView(Context context, AttributeSet attributeSet, int defStyleAttribute, int defStyleRes) {
        super(context, attributeSet, defStyleAttribute, defStyleRes);
        init();
    }

    //Adding ColourItem to the List of ColourItems that'll create the Palette
    public void colourToPalette(ColourItem colourItem) {
        if (colourItem == null) {
            throw new IllegalArgumentException("Colour cannot be null!");
        }

        mLSize = mColourItemList.size();
        mColourItemList.add(colourItem);
        itemWidthAnim();
    }

    //Removing the recently added ColourItem from the List of ColourItems that'll create the Palette.
    @Nullable
    public ColourItem removeRecentColour() {
        if (mColourItemList.isEmpty()) {
            return null;
        }

        mLSize = mColourItemList.size();
        final ColourItem removedColourItem = mColourItemList.remove(mColourItemList.size() - 1);
        itemWidthAnim();
        return removedColourItem;
    }

    //Checking if there are any ColourItems to be added to the PaletteMakerView
//    public boolean isEmpty() {
//        return mColourItemList.isEmpty();
//    }

    //Getting number of ColourItems added to the PaletteMakerView
//    public int size() {
//        return mColourItemList.size();
//    }

    //Create a Palette with the ColourItem(s) that were added
    public Palette create(String pName) {
        return new Palette(pName, mColourItemList);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mRectF.bottom = getMeasuredHeight();

        if (mColourItemList.isEmpty()) {
            mItemWidth = 0f;
        } else {
            mItemWidth = getMeasuredWidth() / (float) mColourItemList.size();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mColourItemList.isEmpty()) {
            if (mColourItemList.size() == 1) {
                //Only one item to draw. If last was greater, draw item from the left, else draw from the right.
                if (mLSize > mColourItemList.size()) {
                    mRectF.left = 0;
                    mRectF.right = mItemWidth;
                } else {
                    mRectF.right = getMeasuredWidth();
                    mRectF.left = mRectF.right - mItemWidth;
                }

                final ColourItem colourItem = mColourItemList.get(0);
                mPaint.setColor(colourItem.getColour());
                canvas.drawRect(mRectF, mPaint);
            } else {
                //There's more than one item to draw, so draw each item(s) excluding the last one with the same width as :mItemWidth,
                //last item lakes extra space
                final int listSize = mColourItemList.size() - 1;
                mRectF.left = 0;
                mRectF.right = mItemWidth;

                for (int i = 0; i < listSize; i++) {
                    final ColourItem colourItem = mColourItemList.get(i);
                    mPaint.setColor(colourItem.getColour());
                    canvas.drawRect(mRectF, mPaint);

                    mRectF.left = mRectF.right;
                    mRectF.right = mRectF.left + mItemWidth;
                }

                final ColourItem lColourItem = mColourItemList.get(listSize);
                mPaint.setColor(lColourItem.getColour());
                mRectF.right = getMeasuredWidth();
                canvas.drawRect(mRectF, mPaint);
            }
        }
    }

    //Animating the items width
    private void itemWidthAnim() {
        float itemWidth;

        if (mColourItemList.isEmpty()) {
            itemWidth = 0f;
        } else {
            itemWidth = getMeasuredWidth() / (float) mColourItemList.size();
        }

        if (mObjectAnimator.isRunning()) {
            mObjectAnimator.cancel();
        }
        mObjectAnimator.setFloatValues(mItemWidth, itemWidth);
        mObjectAnimator.start();
    }

    //Setters used by the ObjectAnimator to animate the items width
    @SuppressWarnings("unused")
    private void setItemWidth(float itemWidth) {
        mItemWidth = itemWidth;
        invalidate();
    }

    //Initialize PaletteCreatorView, much be called once in each of the constructors above
    private void init() {
        mRectF = new RectF();
        mObjectAnimator = ObjectAnimator.ofFloat(this, ITEM_WIDTH_NAME, 0f, 1f)
                .setDuration(ITEM_ANIM_DEFAULT_DUR);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mColourItemList = new ArrayList<>();
    }

}
