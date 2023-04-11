package views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.mad1.blindeye.R;

//a Drawable class that is used to render the/a blob colour with a shadow, hopefully smooth
class ColourBlobDrawable extends GradientDrawable {

    private final Paint paint;
    private final float shadowSize;

    //drawable to render a blob colour with smooth shadow
    public ColourBlobDrawable(Context context) {
        super(
                Orientation.TOP_BOTTOM,
                new int[]{
                        ContextCompat.getColor(context, R.color.black_shadow),
                        Color.TRANSPARENT
                }
        );
        Resources resources = context.getResources();

        shadowSize = resources.getDimensionPixelSize(R.dimen.shadow_size);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);

        setGradientType(RADIAL_GRADIENT);
        setGradientCenter(0.5f, 0.5f);
    }

    @Override
    protected void onBoundsChange(Rect r) {
        super.onBoundsChange(r);
        setGradientRadius(r.width() / 2f);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();

        //drawing shadow
        super.draw(canvas);

        //draw dot color
        canvas.drawCircle(bounds.centerX(), bounds.centerY(), bounds.width() / 2f - shadowSize * 2, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
