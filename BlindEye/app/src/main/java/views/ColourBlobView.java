package views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import utils.BackgroundUtils;

//a View that's used to render the colour dot(s)
//View to render the colour blob (or dot), I will be referring to it as a blob for future reference
public class ColourBlobView extends View {

    //view to render blob
    public ColourBlobView(Context context, AttributeSet attributeSet, int styleAttribute) {
        super(context, attributeSet, styleAttribute);
        initialize(context);
    }

    //view to render blob
    public ColourBlobView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    //view to render blob
    //context to hold current context
    public ColourBlobView(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int s = Math.min(w, h);
        int mSpec = MeasureSpec.makeMeasureSpec(s, MeasureSpec.EXACTLY);
        setMeasuredDimension(mSpec, mSpec);
    }

    //initializing the internal component
    private void initialize(Context context) {
        if (!isInEditMode()) {
            BackgroundUtils.setBackground(this, new ColourBlobDrawable(context));
        }
    }
}
