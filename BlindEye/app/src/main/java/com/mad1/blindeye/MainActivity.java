package com.mad1.blindeye;

import androidx.annotation.IntDef;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener,
        ColorItemListPage.Listener, PalletteListPage.Listener {

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

    private

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}