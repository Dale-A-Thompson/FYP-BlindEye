package fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import data.ColourItem;

//DialogFragment that'll be used to ask user to confirm deletion of a ColourItem
public class DeleteColourDialogFragment extends DialogFragment {
    //key that'll be used to pass the ColourItem to be deleted as an arg
    private static final String COLOUR_ITEM_ARG = "DeleteColourDialogFragment.Args.COLOUR_ITEM_ARG";

    //new instance of a DeleteColourDialogFragment to ask user to confirm deletion of a ColourItem
    public static DeleteColourDialogFragment newFragmentInstance(ColourItem colourItem) {
        final DeleteColourDialogFragment deleteColourDialogFragment = new DeleteColourDialogFragment();
        final Bundle bundle = new Bundle();
        bundle.putParcelable(COLOUR_ITEM_ARG, colourItem);
        deleteColourDialogFragment.setArguments(bundle);
        return deleteColourDialogFragment;
    }

    //A callback used when a user confirms deletion of ColourItem
    private Callback mCallback;

    //default constructor
    //every fragment must have a default constructor to be instantiated when restoring the state of an activity
    public DeleteColourDialogFragment() {
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

        if (!args.containsKey(COLOUR_ITEM_ARG)) {
            throw new IllegalStateException("No args. Need to use newFragmentInstance()");
        }

        final ColourItem colourItem = args.getParcelable(COLOUR_ITEM_ARG);
        final Context context = getActivity();

        return DeleteColourDialogFragmentFlavour.dialogCreation(context, mCallback, colourItem);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    //interface for callbacks
    public interface Callback {
        //called when deletion of ColourItem is confirmed by user
        void onDeletionConfirmedC(@NonNull ColourItem colourItem);
    }
}
