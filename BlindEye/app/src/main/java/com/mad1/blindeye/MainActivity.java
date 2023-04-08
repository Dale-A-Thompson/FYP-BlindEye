package com.mad1.blindeye;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.melnykov.fab.FloatingActionButton;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import adapters.MainPagerAdapter;
import data.ColourItems;
import fragments.AboutDialogFragment;
import views.ColourListPage;
import views.PaletteListPage;

//activity that shows list of saved colours by the user
public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener,
        ColourListPage.Listener, PaletteListPage.Listener {

    @IntDef({PAGE_ID_COLOUR_ITEM_LIST, PAGE_ID_PALETTE_LIST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PageId {

    }

    //FOR COLOUR ITEM LIST PAGE
    private static final int PAGE_ID_COLOUR_ITEM_LIST = 1;

    //FOR PALETTE LIST PAGE
    private static final int PAGE_ID_PALETTE_LIST = 2;

    //FOR HIDING CURRENT TOAST WIDGET BEFORE DISPLAYING A NEW TOAST OR WHEN THE ACTIVITY OF THE APP IS PAUSED
    private Toast mHideToast;

    //FOR MainActivity
    private Toolbar mToolbar;

    //FOR DISPLAYING THE TABS
    private PagerSlidingTabStrip mPagerSlidingTabStrip;

    //A FAB FOR LAUNCHING THE ColourPickerActivity
    private FloatingActionButton mFloatingActionButton;

    //DISPLAYS THE ColourListPage and PaletteListPage
    private ViewPager mViewPager;

    //TO DISPLAY THE ColourListPage IN THE ViewPager
    private ColourListPage mColourListPage;

    //TO DISPLAY THE PaletteListPage IN THE ViewPager
    private PaletteListPage mPaletteListPage;

    //ID OF CURRENT PAGE
    private int mCurrPageID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);

        mCurrPageID = PAGE_ID_COLOUR_ITEM_LIST;

        mColourListPage = new ColourListPage(this);
        mColourListPage.setListener(this);

        mPaletteListPage = new PaletteListPage(this);
        mPaletteListPage.setListener(this);

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.activity_main_fab);
        mFloatingActionButton.setOnClickListener(this);

        final MyPagerAdapter myPagerAdapter = new MyPagerAdapter();
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.activity_main_tabs);
        mViewPager = (ViewPager) findViewById(R.id.activity_main_view_pager);
        mViewPager.setAdapter(myPagerAdapter);
        mPagerSlidingTabStrip.setViewPager(mViewPager);
        mPagerSlidingTabStrip.setOnPageChangeListener(this);

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (ColourItems.getSavedColour(this).size() <= 1) {
            animateFloatingActionButton(mFloatingActionButton);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hidingToast();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);

        //inflating menu, this'll add the menu items to the action bar if/when present
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        return super.onOptionsItemSelected(item);

        final int id = item.getItemId();
        boolean isHandled;

        switch (id) {
            case R.id.about_menu:
                isHandled = true;
                AboutDialogFragment.newFragmentInstance().show(getSupportFragmentManager(), null);
                break;

            case R.id.contact_us_menu:
                isHandled = true;
                final String uriString = getString(R.string.contact_uri,
                        Uri.encode(getString(R.string.contact_email)), Uri.encode(getString(R.string.contact_default_subject)));

                final Uri mailUri = Uri.parse(uriString);

                final Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(mailUri);
                startActivity(intent);
                break;

            default:
                isHandled = super.onOptionsItemSelected(item);
        }

        return isHandled;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        switch (id) {
            case R.id.activity_main_fab:
                if (mCurrPageID == PAGE_ID_COLOUR_ITEM_LIST) {
                    final Intent intentC = new Intent(this, ColourTapActivity.class);
                    startActivity(intentC);
                } else if (mCurrPageID == PAGE_ID_PALETTE_LIST) {
                    //checking to see if there are, at the very least, 2 colour items
                    //then creating a palette with them, as creating a palette with 1 item or less is essentially pointless.
                    if (ColourItems.getSavedColour(this).size() <= 1) {
                        onPaletteCreationRequestValues();
                        showingToast(R.string.activity_main_error_msg);
                    } else {
                        final Intent intentP = new Intent(this, PaletteMakerActivity.class);
                        startActivity(intentP);
                    }
                }
                break;

            default:
                throw new IllegalArgumentException("View clicked but found: " + v);

        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int id;
        if (position == 0) {
            if (positionOffset <= 0.5) {
                //in range of 0-0.5, scrolling from&to first tab
                //we are remapping the positionOffset in the range of 0-1 with 0 being the position the first tab is visible
                positionOffset *= 2;
                id = PAGE_ID_COLOUR_ITEM_LIST;
            } else {
                //in range of 0-0.5, scrolling from&to second tab
                //we are remapping the positionOffset in the range of 0-1 with 0 being the position the second tab is visible
                positionOffset = (1 - positionOffset) * 2;
                id = PAGE_ID_PALETTE_LIST;
            }
        } else {
            positionOffset = 0;
            id = PAGE_ID_PALETTE_LIST;
        }

        mFloatingActionButton.setTranslationY((((FrameLayout) mFloatingActionButton.getParent()).getHeight() - mFloatingActionButton.getTop()) * positionOffset);
        if (id != mCurrPageID) {
            settingCurrPageID(id);
        }
    }

    @Override
    public void onPageSelected(int position) {
        //nothing to be done here, the current page should already be set in the above method, onPageScrolled
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //nothing to be done here
    }

    @Override
    public void onEmphasisOnAddColourActionRequested() {
        animateFloatingActionButton(mFloatingActionButton, 0);
    }

    @Override
    public void onPaletteCreationRequestValues() {
        if (ColourItems.getSavedColour(this).size() <= 1) {
            //needs more colours for a palette to be possible
            mViewPager.setCurrentItem(0, true);
            animateFloatingActionButton(mFloatingActionButton, 300);
        } else {
            //tap the fab to create a palette
            animateFloatingActionButton(mFloatingActionButton, 0);
        }
    }

    //setting the current page's ID
    private void settingCurrPageID(@PageId int currPageID) {
        mCurrPageID = currPageID;
        if (currPageID == PAGE_ID_COLOUR_ITEM_LIST) {
            mFloatingActionButton.setImageResource(R.drawable.ic_fab_action);
        } else if (currPageID == PAGE_ID_PALETTE_LIST) {
            mFloatingActionButton.setImageResource(R.drawable.ic_palette_action);
        }
    }

    //hiding toast
    private void hidingToast() {
        if (mHideToast != null) {
            mHideToast.cancel();
            mHideToast = null;
        }
    }

    //revealing/showing toast
    private void showingToast(@StringRes int resID) {
        hidingToast();

        String textForToast = getString(resID);
        if (!TextUtils.isEmpty(textForToast)) {
            mHideToast = Toast.makeText(this, resID, Toast.LENGTH_SHORT);
            mHideToast.show();
        }
    }

    //creating subtle animation for fab that hopefully draws attention to the button
    private void animateFloatingActionButton(final FloatingActionButton floatingActionButton) {
        animateFloatingActionButton(floatingActionButton, 400);
    }

    //creating subtle animation for fab that hopefully draws attention to the button
    private void animateFloatingActionButton(final FloatingActionButton floatingActionButton, int fabDelay) {
        floatingActionButton.postDelayed(() -> {
            //playing anim
            final long dur = 300;

            final ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(floatingActionButton, View.SCALE_X, 1f, 1.2f, 1f);
            objectAnimatorX.setDuration(dur);
            objectAnimatorX.setRepeatCount(1);

            final ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(floatingActionButton, View.SCALE_Y, 1f, 1.2f, 1f);
            objectAnimatorY.setDuration(dur);
            objectAnimatorY.setRepeatCount(1);

            objectAnimatorX.start();
            objectAnimatorY.start();

            final AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(objectAnimatorX).with(objectAnimatorY);
            animatorSet.start();
        }, fabDelay);
    }

    private class MyPagerAdapter extends MainPagerAdapter {

        @Override
        public CharSequence getPageTitle(int position) {
//            return super.getPageTitle(position);
            switch (position) {
                case 0:
                    return getString(R.string.activity_main_pager_title_colour);
                case 1:
                    return getString(R.string.activity_main_pager_title_palette);
                default:
                    return getString(R.string.activity_main_pager_title_unknown);
            }
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
//            return super.instantiateItem(container, position);
            final View view;

            if (position == 0) {
                view = mColourListPage;
            } else if (position == 1) {
                view = mPaletteListPage;
            } else {
                throw new IllegalArgumentException("Invalid pos. Supported positions are 0 & 1, found" + position);
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
//            super.destroyItem(container, position, object);
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }
}