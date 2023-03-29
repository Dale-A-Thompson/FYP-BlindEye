package views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.mad1.blindeye.PaletteDetailActivity;
import com.mad1.blindeye.R;

import java.util.List;

import data.Palette;
import data.Palettes;
import wrappers.PaletteListWrapper;

public class PaletteListPage extends FrameLayout implements PaletteListWrapper.PaletteListWrapperListener {
    //OnPaletteChangeListener that'll get notified when user's palettes change
    private Palettes.OnPaletteChangeListener mOnPaletteChangeListener;

    //Listener that is used to catch event internally to avoid exposing this particular onClick callback
    private OnClickListener internalOnClickListener;

    //Listener created to catch the view events
    private Listener listener;

    public PaletteListPage(Context context) {
        super(context);
        init(context);
    }

    public PaletteListPage(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public PaletteListPage(Context context, AttributeSet attributeSet, int defStyleAttribute) {
        super(context, attributeSet, defStyleAttribute);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PaletteListPage(Context context, AttributeSet attributeSet, int defStyleAttribute, int defStyleRes) {
        super(context, attributeSet, defStyleAttribute, defStyleRes);
        init(context);
    }

    //Setting the Listener used to catch the view events
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    //Initializing the palette list page
    //Has to be called in each constructor above once
    private void init(Context context) {
        final View view = View.inflate(context, R.layout.palette_list_page, this);

        initializeInternalListener();

        final View emptyView = view.findViewById(R.id.empty_palette_list_page_view);
        emptyView.setOnClickListener(internalOnClickListener);

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.palette_list_page_view);
        final FlavourPaletteListWrapper flavourPaletteListWrapper = FlavourPaletteListWrapper.create(recyclerView, this);
        final PaletteListWrapper.Adapter adapter = flavourPaletteListWrapper.installRecyclerView();

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                //ternary operator if statement
                //TODO: Add this comment to every instance of a ternary IF, something briefly learned/used during placement
                emptyView.setVisibility(adapter.getItemCount() == 0 ? VISIBLE : GONE);
            }
        });

        adapter.setPalettes(Palettes.getSavedPalette(context));
        mOnPaletteChangeListener = new Palettes.OnPaletteChangeListener() {
            @Override
            public void onChangedColourItem(List<Palette> palettes) {
                adapter.setPalettes(palettes);
            }
        };

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //Register OnPaletteChangeListener for notifications for when user creates or deletes new palette
        Palettes.registerListener(getContext(), mOnPaletteChangeListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //Unregister OnPaletteChangeListener, notifications are no longer needed
        Palettes.unregisterListener(getContext(), mOnPaletteChangeListener);
    }

    @Override
    public void onPaletteClicked(@NonNull Palette palette, @NonNull View palettePreview) {
//        TODO: PaletteDetailActivity needs to be populated to populate this particular method
    }

    private void initializeInternalListener() {
        internalOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onPaletteCreationRequestValues();
                }
            }
        };
    }

    //Listener created to catch the view events
    public interface Listener {
        //This is called when the user requested values on the palette creation action
        //When the user touches an empty view
        void onPaletteCreationRequestValues();
    }
}
