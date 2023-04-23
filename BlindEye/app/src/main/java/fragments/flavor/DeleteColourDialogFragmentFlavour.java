package fragments.flavor;

//import android.app.AlertDialog;

import androidx.appcompat.app.AlertDialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;

import com.mad1.blindeye.R;

import data.ColourItem;
import fragments.DeleteColourDialogFragment;

//static class for flavour behaviour
public final class DeleteColourDialogFragmentFlavour {

    //AlertDialog that asks the user to confirm deletion of a ColourItem
    public static Dialog dialogCreation(Context context, final DeleteColourDialogFragment.Callback callback, final ColourItem colourItem) {
        final View view = LayoutInflater.from(context).inflate(R.layout.delete_colour_dialog_fragment, null);
        final int colour = colourItem.getColour();

        view.findViewById(R.id.delete_colour_prev_dialog_fragment).setBackgroundColor(colour);
        view.findViewById(R.id.delete_colour_prev_blob)
                .getBackground().mutate().setColorFilter(colour, PorterDuff.Mode.SRC);

        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setCancelable(true).setView(view).create();

        view.findViewById(R.id.delete_colour_dialog_fragment_validate_btn).setOnClickListener(v -> {
            callback.onDeletionConfirmedC(colourItem);
            alertDialog.dismiss();
        });

        view.findViewById(R.id.delete_colour_dialog_fragment_cancel_btn).setOnClickListener(v -> alertDialog.dismiss());

        return alertDialog;
    }

    //non-instantiable class
    private DeleteColourDialogFragmentFlavour() {
    }

}
