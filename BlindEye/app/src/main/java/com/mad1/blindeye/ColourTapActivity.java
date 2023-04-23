package com.mad1.blindeye;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.ActionBar;

//ColourTapMainActivity flavour essentially
public class ColourTapActivity extends ColourTapMainActivity {

    private Animation shake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //AnimationUtils is an in built class
        shake = AnimationUtils.loadAnimation(this, R.anim.shaker);
//        mColourSelectedPreviewText.setVisibility(View.INVISIBLE);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(null);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void settingCompleteSave(boolean b) {
        super.settingCompleteSave(b);

        if (b) {
            mSelectedColour.setVisibility(View.INVISIBLE);
            mCompletedSaveMsg.setVisibility(View.VISIBLE);
            mCompletedSaveMsg.animate().translationY(0).setDuration(SAVED_COLOUR_DURATION)
                    .setInterpolator(mCompletedSaveMsgInter).start();
            mCompletedSaveMsg.removeCallbacks(mCompletedSaveMsgHide);
            mCompletedSaveMsg.postDelayed(mCompletedSaveMsgHide, HIDDEN_MESSAGE_DELAY);
//            finish();
        }
    }

    @Override
    protected void selectedColourAnim(int colourSelected) {
        super.selectedColourAnim(colourSelected);
        mButtonToSave.postDelayed(() -> mButtonToSave.startAnimation(shake), 500);
    }
}