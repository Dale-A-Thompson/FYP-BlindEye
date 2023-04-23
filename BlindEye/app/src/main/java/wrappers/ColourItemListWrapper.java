package wrappers;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import data.ColourItem;

//a wrapper of type view RecyclerView that displays the ColourItems
public abstract class ColourItemListWrapper {

    protected final RecyclerView mRecyclerView;
    protected final ColourItemListWrapperListener mListener;

    protected ColourItemListWrapper(RecyclerView recyclerView, ColourItemListWrapperListener colourItemListWrapperListener) {
        mRecyclerView = recyclerView;
        mListener = colourItemListWrapperListener;
    }

    //installing a Recycler View Layout Manager and an Adapter on the Recycler View
    //returning the adapter installed on the view ^
    public abstract RecyclerView.Adapter recyclerViewInstallation();

    //setting colour items
    public abstract void setColourItems(List<ColourItem> colourJustAdded);

    //add new colour item
    public void addColourItems(List<ColourItem> newColourAdded) {
        mRecyclerView.scrollToPosition(0);
    }

    //interface for the ColourItemListWrapper
    public interface ColourItemListWrapperListener {
        //called when ColourItem has been clicked on
        void onColourItemClicked(@NonNull ColourItem colourItem, @NonNull View colourPreview);
    }
}
