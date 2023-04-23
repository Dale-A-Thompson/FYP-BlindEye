package views;

import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad1.blindeye.R;

import java.util.ArrayList;
import java.util.List;

import data.ColourItem;
import utils.BackgroundUtils;

//A RecyclerView Adapter that Adapts the ColourItems into the res/layout/colour_item_row.xml
public class ColourItemAdapter extends RecyclerView.Adapter<ColourItemAdapter.ColourItemHolder> {
    private final List<ColourItem> mColourItems;
    private final ColourItemAdapterListener mListener;

    public ColourItemAdapter(ColourItemAdapterListener listener) {
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

    //setting colour items
    public void setColourItems(List<ColourItem> items) {
        mColourItems.clear();
        mColourItems.addAll(items);
        notifyDataSetChanged();
    }

    //adding colour items
    public void addColourItems(List<ColourItem> colourAdded) {
        for (int i = colourAdded.size() - 1; i >= 0; i--) {
            mColourItems.add(0, colourAdded.get(i));
        }
    }

    //Interface for Listening to the ColourItemAdapter callbacks
    public interface ColourItemAdapterListener {
        //called when a ColourItem is clicked
        void onColourItemClicked(@NonNull ColourItem colourItem, @NonNull View colourPreview);

        //called when a ColourItem is clicked on and held
        void onColourItemClickedLong(@NonNull ColourItem colourItem);
    }

    //A ViewHolder associated with the res/layout/colour_item_row.xml
    public static class ColourItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        //View to show a preview of the ColourItem
        private final View mColourPreview;

        //The underlying view
        private final View mUnderlyingView;

        //text to display code(s) for colour
        private final TextView mColourText;

        //a ColourItemAdapterListener for callback(s)
        private final ColourItemAdapterListener mListener;

        //ColourItem bound to the ColourItemHolder
        private ColourItem mColourItem;

        public ColourItemHolder(View itemView, ColourItemAdapterListener listener) {
            super(itemView);
            mListener = listener;
            mUnderlyingView = itemView;
            mColourPreview = itemView.findViewById(R.id.colour_item_row_preview);
            mColourText = itemView.findViewById(R.id.colour_item_row_name_text);
            BackgroundUtils.setBackground(mColourPreview, new ColourBlobDrawable(itemView.getContext()));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(ColourItem colourItem) {
            mColourItem = colourItem;
            mColourPreview.getBackground().setColorFilter(colourItem.getColour(), PorterDuff.Mode.MULTIPLY);
            if (!TextUtils.isEmpty(colourItem.getName())) {
                mColourText.setText(colourItem.getName());
            } else {
                mColourText.setText(colourItem.getHexString());
            }
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

        @Override
        public boolean onLongClick(View v) {
            if (v != mUnderlyingView || mColourItem == null) {
                return false;
            }

            mListener.onColourItemClickedLong(mColourItem);
            return true;
        }
    }
}
