package views;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mad1.blindeye.R;

import java.util.List;

import data.ColourItem;
import wrappers.ColourItemListWrapper;

public class FlavourColourItemListWrapper extends ColourItemListWrapper implements ColourItemAdapter.ColourItemAdapterListener {

    private final ColourItemAdapter mAdapter;
    private final int mSpanCount;

    public FlavourColourItemListWrapper(RecyclerView recyclerView, ColourItemListWrapperListener callback) {
        super(recyclerView, callback);
        mAdapter = new ColourItemAdapter(this);
        mSpanCount = recyclerView.getContext().getResources().getInteger(R.integer.horizontal_list_span);
    }

    @Override
    public void onColourItemClicked(@NonNull ColourItem colourItem, @NonNull View colourPreview) {
        mListener.onColourItemClicked(colourItem, colourPreview);
    }

    @Override
    public RecyclerView.Adapter recyclerViewInstallation() {
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(mRecyclerView.getContext(), mSpanCount);
        mRecyclerView.setLayoutManager(gridLayoutManager);
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
}
