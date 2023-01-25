package utils;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class BackgroundUtils {

    //non-instantiable class
    public BackgroundUtils() {

    }

    //building the background programmatically
    public static void drawableBackground(View view, int normalColour, int pressedColour) {
        BackgroundUtils.setBackground(view,
                BackgroundUtils.drawableBackground(normalColour, pressedColour));
    }

    public static Drawable drawableBackground(int normalColour, int pressedColour) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return BackgroundUtilsImplPostLollipop.getBackground(normalColour, pressedColour);
        } else {
            return BackgroundUtilsImplPreLollipop.getBackground(normalColour, pressedColour);
        }
    }

    //set background scross android versions
    public static void setBackground(@NonNull View view, @Nullable Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

}
