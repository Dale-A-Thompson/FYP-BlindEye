package views;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mad1.blindeye.ColourDetailActivity;
import com.mad1.blindeye.R;

import java.util.ArrayList;
import java.util.List;

import data.ColourItem;
import data.ColourItems;
import wrappers.ColourItemListWrapper;

public class ColourListPage extends FrameLayout implements ColourItemListWrapper.ColourItemListWrapperListener {

    //A ColourItemListWrapper
    private ColourItemListWrapper mColourItemListWrapper;

    //A Listener for the creating of a new ColourItem
    private ColourItems.OnColourItemChangeListener mOnColourItemChangeListener;

    //Internal listener to catch view events
    private OnClickListener internalOnClickListener;

    //Current listener to catch view events
    private Listener listener;

    //To display the current colours
    private List<ColourItem> currentColours;

    //Colour was added when the holding activity was in a paused state
    private ArrayList<ColourItem> colourAdded;

    //Listener for catching holding activity resume event
    private Application.ActivityLifecycleCallbacks activityLifecycleCallbacks;

    //Used to see if holding activity is paused
    private boolean isHoldingActivityPaused;

    public ColourListPage(Context context) {
        super(context);
        init(context);
    }

    public ColourListPage(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public ColourListPage(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ColourListPage(Context context, AttributeSet attributeSet, int defStyleAttr, int defStyleRes) {
        super(context, attributeSet, defStyleAttr, defStyleRes);
        init(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ColourItems.registerListener(getContext(), mOnColourItemChangeListener);
        ((Application) getContext().getApplicationContext()).registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ColourItems.unregisterListener(getContext(), mOnColourItemChangeListener);
        ((Application) getContext().getApplicationContext()).registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    //Listener for catching the view events
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private void init(Context context) {
        initLifeCycleListener();

        initInternalListener();

        final View view = LayoutInflater.from(context).inflate(R.layout.view_colour_list_page, this, true);
        final View emptyView = view.findViewById(R.id.view_colour_list_page_view_empty);
        emptyView.setOnClickListener(internalOnClickListener);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.view_colour_list_page_view);
        mColourItemListWrapper = new FlavourColourItemListWrapper(recyclerView, this);

        final RecyclerView.Adapter adapter = mColourItemListWrapper.recyclerViewInstallation();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                emptyView.setVisibility(adapter.getItemCount() == 0 ? VISIBLE : GONE);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                emptyView.setVisibility(adapter.getItemCount() == 0 ? VISIBLE : GONE);
            }
        });

        colourAdded = new ArrayList<>();
        currentColours = ColourItems.getSavedColour(context);
        mColourItemListWrapper.setColourItems(currentColours);
        mOnColourItemChangeListener = new ColourItems.OnColourItemChangeListener() {
            @Override
            public void onChangedColourItem(List<ColourItem> colourItems) {
                if (!isHoldingActivityPaused) {
                    mColourItemListWrapper.setColourItems(colourItems);
                } else {
                    if (colourItems.size() < currentColours.size()) {
                        //colour's been deleted, reloading full list
                        mColourItemListWrapper.setColourItems(colourItems);
                    } else {
                        //colour has been added
                        colourAdded.clear();
                        for (int i = 0; i < colourItems.size() - currentColours.size(); i++) {

                        }
                    }
                }
            }
        };
    }

    //Initialize listener that is used internally to avoid exposing onClick to user
    private void initInternalListener() {
        internalOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onEmphasisOnAddColourActionRequested();
                }
            }
        };
    }

    private void initLifeCycleListener() {
        isHoldingActivityPaused = false;
        activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                if (getContext() == activity) {
                    onHoldingActivityResumed();
                }
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                if (getContext() == activity) {
                    onHoldingActivityResumed();
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

    private void onHoldingActivityResumed() {
        isHoldingActivityPaused = false;
        if (colourAdded.size() > 0) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mColourItemListWrapper.addColourItems(colourAdded);
                    currentColours.addAll(colourAdded);
                    colourAdded.clear();
                }
            }, 500);
        }
    }

    private void onHoldingActivityPaused() {
        isHoldingActivityPaused = true;
    }

    @Override
    public void onColourItemClicked(@NonNull ColourItem colourItem, @NonNull View colourPreview) {
        ColourDetailActivity.startingColourItem(getContext(), colourItem, colourPreview);
    }

    //Used to catch events
    public interface Listener {
        //Called when the user requests emphasis on the add colour action
        //When the user touches an empty view
        void onEmphasisOnAddColourActionRequested();
    }
}
