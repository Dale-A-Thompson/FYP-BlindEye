package com.mad1.blindeye.flavor;

import android.view.Menu;

import com.mad1.blindeye.ColourDetailActivity;
import com.mad1.blindeye.R;

//static methods for this flavour
public final class ColourDetailActivityFlavour {
    //called when oncreate from ColourDetailActivity is called

    public static void onCreate(ColourDetailActivity colourDetailActivity) {
        //setting title to null
//        colourDetailActivity.getSupportActionBar().setTitle(null);
    }

    //called when onCreateOptionsMenu is called from ColourDetailActivity
    public static void onCreateOptionsMenu(Menu menu) {
        //hide edit & share
//        menu.removeItem(R.id.colour_detail_edit_menu);
        menu.removeItem(R.id.colour_detail_share_menu);
    }

    //non-instantiable class
    private ColourDetailActivityFlavour() {
    }

}
