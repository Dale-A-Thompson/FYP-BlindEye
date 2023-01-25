package utils;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;

final class BackgroundUtilsImplPostLollipop {

    public BackgroundUtilsImplPostLollipop() {
    }

    //build background programmatically
    static Drawable getBackground(int normalColour, int pressedColour) {
        return getPressedColourRippleDrawable(normalColour, pressedColour);
    }


    //get a ripple drawable
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static RippleDrawable getPressedColourRippleDrawable(int normalColour, int pressedColour) {
        return new RippleDrawable(
                ColorStateList.valueOf(pressedColour),
                new ColorDrawable(normalColour),
                new ColorDrawable(Color.WHITE)
        );
    }
}
