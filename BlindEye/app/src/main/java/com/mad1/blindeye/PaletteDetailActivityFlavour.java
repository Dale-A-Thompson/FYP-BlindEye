package com.mad1.blindeye;

import android.view.Menu;

//static methods for flavour
final class PaletteDetailActivityFlavour {

    //called when onCreate in PaletteDetailActivity is called
    static void onCreate(PaletteDetailActivity paletteDetailActivity) {
        //setting title to null
        paletteDetailActivity.getSupportActionBar().setTitle(null);
    }

    //called when onCreateOptionsMenu is called from PaletteDetailActivity
    static void onCreateOptionsMenu(Menu menu) {
        //hide edit and share menu item
        menu.removeItem(R.id.palette_detail_edit_menu);
        menu.removeItem(R.id.palette_detail_share_menu);
    }

    private PaletteDetailActivityFlavour() {
        //non-instantiable class
    }
}
