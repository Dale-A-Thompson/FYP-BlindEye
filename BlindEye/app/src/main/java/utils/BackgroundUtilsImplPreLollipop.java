package utils;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

final class BackgroundUtilsImplPreLollipop {

    public BackgroundUtilsImplPreLollipop() {
    }

    static Drawable getBackground(int normalColour, int pressedColour) {
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(
                new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled},
                getColourDrawableFromColour(pressedColour)
        );
        drawable.addState(
                new int[]{android.R.attr.state_focused},
                getColourDrawableFromColour(pressedColour)
        );
        drawable.addState(
                new int[]{android.R.attr.state_activated},
                getColourDrawableFromColour(pressedColour)
        );
        drawable.addState(
                new int[]{},
                getColourDrawableFromColour(pressedColour)
        );
        return drawable;
    }

    //generate a drawable according to a colour
    private static ColorDrawable getColourDrawableFromColour(int colour) {
        return new ColorDrawable(colour);
    }
}
