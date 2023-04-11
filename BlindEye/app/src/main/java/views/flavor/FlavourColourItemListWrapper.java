package views.flavor;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mad1.blindeye.R;

import java.util.List;

import data.ColourItem;
import utils.ClipDataUtil;
import views.ColourItemAdapter;
import wrappers.ColourItemListWrapper;

//flavour for ColourItemListWrapper
public class FlavourColourItemListWrapper extends ColourItemListWrapper implements ColourItemAdapter.ColourItemAdapterListener {

    private final ColourItemAdapter mAdapter;
//    private final int mSpanCount;

    private final Context mContext;
    private final String mColourClipLabel;
    private final Application.ActivityLifecycleCallbacks mActivityLifecycleCallbacks;
    private Toast mToast;

    public FlavourColourItemListWrapper(RecyclerView recyclerView, ColourItemListWrapperListener callback) {
        super(recyclerView, callback);
        mAdapter = new ColourItemAdapter(this);
        mContext = recyclerView.getContext();
        mColourClipLabel = mContext.getString(R.string.colour_clip_util_hex);
//        mSpanCount = recyclerView.getContext().getResources().getInteger(R.integer.horizontal_list_span);

        //creating an ActivityLifecycleCallbacks as to hide the toast that would display when pausing the activity
        mActivityLifecycleCallbacks = toastWatcherMaker();
        recyclerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(@NonNull View v) {
                ((Application) mContext.getApplicationContext()).registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull View v) {
                ((Application) mContext.getApplicationContext()).unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
                ;
            }
        });
    }

    //this will hide the toast when the activity is paused (minimized basically)
    private Application.ActivityLifecycleCallbacks toastWatcherMaker() {
        return new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                if (mContext == activity) {
                    hidingToast();
                }
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        };
    }

    @Override
    public void onColourItemClicked(@NonNull ColourItem colourItem, @NonNull View colourPreview) {
        mListener.onColourItemClicked(colourItem, colourPreview);
    }

    @Override
    public void onColourItemClickedLong(@NonNull ColourItem colourItem) {
        ClipDataUtil.clipText(mContext, mColourClipLabel, colourItem.getHexString());
        showingToast(R.string.colour_clip_util_success_msg);
    }

    @Override
    public RecyclerView.Adapter recyclerViewInstallation() {
//        final GridLayoutManager gridLayoutManager = new GridLayoutManager(mRecyclerView.getContext(), mSpanCount);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        return mAdapter;
    }

    @Override
    public void setColourItems(List<ColourItem> items) {
        mAdapter.setColourItems(items);
    }

    @Override
    public void addColourItems(List<ColourItem> newColourAdded) {
        super.addColourItems(newColourAdded);
        mAdapter.addColourItems(newColourAdded);
    }

    //hiding toast
    protected void hidingToast() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }

    //showing toast
    protected void showingToast(int resID) {
        hidingToast();
        mToast = Toast.makeText(mContext, resID, Toast.LENGTH_SHORT);
        mToast.show();
    }
}
