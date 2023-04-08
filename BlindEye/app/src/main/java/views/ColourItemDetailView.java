package views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.mad1.blindeye.R;

import data.ColourItem;

public class ColourItemDetailView extends LinearLayout {

    private ColourQuantityBar mQuantityBarRed;
    private ColourQuantityBar mQuantityBarGreen;
    private ColourQuantityBar mQuantityBarBlue;

    public ColourItemDetailView(Context context) {
        super(context);
        init(context);
    }

    public ColourItemDetailView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public ColourItemDetailView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ColourItemDetailView(Context context, AttributeSet attributeSet, int defStyle, int defStyleRes) {
        super(context, attributeSet, defStyle, defStyleRes);
        init(context);
    }

    public void setColourItem(ColourItem colourItem) {
        final int colour = colourItem.getColour();

        //TODO: Add hex strings for possibly name edit

        mQuantityBarRed.setValue(Color.red(colour) / 255f);
        mQuantityBarGreen.setValue(Color.green(colour) / 255f);
        mQuantityBarBlue.setValue(Color.blue(colour) / 255f);
    }

    //Initialize ColourItemDetailView, Should be called in every constructor, the parameter is the context from the constructor(s)
    private void init(Context context) {
        setOrientation(VERTICAL);

        final View view = LayoutInflater.from(context).inflate(R.layout.view_colour_item_detail, this);

        mQuantityBarRed = (ColourQuantityBar) view.findViewById(R.id.quantity_bar_red);
        mQuantityBarGreen = (ColourQuantityBar) view.findViewById(R.id.quantity_bar_green);
        mQuantityBarBlue = (ColourQuantityBar) view.findViewById(R.id.quantity_bar_blue);
    }
}
