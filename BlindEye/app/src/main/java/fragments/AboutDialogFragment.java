package fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mad1.blindeye.R;

import utils.VersionUtil;

//DialogFragment that'll be used for displaying information about the app
public class AboutDialogFragment extends DialogFragment {
    //instance of AboutDialogFragment
    public static AboutDialogFragment newFragmentInstance() {
        return new AboutDialogFragment();
    }

    //default constructor
    //every fragment must have a default constructor to be instantiated when restoring the state of an activity
    public AboutDialogFragment() {

    }

    @SuppressLint("StringFormatInvalid")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        return super.onCreateDialog(savedInstanceState);
        final Activity activity = getActivity();
        final View dView = LayoutInflater.from(activity)
                .inflate(R.layout.about_fragment, null);

        ((TextView) dView.findViewById(R.id.fragment_dialog_about_name))
                .setText(getString(R.string.about_dialog_name,
                        VersionUtil.getNameOfVersion(activity)));

//        ((TextView) dView.findViewById(R.id.fragment_dialog_about_name))
//                .setText(R.string.about_dialog_name);

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setView(dView)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, null);

        return builder.create();
    }
}
