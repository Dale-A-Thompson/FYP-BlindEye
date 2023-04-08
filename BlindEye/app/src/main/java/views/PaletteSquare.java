package views;

import android.content.Context;
import android.util.AttributeSet;

//PaletteView that will always be rendered as a square
public class PaletteSquare extends PaletteView {
    //PaletteView(s) which will always be rendered as a square
    public PaletteSquare(Context context) {
        super(context);
    }

    //attributeSet from xml
    public PaletteSquare(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    //defStyleAttribute from xml
    public PaletteSquare(Context context, AttributeSet attributeSet, int defStyleAttribute) {
        super(context, attributeSet, defStyleAttribute);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(w, h);
        int mSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        super.onMeasure(mSpec, mSpec);
    }
}
