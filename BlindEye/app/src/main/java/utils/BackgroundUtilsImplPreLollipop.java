package utils;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

//utils class that is used to encapsulate behaviour linked to the background(s) for Lollipop devices
final class BackgroundUtilsImplPreLollipop {

    //non-instantiable class
    public BackgroundUtilsImplPreLollipop() {
    }

    //programmatically building a background for the app
    static Drawable getBackground(int colour, int pColour) {
        return getPressedDrawableColour(colour, pColour);
    }

    private static Drawable getPressedDrawableColour(int colour, int pColour) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(
                new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled},
                getColourDrawableFromColour(pColour)
        );
        stateListDrawable.addState(
                new int[]{android.R.attr.state_focused},
                getColourDrawableFromColour(pColour)
        );
        stateListDrawable.addState(
                new int[]{android.R.attr.state_activated},
                getColourDrawableFromColour(pColour)
        );
        stateListDrawable.addState(
                new int[]{},
                getColourDrawableFromColour(pColour)
        );
        return stateListDrawable;
    }

    //generate a drawable according to a colour
    private static ColorDrawable getColourDrawableFromColour(int colour) {
        return new ColorDrawable(colour);
    }
}
