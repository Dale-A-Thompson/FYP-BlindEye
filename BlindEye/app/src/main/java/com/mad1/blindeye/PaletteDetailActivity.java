package com.mad1.blindeye;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import data.ColourItem;
import data.Palette;
import data.Palettes;
import fragments.DeletePaletteDialogFragment;
import fragments.EditTextDialogFragment;
import views.FlavourColourItemListWrapper;
import views.PaletteView;
import wrappers.ColourItemListWrapper;

public class PaletteDetailActivity extends AppCompatActivity implements DeletePaletteDialogFragment.Callback, EditTextDialogFragment.Callback,
        ColourItemListWrapper.ColourItemListWrapperListener {

    //img quality compression before sharing
    private static final int IMAGE_SHARE_QUALITY = 100;

    //pixel sizes of shared img
    private static final int IMAGE_SHARE_SIZE = 150;

    //directory name for created shared img
    private static final String DIRECTORY_SHARE = "palettes";

    //file name of written shared img
    private static final String IMAGE_SHARE_FILE = "shared_palettes.jpg";

    //file authority file provided, declared in manifest file
    private static final String FILE_AUTHORITY_PROVIDER = "com.mad1.blindeye.fileprovider";

    //key for passing a palette as extra
    protected static final String COLOUR_PALETTE_EXTRA = "PaletteDetailActivity.Extras.COLOUR_PALETTE_EXTRA";

    //key for passing global rect of clicked colour preview clicked
    protected static final String START_BOUNDS_EXTRA = "PaletteDetailActivity.Extras.START_BOUNDS_EXTRA";

    //inset of shadow square which does need to be considered when evaluating scale ratio
    private int insetShadow;

    public static void startingColourPalette(Context context, Palette palette, View view) {
        final boolean isActivity = context instanceof Activity;
        final Rect startingBound = new Rect();
        view.getGlobalVisibleRect(startingBound);

        final Intent intent = new Intent(context, PaletteDetailActivity.class);
        intent.putExtra(COLOUR_PALETTE_EXTRA, palette);
        intent.putExtra(START_BOUNDS_EXTRA, startingBound);

        if (!isActivity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);

        if (isActivity) {
            ((Activity) context).overridePendingTransition(0, 0);
        }
    }

    //palette view that shows palette preview getting translated during start animation from clicked pos to right location
    protected PaletteView mTranslatedPrev;

    //palette view that shows palette preview getting scaled during start anim to show palette
    protected PaletteView mScaledPrev;

    //palette being displayed
    protected Palette mPalette;

    //toast
    private Toast mToast;

    //onPaletteChangeListener that updates palette when user changes instance name
    private Palettes.OnPaletteChangeListener mOnPaletteChangeListener;

    //a ColourItemListWrapper
    private ColourItemListWrapper mColourItemListWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette_detail);

        //correct extras
        final Intent intent = getIntent();
        if (!intent.hasExtra(COLOUR_PALETTE_EXTRA) || !intent.hasExtra(START_BOUNDS_EXTRA)) {
            throw new IllegalStateException("Extras missing, need to use startingColourPalette");
        }

        //retrieve extras
        if (savedInstanceState == null) {
            mPalette = intent.getParcelableExtra(COLOUR_PALETTE_EXTRA);
        } else {
            mPalette = savedInstanceState.getParcelable(COLOUR_PALETTE_EXTRA);
        }
        final Rect startingBound = intent.getParcelableExtra(START_BOUNDS_EXTRA);
        setTitle(mPalette.getName());

        //creating rect that'll be used to get the stop bounds
        final Rect stoppingBound = new Rect();

        //finding views
        mTranslatedPrev = (PaletteView) findViewById(R.id.palette_detail_translating_prev);
        mScaledPrev = (PaletteView) findViewById(R.id.palette_detail_scaling_prev);

        final View viewShadow = findViewById(R.id.palette_list_detail_view_shadow);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.palette_list_detail_view);
        mColourItemListWrapper = new FlavourColourItemListWrapper(recyclerView, this);
        mColourItemListWrapper.recyclerViewInstallation();
        mColourItemListWrapper.setColourItems(mPalette.getColours());

        insetShadow = getResources().getDimensionPixelSize(R.dimen.shadow_inset_padding);

        mTranslatedPrev.setPalette(mPalette);
        mScaledPrev.setPalette(mPalette);

        final ViewTreeObserver treeObserver = mTranslatedPrev.getViewTreeObserver();
        if (treeObserver.isAlive()) {
            treeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mTranslatedPrev.getViewTreeObserver().removeOnPreDrawListener(this);
                    mTranslatedPrev.getGlobalVisibleRect(stoppingBound);
                    //s = scale
                    final float s = startingBound.width() / (float) stoppingBound.width();
                    mTranslatedPrev.setScaleX(s);
                    mTranslatedPrev.setScaleY(s);

                    //computing bounds again so we can include scale
                    //del = delta
                    mTranslatedPrev.getGlobalVisibleRect(stoppingBound);
                    final int delX = startingBound.left - stoppingBound.left;
                    final int delY = startingBound.top - stoppingBound.top;

                    //s = scale
                    final float sRatioX = (float) (stoppingBound.width() - 2 * insetShadow) / (float) (mScaledPrev.getWidth());
                    final float sRatioY = (float) (stoppingBound.height() - 2 * insetShadow) / (float) mScaledPrev.getHeight();
                    mScaledPrev.setScaleX(sRatioX);
                    mScaledPrev.setScaleY(sRatioY);
                    mScaledPrev.setVisibility(View.INVISIBLE);

                    final AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.play(ObjectAnimator.ofFloat(mTranslatedPrev, View.TRANSLATION_X, delX, 0))
                            .with(ObjectAnimator.ofFloat(mTranslatedPrev, View.TRANSLATION_Y, delY, 0));
                    animatorSet.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(@NonNull Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(@NonNull Animator animation) {
                            recyclerView.setVisibility(View.VISIBLE);
                            mScaledPrev.setVisibility(View.VISIBLE);
                            viewShadow.setVisibility(View.VISIBLE);
                            final AnimatorSet animatorSet1 = new AnimatorSet();
                            animatorSet1.play(ObjectAnimator.ofFloat(mScaledPrev, View.SCALE_X, sRatioX, 1f))
                                    .with(ObjectAnimator.ofFloat(mScaledPrev, View.SCALE_Y, sRatioY, 1f))
                                    .with(ObjectAnimator.ofFloat(recyclerView, View.ALPHA, 0f, 1f))
                                    .with(ObjectAnimator.ofFloat(viewShadow, View.ALPHA, 0f, 1f));
                            animatorSet1.start();
                        }

                        @Override
                        public void onAnimationCancel(@NonNull Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(@NonNull Animator animation) {

                        }
                    });

                    animatorSet.start();
                    //method is a boolean so we have to return true¬
                    return true;
                }
            });
        }

        mOnPaletteChangeListener = new Palettes.OnPaletteChangeListener() {
            @Override
            public void onChangedColourItem(List<Palette> palettes) {
                Palette nPalette = null;
                for (Palette palette : palettes) {
                    if (palette.getID() == mPalette.getID()) {
                        nPalette = palette;
                        break;
                    }
                }
                if (nPalette == null) {
                    //palette opened is not saved palette, meaning, it has been deleted, so finish activity
                    finish();
                } else {
                    //reload palette
                    mPalette = nPalette;
                    setTitle(mPalette.getName());
                    mColourItemListWrapper.setColourItems(mPalette.getColours());
                }
            }
        };

        Palettes.registerListener(this, mOnPaletteChangeListener);
        PaletteDetailActivityFlavour.onCreate(this);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(COLOUR_PALETTE_EXTRA, mPalette);
    }

    @Override
    protected void onPause() {
        hidingToast();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Palettes.unregisterListener(this, mOnPaletteChangeListener);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_palette_details, menu);
        PaletteDetailActivityFlavour.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handling action bar item clicks. this will automatically handle clicks on the home button
        int id = item.getItemId();

        //noinspection if statement
        if (id == R.id.palette_detail_delete_menu) {
            DeletePaletteDialogFragment.newFragmentInstance(mPalette).show(getSupportFragmentManager(), null);
            return true;
        } else if (id == R.id.palette_detail_edit_menu) {
            return actionEditHandler();
        } else if (id == R.id.palette_detail_share_menu) {
            return actionShareHandler();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDeletionConfirmedP(@NonNull Palette palette) {
        //delete palette
        //do not finish activity here, handled by mOnPaletteChangeListener
        Palettes.deletePalette(this, palette);
    }

    @Override
    public void onEditPositiveButtonClicked(int reqCode, String text) {
        if (!mPalette.getName().equals(text)) {
            //set new name and save palette. do not update UI here, handled by mOnPlatteChangeListener
            mPalette.setName(text);
            Palettes.savePalette(this, mPalette);
        }
    }

    @Override
    public void onEditNegativeButtonClicked(int reqCode) {
        //nothing to do! user abandoned updates to the palette
    }

    @Override
    public void onColourItemClicked(@NonNull ColourItem colourItem, @NonNull View colourPreview) {
        ColourDetailActivity.startingColourItem(this, colourItem, colourPreview, mPalette);
    }

    //handling share action from menu
    private boolean actionShareHandler() {
        boolean isHandled;
        try {
            //creating bitmap to draw colour
            final Bitmap bitmap = Bitmap.createBitmap(IMAGE_SHARE_SIZE, IMAGE_SHARE_SIZE, Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(bitmap);

            final List<ColourItem> colourItemList = mPalette.getColours();
            final float size = canvas.getWidth() / ((float) colourItemList.size());
            final RectF rectF = new RectF(0, 0, size, canvas.getHeight());
            final Paint paint = new Paint();

            paint.setStyle(Paint.Style.FILL);
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(mPalette.getName()).append("\n").append("\n");

            for (ColourItem colourItem : colourItemList) {
                paint.setColor(colourItem.getColour());
                canvas.drawRect(rectF, paint);
                rectF.left = rectF.right;
                rectF.right += size;
                stringBuilder.append(colourItem.getHexString()).append("\n");
            }

            //compress bitmap before save/share
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_SHARE_QUALITY, byteArrayOutputStream);
            bitmap.recycle();

            //write compressed bytes to files
            final File fileOutput = new File(getFilesDir(), DIRECTORY_SHARE);

            if (fileOutput.isDirectory() || fileOutput.mkdirs()) {
                final File file = new File(fileOutput, IMAGE_SHARE_FILE);
                final FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(byteArrayOutputStream.toByteArray());
                fileOutputStream.close();

                //content uri get
                final Uri uri = FileProvider.getUriForFile(this, FILE_AUTHORITY_PROVIDER, file);

                //send intent to share img
                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.putExtra(Intent.EXTRA_TEXT, stringBuilder.toString());
                intent.setType("image/jpeg");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, null));
                isHandled = true;
            } else {
                isHandled = false;
            }
        } catch (IOException e) {
            isHandled = false;
        }

        return isHandled;
    }

    //handling the edit action from the menu
    private boolean actionEditHandler() {
        EditTextDialogFragment.newFragmentInstance(0, R.string.palette_detail_activity_edit_text_dialog_fragment,
                R.string.palette_detail_activity_edit_text_dialog_fragment_pos_button, android.R.string.cancel,
                getString(R.string.palette_detail_activity_edit_text_dialog_fragment_hint), mPalette.getName()).show(getSupportFragmentManager(), null);
        return true;
    }

    //hiding current toast
    private void hidingToast() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }

    private void showingToast(@StringRes int resID) {
        hidingToast();
        mToast = Toast.makeText(this, resID, Toast.LENGTH_SHORT);
        mToast.show();
    }
}