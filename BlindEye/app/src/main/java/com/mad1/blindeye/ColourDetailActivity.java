package com.mad1.blindeye;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import data.ColourItem;
import data.Palette;
import views.ColourItemDetailView;

public class ColourDetailActivity extends AppCompatActivity implements DeleteColourFragment.Callback,
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

    //Name of directory where image is created
    private static final String SHARED_IMAGE_FILE = "shared_colours.jpg";

    //File provider authority that is declared in the AndroidManifest.xml
    private static final String FILE_PROVIDER_AUTHORITY = ".fileprovider";

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
//        TODO: ColourItem methods ^

        shadowInset = getResources().getDimensionPixelSize(R.dimen.preview_size_shadow_size);

        //Create rect that is used to retrieve the stop bounds
        final Rect stopBounds = new Rect();

        //Find the views
        mTranslatedPreview = findViewById(R.id.activity_colour_detail_prev_translating);
        mScaledPreview = findViewById(R.id.activity_colour_detail_prev_scaling);
        mColourItemDetailView = (ColourItemDetailView) findViewById(R.id.activity_colour_list_detail_view);
        mColourItemDetailView.setColourItem(mColourItem);
        mShadow = findViewById(R.id.activity_colour_list_detail_view_shadow);

//        TODO: Finish out this class
    }
}