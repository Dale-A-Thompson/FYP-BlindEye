package adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.astuetz.PagerSlidingTabStrip;
import com.mad1.blindeye.R;

//Adapter that is used to display the tab from the MainActivity
public abstract class MainPagerAdapter extends PagerAdapter implements PagerSlidingTabStrip.CustomTabProvider {

    public View getCustomTabView(ViewGroup parent, int pos) {
        Context context = parent.getContext();
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setContentDescription(getPageTitle(pos));

        switch (pos) {
            case 0:
                imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_roundwhite));
                break;
            case 1:
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_updated_white_column);
//                drawable.setAlpha(170);
                imageView.setImageDrawable(drawable);
                break;
            default:
                throw new IllegalStateException("Cannot instantiate position" + pos);
        }
        return imageView;
    }
}
