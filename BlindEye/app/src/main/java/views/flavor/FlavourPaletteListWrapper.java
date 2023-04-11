package views.flavor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mad1.blindeye.R;

import data.Palette;
import views.PaletteView;
import wrappers.PaletteListWrapper;

//Flavour PaletteListWrapper
public class FlavourPaletteListWrapper extends PaletteListWrapper {

    //Creating a FlavourPaletteListWrapper
    public static FlavourPaletteListWrapper create(RecyclerView recyclerView, PaletteListWrapperListener paletteListWrapperListener) {
        final PaletteAdapter paletteAdapter = new PaletteAdapter(paletteListWrapperListener);
//        int spanCount = recyclerView.getContext().getResources().getInteger(R.integer.horizontal_list_span);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        return new FlavourPaletteListWrapper(recyclerView, paletteListWrapperListener, paletteAdapter, linearLayoutManager);
    }

    protected FlavourPaletteListWrapper(RecyclerView recyclerView, PaletteListWrapperListener paletteListWrapperListener, Adapter adapter, RecyclerView.LayoutManager layoutManager) {
        super(recyclerView, paletteListWrapperListener, adapter, layoutManager);
    }

    //An Adapter of the PaletteHolder inner class
    private static class PaletteAdapter extends Adapter<PaletteHolder> {
        protected PaletteAdapter(PaletteListWrapperListener paletteListWrapperListener) {
            super(paletteListWrapperListener);
        }

        @NonNull
        @Override
        public PaletteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.palette_row, parent, false);
            return new PaletteHolder(view, mListener);
        }

        @Override
        public void onBindViewHolder(PaletteHolder holder, int position) {
            final Palette palette = get(position);
            holder.bind(palette);
        }
    }

    //A ViewHolder that is used by the PaletteAdapter
    private static class PaletteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final View mUnderLyingView;
        private final PaletteListWrapperListener mListener;
        private final PaletteView mPaletteThumbnail;
        private final TextView mPaletteName;
        private Palette mPalette;

        public PaletteHolder(View view, PaletteListWrapperListener listener) {
            super(view);
            mUnderLyingView = view;
            mListener = listener;
            mPaletteThumbnail = (PaletteView) view.findViewById(R.id.palette_row_thumbnail);
            mPaletteName = (TextView) view.findViewById(R.id.palette_row_name_text);

            view.setOnClickListener(this);
        }

        public void bind(Palette palette) {
            mPalette = palette;
            mPaletteThumbnail.setPalette(palette);
            mPaletteName.setText(palette.getName());
        }

        @Override
        public void onClick(View v) {
            if (v != mUnderLyingView) {
                throw new IllegalArgumentException("View Unsupported. Found: " + v);
            }
            if (mPalette != null) {
                mListener.onPaletteClicked(mPalette, mPaletteThumbnail);
            }
        }
    }
}
