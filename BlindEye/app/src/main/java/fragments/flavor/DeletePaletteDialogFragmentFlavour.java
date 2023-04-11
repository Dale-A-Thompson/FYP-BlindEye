package fragments.flavor;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.mad1.blindeye.R;

import data.Palette;
import fragments.DeletePaletteDialogFragment;
import views.PaletteView;

//static class for flavour behaviour
public final class DeletePaletteDialogFragmentFlavour {
    //AlertDialog that asks the user to confirm deletion of a ColourItem
    public static AlertDialog dialogCreation(Context context, final Palette palette, final DeletePaletteDialogFragment.Callback callback) {
        final View view = LayoutInflater.from(context).inflate(R.layout.delete_palette_dialog_fragment, null);
        ((PaletteView) view.findViewById(R.id.delete_palette_prev_dialog_fragment)).setPalette(palette);
        ((PaletteView) view.findViewById(R.id.delete_palette_prev_small_dialog_fragment)).setPalette(palette);

        final AlertDialog alertDialog = new AlertDialog.Builder(context).setView(view)
                .setCancelable(true).create();

        view.findViewById(R.id.delete_palette_dialog_fragment_cancel_btn).setOnClickListener(v -> alertDialog.dismiss());

        view.findViewById(R.id.delete_palette_dialog_fragment_validate_btn).setOnClickListener(v -> {
            callback.onDeletionConfirmedP(palette);
            alertDialog.dismiss();
        });
        return alertDialog;
    }

    //non-instantiable class
    private DeletePaletteDialogFragmentFlavour() {

    }
}
