package views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mad1.blindeye.R;

import data.ColourItem;
import utils.ClipDataUtil;

//view that displays details of the colour item
public class ColourItemDetailView extends LinearLayout implements View.OnClickListener {

    private TextView mHexString;
    private TextView mRGBString;
    private TextView mHSVString;

    private ColourQuantityBar mQuantityBarRed;
    private ColourQuantityBar mQuantityBarGreen;
    private ColourQuantityBar mQuantityBarBlue;

    private Toast mToast;

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

    @Override
    protected void onDetachedFromWindow() {
        hidingToast();
        super.onDetachedFromWindow();
    }

    public void setColourItem(ColourItem colourItem) {
        final int colour = colourItem.getColour();

        mHexString.setText(colourItem.getHexString());
        mRGBString.setText(colourItem.getRGBString());
        mHSVString.setText(colourItem.getHSVString());

        mQuantityBarRed.setValue(Color.red(colour) / 255f);
        mQuantityBarGreen.setValue(Color.green(colour) / 255f);
        mQuantityBarBlue.setValue(Color.blue(colour) / 255f);
    }

    //Initialize ColourItemDetailView, Should be called in every constructor, the parameter is the context from the constructor(s)
    private void init(Context context) {
        setOrientation(VERTICAL);

        final View view = LayoutInflater.from(context).inflate(R.layout.view_colour_item_detail, this);

        mHexString = (TextView) view.findViewById(R.id.view_colour_item_detail_hexValue);
        mRGBString = (TextView) view.findViewById(R.id.view_colour_item_detail_rgbValue);
        mHSVString = (TextView) view.findViewById(R.id.view_colour_item_detail_hsvValue);

        mQuantityBarRed = (ColourQuantityBar) view.findViewById(R.id.quantity_bar_red);
        mQuantityBarGreen = (ColourQuantityBar) view.findViewById(R.id.quantity_bar_green);
        mQuantityBarBlue = (ColourQuantityBar) view.findViewById(R.id.quantity_bar_blue);

        //set onclick listeners
        mHexString.setOnClickListener(this);
        mRGBString.setOnClickListener(this);
        mHSVString.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        switch (id) {
            case R.id.view_colour_item_detail_hexValue:
                colourClip(R.string.colour_clip_util_hex, mHexString.getText());
                break;
            case R.id.view_colour_item_detail_rgbValue:
                colourClip(R.string.colour_clip_util_rgb, mRGBString.getText());
                break;
            case R.id.view_colour_item_detail_hsvValue:
                colourClip(R.string.colour_clip_util_hsv, mHSVString.getText());
                break;

            default:
                throw new IllegalArgumentException("Clicked an Unsupported View. Found: " + v);
        }
    }

    private void colourClip(int resID, CharSequence colourStr) {
        final Context context = getContext();
        ClipDataUtil.clipText(context, context.getString(resID), colourStr);
        showingToast(R.string.colour_clip_util_success_msg);
    }

    protected void hidingToast() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }

    protected void showingToast(int resID) {
        hidingToast();
        mToast = Toast.makeText(getContext(), resID, Toast.LENGTH_SHORT);
        mToast.show();
    }
}
