package fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
//import android.support.v4.app.DialogFragment;

import androidx.fragment.app.DialogFragment;

import data.Palette;

//DialogFragment that'll be used to ask user to confirm deletion of a Palette
public class DeletePaletteDialogFragment extends DialogFragment {
    //key used to pass Palette that is to be deleted as an arg
    private static final String PALETTE_ARG = "DeletePaletteDialogFragment.Args.PALETTE_ARG";

    //new instance of a DeletePaletteDialogFragment to ask user to confirm Palette deletion
    private static DeletePaletteDialogFragment newFragmentInstance(Palette palette) {
        final DeletePaletteDialogFragment deletePaletteDialogFragment = new DeletePaletteDialogFragment();
        final Bundle bundle = new Bundle();
        bundle.putParcelable(PALETTE_ARG, palette);
        deletePaletteDialogFragment.setArguments(bundle);
        return deletePaletteDialogFragment;
    }

    //callback that is used when user confirms Palette deletion
    private Callback mCallback;

    //default constructor
    //every fragment must have a default constructor to be instantiated when restoring the state of an activity
    public DeletePaletteDialogFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof Callback) {
            mCallback = (Callback) activity;
        } else {
            throw new IllegalStateException("Activity needs to implement Callback");
        }
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        return super.onCreateDialog(savedInstanceState);
        final Bundle args = getArguments();

        if (!args.containsKey(PALETTE_ARG)) {
            throw new IllegalStateException("No args, Need to use newFragmentInstance()");
        }

        final Palette palette = args.getParcelable(PALETTE_ARG);
        return DeletePaletteDialogFragmentFlavour.dialogCreation(getContext(), palette, mCallback);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    //interface for callbacks
    public interface Callback {
        //called when Palette deletion is confirmed by user
        void onDeletionConfirmedP(@NonNull Palette palette);
    }
}
