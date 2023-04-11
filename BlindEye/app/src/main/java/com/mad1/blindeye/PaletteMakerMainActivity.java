package com.mad1.blindeye;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.mad1.blindeye.flavor.PaletteMakerMainActivityFlavour;
import com.melnykov.fab.FloatingActionButton;

import data.ColourItem;
import data.ColourItems;
import views.flavor.FlavourColourItemListWrapper;
import views.PaletteCreatorView;
import wrappers.ColourItemListWrapper;

//an activity for palette creations from colouritems that the user saved. this has the base interface for the palettes. the palettes are left to this
//activity implementation
public abstract class PaletteMakerMainActivity extends AppCompatActivity implements OnClickListener, ColourItemListWrapper.ColourItemListWrapperListener {

    //floating action button (fab) animation duration
    private static final int ANIM_FAB_DUR = 300;

    //palettecreatorview used for building the palette of the colours
    protected PaletteCreatorView mPaletteCreatorView;

    //objectanimator for animating the button that will remove the final colour from the builder for the palettes
    private ObjectAnimator mFinalColourRemoverBtnAnim;

    //fab to create the palette
    private FloatingActionButton mFloatingActionButton;

    //linearinterpolator to animate the fab above ^
    private LinearInterpolator mLinearInterpolator;

    //decelerateinterpolator to animate the fab above ^^
    private DecelerateInterpolator mDecelerateInterpolator;

    //y translation that'll be used for hiding the fab above ^^
    private int mHideFabTransY;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette_maker);

        mPaletteCreatorView = (PaletteCreatorView) findViewById(R.id.activity_palette_maker_builder);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_palette_maker_view);
        final ColourItemListWrapper colourItemListWrapper = new FlavourColourItemListWrapper(recyclerView, this);
        colourItemListWrapper.recyclerViewInstallation();
        colourItemListWrapper.setColourItems(ColourItems.getSavedColour(this));

        final View rmvBtn = findViewById(R.id.activity_palette_maker_button_remove);
        rmvBtn.setOnClickListener(this);
        rmvBtn.setScaleX(0f);
        mFinalColourRemoverBtnAnim = ObjectAnimator.ofFloat(rmvBtn, View.SCALE_X, 0f, 1f);

        mLinearInterpolator = new LinearInterpolator();
        mDecelerateInterpolator = new DecelerateInterpolator();

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.activity_palette_maker_fab);
        mFloatingActionButton.setOnClickListener(this);

        ViewTreeObserver viewTreeObserver = mFloatingActionButton.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    final ViewTreeObserver treeObserver = mFloatingActionButton.getViewTreeObserver();
                    treeObserver.removeOnPreDrawListener(this);

                    mHideFabTransY = ((View) mFloatingActionButton.getParent()).getHeight() - mFloatingActionButton.getTop();
                    mFloatingActionButton.setTranslationY(mHideFabTransY);
                    return true;
                }
            });
        }

        PaletteMakerMainActivityFlavour.onCreate(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.activity_palette_maker_button_remove:
                mPaletteCreatorView.removeRecentColour();
                if (mPaletteCreatorView.isEmpty()) {
                    mFinalColourRemoverBtnAnim.setFloatValues(0f);
                    mFinalColourRemoverBtnAnim.start();

                    mFloatingActionButton.animate().translationY(mHideFabTransY).setDuration(ANIM_FAB_DUR).setInterpolator(mLinearInterpolator);
                }
                break;
            case R.id.activity_palette_maker_fab:
                if (!mPaletteCreatorView.isEmpty()) {
                    makePalette(mPaletteCreatorView);
                }
                break;

            default:
                throw new IllegalArgumentException("Unsupported View was clicked. Found: " + v);
        }
    }

    @Override
    public void onColourItemClicked(@NonNull ColourItem colourItem, @NonNull View colourPreview) {
        mPaletteCreatorView.colourToPalette(colourItem);
        if (mPaletteCreatorView.size() == 1) {
            mFinalColourRemoverBtnAnim.setFloatValues(1f);
            mFinalColourRemoverBtnAnim.start();

            mFloatingActionButton.animate().translationY(0).setDuration(ANIM_FAB_DUR).setInterpolator(mDecelerateInterpolator);
        }
    }

    //create a palette, this is called when the user wants to make a palette
    protected abstract void makePalette(PaletteCreatorView paletteCreatorView);
}