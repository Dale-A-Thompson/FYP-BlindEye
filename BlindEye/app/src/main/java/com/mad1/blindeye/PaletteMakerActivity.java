package com.mad1.blindeye;

import data.Palette;
import data.Palettes;
import fragments.EditTextDialogFragment;
import views.PaletteCreatorView;

//essentially a flavour(flavor) implementation
public class PaletteMakerActivity extends PaletteMakerMainActivity implements EditTextDialogFragment.Callback {

    @Override
    protected void makePalette(PaletteCreatorView paletteCreatorView) {
//        final Palette palette = paletteCreatorView.create(getString(R.string.activity_palette_maker_default_name_for_palette, System.currentTimeMillis()));
//        if (Palettes.savePalette(this, palette)) {
//            finish();
//        }

        final EditTextDialogFragment editTextDialogFragment = EditTextDialogFragment.newFragmentInstance(0,
                R.string.palette_detail_activity_edit_text_dialog_fragment,
                R.string.palette_detail_activity_edit_text_dialog_fragment_pos_button, android.R.string.cancel,
                getString(R.string.palette_detail_activity_edit_text_dialog_fragment_hint), null);
        editTextDialogFragment.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onEditPositiveButtonClicked(int reqCode, String text) {
        final Palette palette = mPaletteCreatorView.create(text);
        if (Palettes.savePalette(this, palette)) {
            finish();
        }
    }

    @Override
    public void onEditNegativeButtonClicked(int reqCode) {
        //user cancels edit, nothing to be done here
    }
}