package com.mad1.blindeye;

import androidx.annotation.IntDef;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import views.ColourListPage;
import views.PaletteListPage;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener,
        ColourListPage.Listener, PaletteListPage.Listener {
//    TODO: Create the Listeners in the respective classes to be rid of the red lined errors.


    @IntDef({PAGE_ID_COLOUR_ITEM_LIST, PAGE_ID_PALETTE_LIST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PageId {

    }

    //FOR COLOUR ITEM LIST PAGE
    private static final int PAGE_ID_COLOUR_ITEM_LIST = 1;

    //FOR PALETTE LIST PAGE
    private static final int PAGE_ID_PALETTE_LIST = 2;

    //FOR HIDING CURRENT TOAST WIDGET BEFORE DISPLAYING A NEW TOAST OR WHEN THE ACTIVITY OF THE APP IS PAUSED
    private Toast hideToast;

    //FOR MainActivity
    private Toolbar toolbar;

    //FOR DISPLAYING THE TABS
    private PagerSlidingTabStrip pagerSlidingTabStrip;

    //A FAB FOR LAUNCHING THE ColourPickerActivity
    private FloatingActionButton floatingActionButton;

    //DISPLAYS THE ColourListPage and PaletteListPage
    private ViewPager viewPager;

    //TO DISPLAY THE ColourListPage IN THE ViewPager
    private ColourListPage colourListPage;

    //TO DISPLAY THE PaletteListPage IN THE ViewPager
    private PaletteListPage paletteListPage;

    //ID OF CURRENT PAGE
    private int currPageID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}