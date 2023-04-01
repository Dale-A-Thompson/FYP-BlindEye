package utils;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

//methods (static) for dealing with Views
public class ViewUtil {

    //non-instantiable class
    private ViewUtil() {

    }

    //Converting value(s) from dp (device independent pixels) to pixel
    public static int dpToPixels(DisplayMetrics displayMetrics, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    //ObjectAnimator that will play a 'no' animation
    public static ObjectAnimator noAnim(View view, int d) {
        PropertyValuesHolder propertyValuesHolder = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(.10f, -d),
                Keyframe.ofFloat(.26f, d),
                Keyframe.ofFloat(.42f, -d),
                Keyframe.ofFloat(.58f, d),
                Keyframe.ofFloat(.74f, -d),
                Keyframe.ofFloat(.90f, d),
                Keyframe.ofFloat(1f, 0f));
        return ObjectAnimator.ofPropertyValuesHolder(view, propertyValuesHolder)
                .setDuration(500);
    }
}
