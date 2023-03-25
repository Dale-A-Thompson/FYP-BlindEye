package views;

import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.mad1.blindeye.R;

import java.util.ArrayList;
import java.util.List;

import data.ColourItem;

//A RecyclerView Adapter that Adapts the ColourItems
public class ColourItemAdapter extends RecyclerView.Adapter<ColourItemAdapter.ColourItemHolder> {
    private final List<ColourItem> mColourItems;
    private final ColourItemAdapterListener mListener;

    ColourItemAdapter(ColourItemAdapterListener listener) {
        this.mListener = listener;
        this.mColourItems = new ArrayList<>();
    }

    @Override
    public ColourItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.colour_item_row, parent, false);
        return new ColourItemHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(ColourItemHolder holder, int position) {
        final ColourItem colourItem = mColourItems.get(position);
        holder.bind(colourItem);
    }

    @Override
    public int getItemCount() {
        return mColourItems.size();
    }

    void setColourItems(List<ColourItem> items) {
        mColourItems.clear();
        mColourItems.addAll(items);
        notifyDataSetChanged();
    }

    void addColourItems(List<ColourItem> colourAdded) {
        for (int i = colourAdded.size() - 1; i >= 0; i--) {
            mColourItems.add(0, colourAdded.get(i));
        }
    }

    //Interface for Listening to the ColourItemAdapter callbacks
    interface ColourItemAdapterListener {
        //called when a ColourItem is clicked
        void onColourItemClicked(@NonNull ColourItem colourItem, @NonNull View colourPreview);
    }

    //A ViewHolder associated with the layout colour_item_row
    public static class ColourItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //View to show a preview of the ColourItem
        private final View mColourPreview;

        //The underlying view
        private final View mUnderlyingView;

        //a ColourItemAdapterListener for callback(s)
        private final ColourItemAdapterListener mListener;

        //ColourItem bound to the ColourItemHolder
        private ColourItem mColourItem;

        public ColourItemHolder(View itemView, ColourItemAdapterListener listener) {
            super(itemView);
            mListener = listener;
            mUnderlyingView = itemView;
            mColourPreview = itemView.findViewById(R.id.colour_item_row_preview);
            itemView.setOnClickListener(this);
        }

        public void bind(ColourItem colourItem) {
            mColourItem = colourItem;
            mColourPreview.getBackground().setColorFilter(colourItem.getColour(), PorterDuff.Mode.MULTIPLY);
        }

        @Override
        public void onClick(View v) {
            if (v != mUnderlyingView) {
                throw new IllegalArgumentException("Unsupported view was clicked. Found: " + v);
            }

            if (mColourItem != null) {
                mListener.onColourItemClicked(mColourItem, mColourPreview);
            }
        }
    }
}
