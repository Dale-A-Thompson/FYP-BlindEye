package com.mad1.blindeye.flavor;

import android.view.Menu;

import com.mad1.blindeye.PaletteDetailActivity;
import com.mad1.blindeye.R;

//static methods for flavour
public final class PaletteDetailActivityFlavour {

    //called when onCreate in PaletteDetailActivity is called
    public static void onCreate(PaletteDetailActivity paletteDetailActivity) {
        //setting title to null
//        paletteDetailActivity.getSupportActionBar().setTitle(null);
    }

    //called when onCreateOptionsMenu is called from PaletteDetailActivity
    public static void onCreateOptionsMenu(Menu menu) {
        //hide edit and share menu item
//        menu.removeItem(R.id.palette_detail_edit_menu);
        menu.removeItem(R.id.palette_detail_share_menu);
    }

    private PaletteDetailActivityFlavour() {
        //non-instantiable class
    }
}
