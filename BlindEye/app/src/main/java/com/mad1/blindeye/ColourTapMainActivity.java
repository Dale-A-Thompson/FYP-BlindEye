package com.mad1.blindeye;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import data.ColourItem;
import data.ColourItems;
import utils.CameraUtil;
import views.CameraPreviewPicker;

//an activity for choosing colours thru camera
public class ColourTapMainActivity extends AppCompatActivity implements CameraPreviewPicker.OnColourSelectedListener, View.OnClickListener {

    //tag to be used in logcat
    protected static final String TAG = ColourTapMainActivity.class.getSimpleName();

    //property name to animate colour chosen
    protected static final String TAPPED_COLOUR_NAME_FOR_PROGRESS = "tappedColour";

    //property name that animates completed save
    protected static final String SAVED_COLOUR_NAME_COMPLETE_PROGRESS = "tappedColourSaved";

    //animation duration to confirm save
    protected static final long SAVED_COLOUR_DURATION = 400;

    //delay before the save confirmation is hidden
    protected static final long HIDDEN_MESSAGE_DELAY = 1400;

    //instance of the camera
    private static Camera instanceOfCamera() {
        Camera camera = null;
        try {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return camera;
    }

    //camera instance for displaying preview
    protected Camera mCamera;

    //boolean for orientation of activity
    protected boolean mIsCameraPortrait;

    //framelayout that will contain preview
    protected FrameLayout mFrameLayout;

    //CameraPreviewPicker used for the preview
    protected CameraPreviewPicker mCameraPreviewPicker;

    //inner class that gets the camera
    protected AsyncCameraTask mAsyncCameraTask;

    //slected colour from user
    protected int mColourSelected;

    //recent/last picked colour
    protected int mLastColourSelected;

    //view to show selected colour
    protected View mColourSelectedPreview;

    //view to animate selected colour
    protected View mColourSelectedAnim;

    //objectanimator to animate selected colour
    protected ObjectAnimator mColourSelectedObjAnim;

    //delta value for x-axis translation of mColourSelectedAnim
    protected float mTransDelX;

    //delta value for y-axis translation of mColourSelectedAnim
    protected float mTransDelY;

    //TextView for showing string values of selected colour
    protected TextView mColourSelectedPreviewText;

    //view for selected colour
    protected View mSelectedColour;

    //save complete state
    protected View mCompletedSave;

    //save button view
    protected View mButtonToSave;

    //float for progress of complete save
    protected float mCompletedSaveProgress;

    //objectanimator to animate ^complete save^
    protected ObjectAnimator mCompleteSaveProgressAnim;

    //TextView to confirm colour has been saved for user
    protected TextView mCompletedSaveMsg;

    //interpolator to show ^mCompletedSaveMsg^
    protected Interpolator mCompletedSaveMsgInter;

    //Runnable to hide save message, posted with delay each time colour is saved
    protected Runnable mCompletedSaveMsgHide;

    //boolean that keeps track of the state of camera flash - on or off
    protected boolean mIsCameraFlashEnabled;

    //intent action for current activity
    protected String intentAction = null;

    //http://www.openintents.org/action/org-openintents-action-pick-color/
    public static final String OPEN_INTENT_COLOUR_PICK = "org.openintents.action.PICK_COLOR";
    public static final String OPEN_INTENT_COLOUR_DATA = "org.openintents.extra.COLOR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colour_tap_main_actvity);

        initializeSelectedColourAnim();
        initializeCompletedSaveProgressAnim();
        initializeViews();
        initializeTransDeltas();

        Intent intent = getIntent();
        if (intent != null) {
            intentAction = intent.getAction();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //setting up async task
        mAsyncCameraTask = new AsyncCameraTask();
        mAsyncCameraTask.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //cancelling async task
        mAsyncCameraTask.cancel(true);

        //stop camera
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }

        //remove camera's preview
        if (mCameraPreviewPicker != null) {
            mFrameLayout.removeView(mCameraPreviewPicker);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //remove pending complete save messages that may be pending
        mCompletedSaveMsg.removeCallbacks(mCompletedSaveMsgHide);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        if (isCameraFlashSupported()) {
            getMenuInflater().inflate(R.menu.menu_selected_colour, menu);
            final MenuItem menuItem = menu.findItem(R.id.colour_selected_main_menu_action);
            int flash = mIsCameraFlashEnabled ? R.drawable.ic_flash_off : R.drawable.ic_flash_on;
            menuItem.setIcon(flash);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        return super.onOptionsItemSelected(item);
        final int itemID = item.getItemId();
        boolean isHandled;
        switch (itemID) {
            case android.R.id.home:
                finish();
                isHandled = true;
                break;
            case R.id.colour_selected_main_menu_action:
                cameraFlashToggle();
                isHandled = true;
                break;

            default:
                isHandled = super.onOptionsItemSelected(item);
        }
        return isHandled;
    }

    @Override
    public void onSelectedColour(int newColour) {
        mColourSelected = newColour;
        mSelectedColour.getBackground().setColorFilter(newColour, PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void onClick(View v) {
        if (v == mCameraPreviewPicker) {
            selectedColourAnim(mColourSelected);
        } else if (v.getId() == R.id.activity_colour_selected_button_save) {
            if (OPEN_INTENT_COLOUR_PICK.equals(intentAction)) {
                Intent intent = new Intent();
                intent.putExtra(OPEN_INTENT_COLOUR_DATA, mLastColourSelected);
                setResult(RESULT_OK, intent);
                finish();
                return;
            }
            ColourItems.saveColourItem(this, new ColourItem(mLastColourSelected));
            settingCompleteSave(true);
        }
    }

    //setting save state
    protected void settingCompleteSave(boolean b) {
        mButtonToSave.setEnabled(!b);
        mCompleteSaveProgressAnim.cancel();
        mCompleteSaveProgressAnim.setFloatValues(mCompletedSaveProgress, b ? 0f : 1f);
        mCompleteSaveProgressAnim.start();
    }

    //animating selected colour
    protected void selectedColourAnim(int colourSelected) {
        mLastColourSelected = colourSelected;
        if (mColourSelectedObjAnim.isRunning()) {
            mColourSelectedObjAnim.cancel();
        }

        mColourSelectedObjAnim.start();
    }

    //toggling the device in use's cam flash
    protected void cameraFlashToggle() {
        if (mCamera != null) {
            final Camera.Parameters cameraParameters = mCamera.getParameters();
            final String flashParameter = mIsCameraFlashEnabled ? Camera.Parameters.FLASH_MODE_OFF : Camera.Parameters.FLASH_MODE_TORCH;
            cameraParameters.setFlashMode(flashParameter);

            //setting preview callback to null so the preview stops
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();

            //changing params
            mCamera.setParameters(cameraParameters);

            //restoring preview callback to restart preview
            mCamera.setPreviewCallback(mCameraPreviewPicker);
            mCamera.startPreview();

            mIsCameraFlashEnabled = !mIsCameraFlashEnabled;
            invalidateOptionsMenu();
        }
    }

    //checking if the device in use's camera supports flash
    protected boolean isCameraFlashSupported() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    //initializing delta values for translation of the selected colour preview
    @SuppressLint("NewApi")
    protected void initializeTransDeltas() {
        ViewTreeObserver viewTreeObserver = mSelectedColour.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ViewTreeObserver treeObserver = mSelectedColour.getViewTreeObserver();
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                        treeObserver.removeGlobalOnLayoutListener(this);
                    } else {
                        treeObserver.removeOnGlobalLayoutListener(this);
                    }

                    final Rect ring = new Rect();
                    final Rect colourPrevAnim = new Rect();
                    mColourSelectedAnim.getGlobalVisibleRect(ring);
                    mColourSelectedPreview.getGlobalVisibleRect(colourPrevAnim);

                    mTransDelX = colourPrevAnim.left - ring.left;
                    mTransDelY = colourPrevAnim.top - ring.top;
                }
            });
        }
    }

    //initialize views in activity
    protected void initializeViews() {
        mIsCameraPortrait = getResources().getBoolean(R.bool.is_camera_portrait);
        mFrameLayout = (FrameLayout) findViewById(R.id.activity_colour_selected_prev_container);
        mColourSelectedPreview = findViewById(R.id.activity_selected_colour_prev);
        mColourSelectedAnim = findViewById(R.id.activity_colour_selected_anim_prev);
        mColourSelectedPreviewText = (TextView) findViewById(R.id.activity_selected_colour_prev_text);
        mSelectedColour = findViewById(R.id.activity_colour_selected_ring);
        mCompletedSave = findViewById(R.id.activity_colour_selected_save_complete);
        mButtonToSave = findViewById(R.id.activity_colour_selected_button_save);
        mButtonToSave.setOnClickListener(this);
        mCompletedSaveMsg = (TextView) findViewById(R.id.activity_selected_colour_selected_save_msg);
        mCompletedSaveMsgHide = () -> mCompletedSaveMsg.animate().translationY(-mCompletedSaveMsg.getMeasuredHeight()).setDuration(SAVED_COLOUR_DURATION).start();

        confirmCompleteSaveMsg();
        mCompletedSaveMsgInter = new DecelerateInterpolator();

        mLastColourSelected = ColourItems.getLastPickedColour(this);
        previewColour(mLastColourSelected);
    }

    //applying preview colour, displaying preview colour and readable format
    protected void previewColour(int colourSelected) {
        settingCompleteSave(false);
        mColourSelectedPreview.getBackground().setColorFilter(colourSelected, PorterDuff.Mode.SRC_ATOP);
        mColourSelectedPreviewText.setText(ColourItem.createHexString(colourSelected).toUpperCase());
        Log.d("displaycolour", "previewColour: " + mColourSelectedPreviewText.getText());
    }

    //setting transY of mCompletedSaveMsg to -mCompletedSaveMsg.getMeasuredHeight() so it's correctly placed before anim
    protected void confirmCompleteSaveMsg() {
        ViewTreeObserver viewTreeObserver = mCompletedSaveMsg.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    ViewTreeObserver treeObserver = mCompletedSaveMsg.getViewTreeObserver();
                    treeObserver.removeOnPreDrawListener(this);
                    mCompletedSaveMsg.setTranslationY(-mCompletedSaveMsg.getMeasuredHeight());
                    return true;
                }
            });
        }
    }

    //initializing animator used for selected colour progress
    protected void initializeCompletedSaveProgressAnim() {
        mCompleteSaveProgressAnim = ObjectAnimator.ofFloat(this, SAVED_COLOUR_NAME_COMPLETE_PROGRESS, 1f, 0f);
    }

    //initializing animator used for progress of selected colour
    protected void initializeSelectedColourAnim() {
        mColourSelectedObjAnim = ObjectAnimator.ofFloat(this, TAPPED_COLOUR_NAME_FOR_PROGRESS, 0f, 1f);
        mColourSelectedObjAnim.setDuration(400);
        mColourSelectedObjAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
                mColourSelectedAnim.setVisibility(View.VISIBLE);
                mColourSelectedAnim.getBackground().setColorFilter(mColourSelected, PorterDuff.Mode.SRC_ATOP);
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                ColourItems.saveLastColour(ColourTapMainActivity.this, mLastColourSelected);
                previewColour(mLastColourSelected);
                mColourSelectedAnim.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {
                mColourSelectedAnim.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });
    }

    //setting progress of selected colour anim
    protected void setTappedColourSaved(float p) {
        final float progress = (float) Math.pow(p, 0.3f);
        final float transX = mTransDelX * p;
        final float transY = (float) (mTransDelY * Math.pow(p, 2f));

        mColourSelectedAnim.setTranslationX(transX);
        mColourSelectedAnim.setTranslationY(transY);
        mColourSelectedAnim.setScaleX(progress);
        mColourSelectedAnim.setScaleY(progress);
    }

    //setting progress of save complete anim state
    protected void setTappedColour(float p) {
        mButtonToSave.setScaleX(p);
        mButtonToSave.setRotation(45 * (1 - p));
        mCompletedSave.setScaleX(1 - p);
        mCompletedSaveProgress = p;
    }

    //async task to configure/start camera preview
    private class AsyncCameraTask extends AsyncTask<Void, Void, Camera> {
        //layout params for adding preview to its container
        protected FrameLayout.LayoutParams mLayoutParams;

        @Override
        protected Camera doInBackground(Void... voids) {
            Camera camera = instanceOfCamera();
            if (camera == null) {
                ColourTapMainActivity.this.finish();
            } else {
                //configure params
                Camera.Parameters parameters = camera.getParameters();

                //getting ideal cam size according to the layout to display
                Camera.Size ideal = CameraUtil.bestPreviewSize(parameters.getSupportedPreviewSizes(), mFrameLayout.getWidth(), mFrameLayout.getHeight(),
                        mIsCameraPortrait);

                //set ideal view
                parameters.setPreviewSize(ideal.width, ideal.height);
                camera.setParameters(parameters);

                //setting cam orientation to match device in use
                CameraUtil.displayOrien(ColourTapMainActivity.this, camera);

                //proportional dimen for layout to display preview
                int[] dimens = CameraUtil.proportionalDimen(ideal, mFrameLayout.getWidth(), mFrameLayout.getHeight(), mIsCameraPortrait);

                //setting params for layout preview
                mLayoutParams = new FrameLayout.LayoutParams(dimens[0], dimens[1]);
                mLayoutParams.gravity = Gravity.CENTER;
            }
            return camera;
        }

        @Override
        protected void onPostExecute(Camera camera) {
            super.onPostExecute(camera);

            //checking if task cancelled before camera is in use
            if (!isCancelled()) {
                mCamera = camera;
                if (mCamera == null) {
                    ColourTapMainActivity.this.finish();
                } else {
                    //set cam preview
                    mCameraPreviewPicker = new CameraPreviewPicker(ColourTapMainActivity.this, mCamera);
                    mCameraPreviewPicker.setOnColourSelectedListener(ColourTapMainActivity.this);
                    mCameraPreviewPicker.setOnClickListener(ColourTapMainActivity.this);

                    //adding cam preview
                    mFrameLayout.addView(mCameraPreviewPicker, 0, mLayoutParams);
                }
            }
        }

        @Override
        protected void onCancelled(Camera camera) {
            super.onCancelled(camera);
            if (camera != null) {
                camera.release();
            }
        }
    }
}