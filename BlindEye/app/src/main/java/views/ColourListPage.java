package views;

import android.app.Application;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.mad1.blindeye.R;

import java.util.ArrayList;
import java.util.List;

import data.ColourItem;
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

    private void init(Context context) {
        initLifeCycleListener();

        initInternalListener();

        final View view = LayoutInflater.from(context).inflate(R.layout.view_colour_list_page, this, true);
        final View emptyView = view.findViewById(R.id.view_colour_list_page_view_empty);
        emptyView.setOnClickListener(internalOnClickListener);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.view_colour_list_page_view);
        mColourItemListWrapper = new FlavourColourItemListWrapper(recyclerView, this);
//        TODO: Finish populating this method and FlavourColourItemListWrapper
    }

    private void initInternalListener() {
    }

    private void initLifeCycleListener() {
    }

    //Used to catch events
    public interface Listener {
        //Called when the user requests emphasis on the add colour action
        //When the user touches an empty view
        void onEmphasisOnAddColourActionRequested();
    }
}
