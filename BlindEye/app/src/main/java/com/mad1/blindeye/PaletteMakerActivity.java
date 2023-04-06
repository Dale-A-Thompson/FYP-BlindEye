package com.mad1.blindeye;

import data.Palette;
import data.Palettes;
import views.PaletteCreatorView;

public class PaletteMakerActivity extends PaletteMakerMainActivity {

    @Override
    protected void makePalette(PaletteCreatorView paletteCreatorView) {
        final Palette palette = paletteCreatorView.create(getString(R.string.activity_palette_maker_default_name_for_palette, System.currentTimeMillis()));
        if (Palettes.savePalette(this, palette)) {
            finish();
        }
    }
}