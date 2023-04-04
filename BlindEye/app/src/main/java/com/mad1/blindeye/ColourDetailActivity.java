package com.mad1.blindeye;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.mad1.blindeye.R;

import data.ColourItem;
import data.ColourItems;
import data.Palette;
import data.Palettes;
import fragments.DeleteColourDialogFragment;
import fragments.EditTextDialogFragment;
import views.ColourItemDetailView;

public class ColourDetailActivity extends AppCompatActivity implements DeleteColourDialogFragment.Callback,
        EditTextDialogFragment.Callback {

    //Passing a colour as an extra
    private static final String EXTRA_COLOUR_ITEM = "ColourDetailActivity.Extras.EXTRA_COLOUR_ITEM";

    //Passing global rect of the clicked colour preview
    private static final String EXTRA_START_BOUNDS = "ColourDetailActivity.Extras.EXTRA_START_BOUNDS";

    //Passing a palette that is optional and is associated with the displayed colour
    private static final String EXTRA_PALETTE = "ColourDetailActivity.Extras.EXTRA_PALETTE";

    //Image quality compression before sharing it
    private static final int SHARED_IMAGE_QUALITY = 100;

    //Pixel sizes of the image
    private static final int SHARED_IMAGE_SIZE = 150;

    //Image Directory where shared image will be created
    private static final String DIRECTORY_NAME_SHARE = "colours";

    //Name of directory where image is created
    private static final String SHARED_IMAGE_FILE = "shared_colours.jpg";

    //File provider authority that is declared in the AndroidManifest.xml
    private static final String FILE_PROVIDER_AUTHORITY = ".fileprovider";

    //Request code for newFragmentInstance()
    private static final int EDIT_COLOUR_ITEM_REQUEST_CODE = 15;

    //Request code to use for EditTextDialogFragment (implemented above)
    public static void startingColourItem(Context context, ColourItem colourItem, View colourPreviewClicked) {
        startingColourItem(context, colourItem, colourPreviewClicked, null);
    }

    public static void startingColourItem(Context context, ColourItem colourItem, View colourPreviewClicked, Palette palette) {
        final boolean isActivity = context instanceof Activity;
        final Rect startBounds = new Rect();
        colourPreviewClicked.getGlobalVisibleRect(startBounds);

        final Intent intent = new Intent(context, ColourDetailActivity.class);
        intent.putExtra(EXTRA_COLOUR_ITEM, colourItem);
        intent.putExtra(EXTRA_START_BOUNDS, startBounds);
        intent.putExtra(EXTRA_PALETTE, palette);

        if (!isActivity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);

        if (isActivity) {
            ((Activity) context).overridePendingTransition(0, 0);
        }
    }

    //View for showing colour preview being translated during start animation from the clicked position to its correct location
    private View mTranslatedPreview;

    //View for showing colour preview getting scaled during its animation to fill the preview container
    private View mScaledPreview;

    //A ColourItemDetailView displaying details of a ColourItem
    private ColourItemDetailView mColourItemDetailView;

    //A ColourItem being Displayed
    private ColourItem mColourItem;

    //A palette, which is optional, that is associated with the ColourItem
    private Palette mPalette;

    //An inset of the round shadow which needs to be taken into account when scale ratio is evaluated
    private int shadowInset;

    //Shadow used as a separator
    private View mShadow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colour_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Intent intent = getIntent();
        if (!intent.hasExtra(EXTRA_COLOUR_ITEM) || !intent.hasExtra(EXTRA_START_BOUNDS) || !intent.hasExtra(EXTRA_PALETTE)) {
            throw new IllegalStateException("Missing extras. Please use startingColourItem.");
        }

        //Retrieving extras
        final Rect startBounds = intent.getParcelableExtra(EXTRA_START_BOUNDS);
        if (savedInstanceState == null) {
            mColourItem = intent.getParcelableExtra(EXTRA_COLOUR_ITEM);
            mPalette = intent.getParcelableExtra(EXTRA_PALETTE);
        } else {
            mColourItem = savedInstanceState.getParcelable(EXTRA_COLOUR_ITEM);
            mPalette = savedInstanceState.getParcelable(EXTRA_PALETTE);
        }

        //Set title of activity with the name of the colour, if it isn't null
        if (!TextUtils.isEmpty(mColourItem.getName())) {
            setTitle(mColourItem.getName());
        } else {
            setTitle(mColourItem.getHexString());
        }

        shadowInset = getResources().getDimensionPixelSize(R.dimen.preview_size_shadow_size);

        //Create rect that is used to retrieve the stop bounds
        final Rect stopBounds = new Rect();

        //Find the views
        mTranslatedPreview = findViewById(R.id.activity_colour_detail_prev_translating);
        mScaledPreview = findViewById(R.id.activity_colour_detail_prev_scaling);
        mColourItemDetailView = (ColourItemDetailView) findViewById(R.id.activity_colour_list_detail_view);
        mColourItemDetailView.setColourItem(mColourItem);
        mShadow = findViewById(R.id.activity_colour_list_detail_view_shadow);

        //displaying ColourItem data
        mTranslatedPreview.getBackground().setColorFilter(mColourItem.getColour(), PorterDuff.Mode.MULTIPLY);
        mScaledPreview.getBackground().setColorFilter(mColourItem.getColour(), PorterDuff.Mode.MULTIPLY);

        final View previewContainer = findViewById(R.id.activity_colour_detail_prev_container);
        final ViewTreeObserver observer = previewContainer.getViewTreeObserver();

        if (observer.isAlive()) {
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    previewContainer.getViewTreeObserver().removeOnPreDrawListener(this);

                    mTranslatedPreview.getGlobalVisibleRect(stopBounds);
                    final float scale = startBounds.width() / (float) stopBounds.width();
                    mTranslatedPreview.setScaleX(scale);
                    mTranslatedPreview.setScaleY(scale);

                    //computing bounds to include scale
                    mTranslatedPreview.getGlobalVisibleRect(stopBounds);
                    final int delY = startBounds.top - stopBounds.top;
                    final int delX = startBounds.left - stopBounds.left;

                    float sRatioX = (startBounds.width() - 2 * shadowInset) / (float) stopBounds.width();
                    float sRatioY = (startBounds.height() - 2 * shadowInset) / (float) stopBounds.height();
                    mScaledPreview.setScaleX(sRatioX);
                    mScaledPreview.setScaleY(sRatioY);

                    final AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.play(ObjectAnimator.ofFloat(mTranslatedPreview, View.TRANSLATION_X, delX, 0))
                            .with(ObjectAnimator.ofFloat(mTranslatedPreview, View.TRANSLATION_Y, delY, -2 * shadowInset));
                    animatorSet.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(@NonNull Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(@NonNull Animator animation) {
                            mScaledPreview.setVisibility(View.VISIBLE);
                            mColourItemDetailView.setVisibility(View.VISIBLE);
                            mShadow.setVisibility(View.VISIBLE);

                            final float maximumConSize = (float) Math.sqrt(Math.pow(previewContainer.getWidth(), 2) + Math.pow(previewContainer.getHeight(), 2));
                            final float maximumSize = Math.max(mScaledPreview.getWidth(), mScaledPreview.getHeight());
                            final float ratioScale = maximumConSize / maximumSize;

                            final AnimatorSet animatorSet1 = new AnimatorSet();
                            animatorSet1.play(ObjectAnimator.ofFloat(mScaledPreview, View.SCALE_X, mScaledPreview.getScaleX(), ratioScale))
                                    .with(ObjectAnimator.ofFloat(mScaledPreview, View.SCALE_Y, mScaledPreview.getScaleY(), ratioScale))
                                    .with(ObjectAnimator.ofFloat(mColourItemDetailView, View.ALPHA, 0f, 1f));
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

                    return true;
                }
            });
        }

        ColourDetailActivityFlavour.onCreate(this);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_COLOUR_ITEM, mColourItem);
        outState.putParcelable(EXTRA_PALETTE, mPalette);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        //inflate menu, adding items to action bar if present
        getMenuInflater().inflate(R.menu.menu_colour_details, menu);
        if (mPalette != null) {
            //colour associated with palette cannot be deleted
            menu.removeItem(R.id.colour_detail_delete_menu);
        }
        ColourDetailActivityFlavour.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
        //handling action bar item clicks. this will automatically handle clicks on the home button
        int id = item.getItemId();

        //noinspection if statement
        if (id == R.id.colour_detail_delete_menu) {
            DeleteColourDialogFragment.newFragmentInstance(mColourItem).show(getSupportFragmentManager(), null); //TODO: having issues implementing show();
            return true;
        } else if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.colour_detail_share_menu) {
            return actionShareHandler(this);
        } else if (id == R.id.colour_detail_edit_menu) {
            EditTextDialogFragment.newFragmentInstance(EDIT_COLOUR_ITEM_REQUEST_CODE, R.string.colour_detail_activity_edit_text_dialog_fragment,
                    R.string.colour_detail_activity_edit_text_dialog_fragment_pos_button, android.R.string.cancel, mColourItem.getHexString(),
                    mColourItem.getName(), true).show(getSupportFragmentManager(), null);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDeletionConfirmedC(@NonNull ColourItem colourItem) {
        if (ColourItems.deleteColourItem(this, colourItem)) {
            finish();
        }
    }

    @Override
    public void onEditPositiveButtonClicked(int reqCode, String text) {
        if (reqCode == EDIT_COLOUR_ITEM_REQUEST_CODE) {
            //updating title of activity
            if (TextUtils.isEmpty(text)) {
                setTitle(mColourItem.getHexString());
            } else {
                setTitle(text);
            }

            //setting new name
            mColourItem.setName(text);

            //persist change
            if (mPalette == null) {
                //colour item is standalone colour, not associated with a palette, just save colour item
                ColourItems.saveColourItem(this, mColourItem);
            } else {
                //colour item is associated with a palette, edit & save palette
                final List<ColourItem> colourItemList = mPalette.getColours();
                for (ColourItem colourItem : colourItemList) {
                    if (colourItem.getID() == mColourItem.getID()) {
                        colourItem.setName(text);
                        break;
                    }
                }
                Palettes.savePalette(this, mPalette);
            }
        }
    }

    @Override
    public void onEditNegativeButtonClicked(int reqCode) {
        //nothing to be done here
    }

    //handle share action from the menu, create bitmap, draw the colour, send it to an intent for sharing the colour
    private boolean actionShareHandler(Context context) {
        boolean isHandled;
        try {
            //creating bitmap to draw colour
            final Bitmap bitmap = Bitmap.createBitmap(SHARED_IMAGE_SIZE, SHARED_IMAGE_SIZE, Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(bitmap);

            //compressing bitmap before save/share
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, SHARED_IMAGE_QUALITY, byteArrayOutputStream);
            bitmap.recycle();

            //compressing bitmap before saving/sharing
            final File file = new File(getFilesDir(), DIRECTORY_NAME_SHARE);
            if (file.isDirectory() || file.mkdirs()) {
                final File colourFile = new File(file, SHARED_IMAGE_FILE);
                final FileOutputStream fileOutputStream = new FileOutputStream(colourFile);
                fileOutputStream.write(byteArrayOutputStream.toByteArray());
                fileOutputStream.close();

                //getting content URi
                final Uri uri = FileProvider.getUriForFile(this, context.getPackageName() + FILE_PROVIDER_AUTHORITY, colourFile);

                //send intent to share image
                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.putExtra(Intent.EXTRA_TEXT, mColourItem.getHexString() + "\n" + mColourItem.getRGBString() + "\n" + mColourItem.getHSVString());
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
}