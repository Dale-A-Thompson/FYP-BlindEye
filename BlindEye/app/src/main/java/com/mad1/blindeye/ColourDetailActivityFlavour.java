package com.mad1.blindeye;

import android.view.Menu;

//static methods for this flavour
final class ColourDetailActivityFlavour {
    //called when oncreate from ColourDetailActivity is called

    static void onCreate(ColourDetailActivity colourDetailActivity) {
        //setting title to null
        colourDetailActivity.getSupportActionBar().setTitle(null);
    }

    //called when onCreateOptionsMenu is called from ColourDetailActivity
    static void onCreateOptionsMenu(Menu menu) {
        //hide edit & share
        menu.removeItem(R.id.colour_detail_edit_menu);
        menu.removeItem(R.id.colour_detail_share_menu);
    }

    //non-instantiable class
    private ColourDetailActivityFlavour() {
    }

}
