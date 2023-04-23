package wrappers;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import data.Palette;

//a wrapper of type view RecyclerView that displays the Palettes
public abstract class PaletteListWrapper {

    protected final RecyclerView mRecyclerView;
    protected final PaletteListWrapperListener mListener;
    protected final Adapter mAdapter;
    protected final RecyclerView.LayoutManager mLayoutManager;

    protected PaletteListWrapper(RecyclerView recyclerView, PaletteListWrapperListener listener, Adapter adapter, RecyclerView.LayoutManager layoutManager) {
        mRecyclerView = recyclerView;
        mListener = listener;
        mAdapter = adapter;
        mLayoutManager = layoutManager;
    }

    //installing a LayoutManager and an Adapter on the RecyclerView
    //Also returning the Adapter installed on the RecyclerView
    public PaletteListWrapper.Adapter installRecyclerView() {
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        return mAdapter;
    }

    //Interface for listening to PaletteListWrapper
    public interface PaletteListWrapperListener {
        //Called when a Palette has been clicked
        void onPaletteClicked(@NonNull Palette palette, @NonNull View palettePreview);
    }

    //An Adapter backed by a List of Palettes
    public static abstract class Adapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

        private final List<Palette> mPalettes;
        protected PaletteListWrapperListener mListener;

        protected Adapter(PaletteListWrapperListener listener) {
            mListener = listener;
            mPalettes = new ArrayList<>();
        }

        @Override
        public int getItemCount() {
            return mPalettes.size();
        }

        //Setting the Palettes
        public void setPalettes(List<Palette> palettes) {
            mPalettes.clear();
            mPalettes.addAll(palettes);
            notifyDataSetChanged();
        }

        //Get a Palette at a given position
        protected Palette get(int pos) {
            return mPalettes.get(pos);
        }
    }
}
